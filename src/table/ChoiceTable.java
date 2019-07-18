package table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import entity.Choice;
import entity.Course;
import index.Index;

//import entity.Choice;
//import table.ChoiceTable;
//import table.CourseTable;
//import table.MyThread2;
//import table.MyThread4;
//import table.StudentTable;

public class ChoiceTable extends Table{

	//选课信息包括学生表和课程表
	private StudentTable studentTable;
	private CourseTable courseTable;
	
	/**
	 *  ChoiceTable构造函数，调用父类Table构造函数
	 * 
	 * @param studentTable 全部学生表
	 * @param courseTable全部课程表
	 */
	public ChoiceTable(StudentTable studentTable,CourseTable courseTable) {
		super("choice");
		this.studentTable = studentTable;
		this.courseTable = courseTable;
	}
	

	/**
	 * 选课
	 * 将全部信息存在字符串data中，格式为：学生id~课程id~选课时间
	 *判断是不是已经选过 
	 * 判断学生和课程是不是都存在
	 * 读取课程信息，课余量减一
	 * 用*补足64位写回
	 * 写选课信息文件，写入data
	 * 选课信息的indexTree中，保存结构为:<String,Index> = <学生id-课程id,存储信息index>
	 * 
	 * @param choice 一个选择实体，即包括：学生id，课程id，选课时间
	 * @return 一个字符串，是否选课成功
	 */
	public String insertChoice(Choice choice)
	{
		//本表要存的全部信息
		String data = choice.getStudent_id() + "~" + choice.getCourse_id() + "~" + choice.getTime();
		
		if(data.getBytes().length > 64)
		{
			return "Too long";
		}
		
		indexLock.lock();
		if(indexTree.getTreeMap().containsKey(choice.getStudent_id() + "-" + choice.getCourse_id()))
		{
			indexLock.unlock();
			return "Choice has exestied";
		}
		indexLock.unlock();
		
		studentTable.indexLock.lock();
		Index studentindex = new Index();
		studentindex = studentTable.indexTree.getTreeMap().get(choice.getStudent_id());
		if(studentindex == null)
		{
			studentTable.indexLock.unlock();
            return "Student not exesit ";
		}
		studentTable.indexLock.unlock();
		
		boolean isExist = courseTable.courseTree.containsKey(choice.getCourse_id());
		if(!isExist)
			return "Course not exesit";
		
		ReentrantLock filelock = studentTable.FileLocks.get(studentindex.getFileNum());
		filelock.lock();
		courseTable.fileLock.lock();//读coursetable树时不希望别人读写
		try {	
			//修改课程信息
			String name = courseTable.courseTree.get(choice.getCourse_id()).getName();
			int totalSize = Integer.valueOf(courseTable.courseTree.get(choice.getCourse_id()).getTotalSize());
			int remainSize = Integer.valueOf(courseTable.courseTree.get(choice.getCourse_id()).getRemainSize());
			if(remainSize == 0)
			{
				return "course fulled";
			}
			
			remainSize--;
			Course UpdataCourse = new Course(choice.getCourse_id(), name, totalSize, remainSize);
			try {
				String re=courseTable.updateCourse(UpdataCourse);
				System.out.println("课余量更新情况:" +  re);
				if(re.equals("course not found"))
				{
					return "course update fail";
				}
			} catch (Exception e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
				return "course update fail";
			}
			
			//写选课信息文件
			int myNum = lineNum.getAndAdd(1);
			int myFile = myNum / lineNumConfine + 1;
			
			if (!FileLocks.containsKey(myFile)) {
                synchronized (FileLocks.getClass()) {
                    if (!FileLocks.containsKey(myFile)) {
                        FileLocks.put(myFile, new ReentrantLock(true));
                    }
                }
            }
			
			FileLocks.get(myFile).lock();
			
			int distance = 64 - data.getBytes().length;
			StringBuilder stringBuilder = new StringBuilder(data);
			for(int i = 0;i < distance ;i++)
			{
				stringBuilder.append("*");
			}
			
			File file = new File(Folder + "/data",myFile + ".data");
			file.getParentFile().mkdirs();
			try (
					FileWriter fw = new FileWriter(file, true);
                PrintWriter pw = new PrintWriter(fw)
             ){
				pw.println(stringBuilder.toString());
			} catch (Exception e) {
				e.printStackTrace();
				FileLocks.get(myFile).unlock();
				return "information insert exception";
			}
			indexLock.lock();
			indexTree.putIndex(choice.getStudent_id() + "-" + choice.getCourse_id(), file.getAbsolutePath(), FileLineNum(file), myFile);
			writeIndex();
			indexLock.unlock();
			FileLocks.get(myFile).unlock();
			
			return "success";
			
		}finally {
			courseTable.fileLock.unlock();
			filelock.unlock();
		}
	}
	
	
	/**
	 * 退课
	 * 获得选课信息的index
	 * 判断选课信息是否存在，课程是否存在
	 * 读取课程信息，课余量加一
	 * 读取选课信息，最后加上"+"代表删除
	 * 补*至长度为64写回
	 * 
	 * @param choice 一个选择实体，即包括：学生id，课程id，选课时间
	 * @return 
	 */
	public String deleteChoice(Choice choice)
	{
		indexLock.lock();
		Index index = new Index();
		index = indexTree.getTreeMap().get(choice.getStudent_id() + "-" + choice.getCourse_id());
		
		if(index == null)
		{
			indexLock.unlock();
			return "choice not exist";
		}
		indexLock.unlock();
		
		ReentrantLock fileLock = FileLocks.get(index.getFileNum());
		boolean isExist = courseTable.courseTree.containsKey(choice.getCourse_id());
		if(!isExist)
			return "Course not exesit";
		
		courseTable.fileLock.lock();
		fileLock.lock();
		try {
			String name = courseTable.courseTree.get(choice.getCourse_id()).getName();
			int totalSize = Integer.valueOf(courseTable.courseTree.get(choice.getCourse_id()).getTotalSize());
			int remainSize = Integer.valueOf(courseTable.courseTree.get(choice.getCourse_id()).getRemainSize());
			
			remainSize++;
			Course UpdataCourse = new Course(choice.getCourse_id(), name, totalSize, remainSize);
			try {
				String re = courseTable.updateCourse(UpdataCourse);
				System.out.println("课余量更新情况:" +  re);
				if(re.equals("course not found"))
				{
					return "course update fail";
				}
			} catch (Exception e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
				return "course update fail";
			}
            
            //修改课程表信息
         
            indexLock.lock();
            Index fileIndex = indexTree.getTreeMap().get(choice.getStudent_id() + "-" + choice.getCourse_id());
            indexLock.unlock();
            
            String srcData = "";
            //读取选课表信息修改并存文件
             try (
                    RandomAccessFile raf = new RandomAccessFile(new File(fileIndex.getFilePath()), "rw");
            ) {
                raf.skipBytes(66 * (fileIndex.getLineNum() - 1));
                byte[] b = new byte[64];
                raf.read(b);
                srcData = new String(b);
            } catch (IOException e) {
                e.printStackTrace();
                return "choice file read exception";
            }
             srcData = srcData.replace("*","") + "+";
             int distance = 64 - srcData.getBytes().length;
             StringBuilder stringBuilder = new StringBuilder(srcData);
             for (int i = 0; i < distance; i++) {
                 stringBuilder.append("*");
             }
             try (
                     RandomAccessFile raf = new RandomAccessFile(new File(fileIndex.getFilePath()), "rw");
             ) {
                 raf.skipBytes(66 * (fileIndex.getLineNum() - 1));
                 raf.write(stringBuilder.toString().getBytes());
             } catch (IOException e) {
                 e.printStackTrace();
                 return "choice file write exception";
             }
             
             indexLock.lock();
             indexTree.getTreeMap().remove(choice.getStudent_id() + "-" + choice.getCourse_id());
             writeIndex();
             indexLock.unlock();
             
             return "success";
		} finally {
			fileLock.unlock();
			courseTable.fileLock.unlock();
		}
	}
	
	
	/**
	 * 获得一条选课信息
	 * 调用父类的getRecord()函数，spilt出信息，构造一个Choice并将其返回
	 * 
	 * @param StudentId 学生id，学生表的主码
	 * @param CourseId 课程id，课程表的主码
	 * @return Choice 一条选课记录
	 */
	public Choice getChoice(String StudentId,String CourseId)
	{
		String data = getRecord(StudentId + "-" + CourseId);
		if(data == null)
		{
			return null;
		}
		String[] datas = data.split("~");
		Choice choice = new Choice(datas[0], datas[1],datas[2]);
		
		return choice;
	}
	
