package table;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import index.Index;
import index.IndexTree;

//import table.ChoiceTable;
//import table.CourseTable;
//import table.StudentTable;

public class Table implements Serializable {
	private String name;//表名
	protected File Folder;//表所在的文件夹
	protected File IndexFile;//索引文件
	protected IndexTree indexTree;//主码和位置的映射
	protected AtomicInteger lineNum;//线程安全的具有原子性的能返回int类型的对象
	protected Map<Integer, ReentrantLock> FileLocks;//锁文件？
	protected ReentrantLock indexLock;//锁数据,被锁的不能修改,即我改你不能改
	protected CopyOnWriteArraySet<File> dataFileSet;//线程安全，数据文件列表，不可重复的数组，File类型
	
	protected static int lineNumConfine = 10000;//一个文件行数限制
	private static final long serialVersionUID = -1105157846099295625L;
	/**
	 * 构造函数
	 * 
	 * @param name
	 */
	public Table(String name)
	{
		this.name = name;
		this.Folder = new File("dir" + "/" + name);
		this.IndexFile = new File(Folder, this.name + ".index");
		this.indexTree = new IndexTree();
		this.FileLocks = new ConcurrentHashMap<>();
		this.indexLock = new ReentrantLock();
		this.lineNum = new AtomicInteger(0);
		this.dataFileSet = new CopyOnWriteArraySet<>();
		InitSystem();
		System.out.println("The record is " + lineNum + " lines");
		
	}
	
	
	/**
	 * 初始化系统
	 */
	private synchronized void InitSystem() {
		File[] dataFiles = new File(Folder,"data").listFiles();
		if(dataFiles != null && dataFiles.length != 0)
		{
			for(int i = 1;i <= dataFiles.length;i++)
			{
				File dataFile = new File(Folder + "/data" ,i + ".data");
				dataFileSet.add(dataFile);
				FileLocks.put(i, new ReentrantLock(true));
				lineNum.addAndGet(FileLineNum(dataFile));
			}
		}else {
			lineNum.set(0);
		}
		
		if(IndexFile.exists())
		{
			readIndex();
			System.out.println("读取索引文件...\r\n" + indexTree.toString());
		}else {
			buildIndex();
			writeIndex();
		}
	}

	/**
     * 将索引对象从索引文件读取
     */
	public void readIndex()
	{
		if (!IndexFile.exists()) {
            return;
        }

        try(
        		FileInputStream fis = new FileInputStream(IndexFile);
        		ObjectInputStream ois = new ObjectInputStream(fis)
        )
        {
			indexTree = (IndexTree)ois.readObject();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();

		}
        
	}
	
	/**
	 * 插入数据到文件，超过行数限制转到下一个文件
	 * 
	 * @param data 存储一条记录全部信息的字符串
	 * @param key 主码
	 * @return
	 */
	public String InsertToFile(String data,String key)
	{
		int myNum = lineNum.addAndGet(1);
		int myFile = myNum / lineNumConfine + 1;
		
		if(!FileLocks.containsKey(myFile))
		{
			synchronized (FileLocks.getClass()) {
				if(!FileLocks.containsKey(myFile))
				{
					FileLocks.put(myFile, new ReentrantLock(true));
				}
			}
		}
		
		FileLocks.get(myFile).lock();
		indexLock.lock();
		
		try {
			if(indexTree.getTreeMap().containsKey(key))
			{
				lineNum.addAndGet(-1);
				return "key exists";
			}
			
			File file = new File(Folder + "/data",myFile + ".data");
			file.getParentFile().mkdirs();
			
			
			try (
                    FileWriter fw = new FileWriter(file, true);
                    PrintWriter pw = new PrintWriter(fw)
            ) {
                pw.println(data);
            } catch (IOException e) {
                e.printStackTrace();
                return "write exception";
            }
            indexTree.putIndex(key, file.getAbsolutePath(), FileLineNum(file), myFile);
            writeIndex();

            return "sucess";
		} finally {
			// TODO: handle finally clause
			FileLocks.get(myFile).unlock();
            indexLock.unlock();
		}
	}
	
	/**
	 * 读取文件行数
	 * 
	 * @param file 一个文件
	 * @return 文件行数
	 */
	public int FileLineNum(File file)
	{
		if(!file.exists())
        {
            return 0;
        }
		int num = 0;
		try (
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr)
        ) {
            while (null != br.readLine()) {
                num++;
            }
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return num;
	}
	