	public String getchoiceByStudentid(String Sid){
		String s="";
		StringBuilder stringbuilder = new StringBuilder(s);
		for(File f:dataFileSet){
			int fileNum = Integer.valueOf(f.getName().split(".data")[0]);
			ReentrantLock fLock=FileLocks.get(fileNum);
			fLock.lock();
			try(
					FileReader fr = new FileReader(f);
					BufferedReader br = new BufferedReader(fr);
					){
				String line = null;
				while((line = br.readLine()) != null)
				{
					if(!line.contains("+"))
					{
						String[] datas = line.replace("*", "").split("~");
						if(datas[0].equals(Sid)){
							stringbuilder.append(line.replace("*", ""));
							stringbuilder.append("\n");
						}
					}
				}
			}
			catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			fLock.unlock();
		}
		String result=stringbuilder.toString();
		if(result.equals("")||result==null){
			return "not found";
		}
		return result;
	}
	
	public String getchoiceByCourseid(String Cid){
		String s="";
		StringBuilder stringbuilder = new StringBuilder(s);
		for(File f:dataFileSet){
			int fileNum = Integer.valueOf(f.getName().split(".data")[0]);
			ReentrantLock fLock=FileLocks.get(fileNum);
			fLock.lock();
			try(
					FileReader fr = new FileReader(f);
					BufferedReader br = new BufferedReader(fr);
					){
				String line = null;
				while((line = br.readLine()) != null)
				{
					if(!line.contains("+"))
					{
						String[] datas = line.replace("*", "").split("~");
						if(datas[1].equals(Cid)){
							stringbuilder.append(line.replace("*", ""));
							stringbuilder.append("\n");
						}
					}
				}
			}
			catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			fLock.unlock();
		}
		String result=stringbuilder.toString();
		if(result.equals("")||result==null){
			return "not found";
		}
		return result;
	}

	/**
	 * 测试用例
	 * @param args
	 */
	 public static void main(String[] args) {
	        StudentTable studentTable = new StudentTable();
	        CourseTable courseTable = new CourseTable();
//	        ChoiceTable table = new ChoiceTable(studentTable,courseTable);
//	        System.out.println(table.insertChoice(new Choice("Thread-01561971925369","Thread-11561971729722","3")));
	        ChoiceTable choiceTable = new ChoiceTable(studentTable,courseTable);

//	        Choice choice = new Choice("123","2","timexxx");
//	        System.out.println(choiceTable.insertChoice(choice));
	        //Choice choice = new Choice("1234","2","timexxx");
	        //System.out.println(choiceTable.insertChoice(new Choice("1234","2","timexxx")));
	        //System.out.println(choiceTable.deleteChoice(new Choice("1234","2","timexxx")));
	        //System.out.println(choiceTable.deleteChoice(new Choice("1234","2","timexxx")));
	       // choice = choiceTable.getChoice("123", "2");
	        //System.out.println(choice);
//	        for(int i = 0;i < 31 ;i++)
//	        {
//	        	System.out.println(choiceTable.insertChoice(new Choice(i + "", (i%10)+"")));
//	        }
//	        for(int i = 1;i < 6;i++)
//	        {
//	        	System.out.println(choiceTable.deleteChoice(new Choice(i + "", i + "")));
//	        }
//	        System.out.println(choiceTable.deleteChoice(choice));

//	        MyThread2 myThread = new MyThread2(choiceTable);
//	        MyThread4 myThread4 = new MyThread4(choiceTable);
//	        List<Thread> threads = new ArrayList<>();
//	        for ( int i=0; i<30;i++)
//	        {
//	            threads.add(new Thread(myThread));
//	        }
//
//	        for(int i=0; i< 30;i++)
//	        {
//	            threads.get(i).start();
//	        }
//
//	        List<Thread> thread4s = new ArrayList<>();
//	        for ( int i=0; i<30;i++)
//	        {
//	            thread4s.add(new Thread(myThread4));
//	        }
//
//	        for(int i=0; i< 30;i++)
//	        {
//	            thread4s.get(i).start();
//	        }

	    }
}