	/**
	 * 将索引对象写入索引文件
	 */
	public void writeIndex()
	{
		try (
				FileOutputStream fos = new FileOutputStream(IndexFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(indexTree);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
    /**
     * 为每个属性建立索引树，如果此属性值为[NULL]索引树将排除此条字段
     */
	public void buildIndex()
	{
		indexTree = new IndexTree();
		File[] dataFiles = new File(Folder,"data").listFiles();//每个文件
		if(dataFiles == null || dataFiles.length == 0)
		{
			return;
		}
		
		for(File dataFile : dataFiles)
		{
			List<Map<String,String>> datas = readDatasAndLineNum(dataFile);//每个元组的容器
			
			int fileNum = Integer.valueOf(dataFile.getAbsolutePath().split("dir/" + name + "/.data/")[1].split(".data")[0]);//?
			for(Map<String, String> data : datas)//对于每个数据字段	
			{
				int lineNum = Integer.valueOf(data.get("[lineNum]"));
				String priKey = data.get("[prikey]");
				if(name == "choice")
				{
					priKey = data.get("[prikey]") + "-" + data.get("[seckey]");
				}
				System.out.println("key:" + priKey);
				indexTree.putIndex(priKey, dataFile.getAbsolutePath(),lineNum , fileNum);
			}
			System.out.println(fileNum + "----" + dataFile.getAbsolutePath());
		}
	}

	/**
	 * 读取指定文件的所有数据和行号
	 * 放进一个list中，list内容是一个个map<String,String>，每一个map包括
	 * @param dataFile
	 * @return Map<String,String>类型的list,是一个ArrayList
	 */
	private List<Map<String,String>> readDatasAndLineNum(File dataFile)
	{
		List<Map<String,String>> dataMapList = new ArrayList<>();
		
		try(
				FileReader fr = new FileReader(dataFile);
				BufferedReader br = new BufferedReader(fr);
		) 
		{
			String line = null;
			long lineNum = 1;//int?
			while((line = br.readLine()) != null)
			{
				Map<String, String> dataMap = new LinkedHashMap<>();
				if(!line.contains("+"))
				{
					String[] datas = line.replace("*", "").split("~");
					dataMap.put("[prikey]", datas[0]);
					dataMap.put("[seckey]", datas[1]);
					dataMap.put("[lineNum]", String.valueOf(lineNum));
					dataMapList.add(dataMap);
				}
				lineNum++;
			}
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataMapList;
	}
	
	/**
	 * 获取一条记录
	 * 
	 * @param key 主码
	 * @return 字符串 包括全部信息的字符串
	 */
	public String getRecord(String key)
	{
		indexLock.lock();
		Index index = new Index();
		index = indexTree.getTreeMap().get(key);
		if(index == null)
		{
			indexLock.unlock();
			return "not found";
		}
		indexLock.unlock();
		
		ReentrantLock lock = FileLocks.get(index.getFileNum());
		if(lock == null)
		{
			return "not found";
		}
		
		lock.lock();
		indexLock.lock();
		index = indexTree.getTreeMap().get(key);
		if(index == null)
		{
			indexLock.unlock();
			lock.unlock();
			return "not found";
		}
		indexLock.unlock();
		
		if(FileLocks.get(index.getFileNum()) == null)
		{
			lock.unlock();
			return "not found";
		}
		
		String srcData = "";
		try(
				RandomAccessFile raf = new RandomAccessFile(new File(index.getFilePath()), "rw");
		){
			raf.skipBytes(65 * (index.getLineNum() - 1));
			byte[] b = new byte[64];
			raf.read(b);
			srcData = new String(b);
		} catch (Exception e) {
			e.printStackTrace();
			lock.unlock();
			System.out.println("file read exception");
			return "read file exception";
			// TODO: handle exception
		}
		lock.unlock();
		if(srcData.contains("+"))
		{
			return "not found";
		}
		return srcData.replace("*", "");
	}
	
	/**
	 * 用于测试
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
        StudentTable studentTable = new StudentTable();
        CourseTable courseTable = new CourseTable();
        ChoiceTable choiceTable = new ChoiceTable(studentTable, courseTable);
        //System.out.println( choiceTable.getChoice("1","1"));

        //System.out.println(courseTable.deleteCourse("2",choiceTable));
    }
	
	
}
