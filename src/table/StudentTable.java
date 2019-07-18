package table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import entity.Course;
import entity.Student;
import index.Index;
import thread.Thread_InsertChoice;

//import entity.Student;
//import table.StudentTable;

public class StudentTable extends Table implements Tool_StudentAndCourse{

	/**
	 * StudentTable构造函数，调用父类Table构造函数
	 */
	public StudentTable() {
		super("student");//调用父类构造函数
		System.out.println("学生表初始化完成!");
	}
	
	/**
	 * 实现接口插入学生
	 */
	public String insert(Object object)
	{
		Student student = (Student)object;
		String result = InsertStudent(student);
		return result;
	}
	/**
	 * 插入学生
	 * 文件存储顺序是：id~班级~名字~性别，后面补*至长度为64
	 * 用StringBuilder构造要存储的字符串，调用父类插入文件函数InsertToFile()
	 * 
	 * @param studnet 一个学生实体
	 * @return 一个字符串，是否插入成功
	 */
	public String InsertStudent(Student studnet)
	{
		//存储顺序：id~班级~名字~性别
		String data = studnet.getid() + "~" + studnet.getStudentClass() + "~" + studnet.getName() + "~" + studnet.getSex();
		
		if(data.getBytes().length > 64)
		{
			return "too long";
		}
		int distance = 64 - data.getBytes().length;
		StringBuilder stringBuilder = new StringBuilder(data);	
		for(int i = 0;i < distance;i++)
		{
			stringBuilder.append("*");
		}
		return InsertToFile(stringBuilder.toString(), studnet.getid());
	}
	
	/**
	 * 实现查找接口
	 */
	public String get(String key)
	{
		Student student = getStudent(key);
		return student.toString();
	}
	
	/**
	 * 获得学生信息
	 * 调用父类函数getRecord()
	 * 
	 * @param key 学生学号
	 * @return Student 一个学生实体
	 */
	public Student getStudent(String key)
	{
		String data = getRecord(key);
		if(data == null)
		{
			return null;
		}
		String[] datas = data.split("~");
		Student student = new Student(datas[0], datas[2], datas[1], (datas[3] == "true")?true:false);
		
		return student;
	}
	
	
	public String update(Object object) {
		Student student = (Student)object;
		String result = update(student);
		return result;
	}
	/**
	 * 更新学生信息
	 * 首先找到学生对应的index
	 * 编辑学生信息字符串id~班级~名字~性别
	 * 填满*使得长度为64
	 * 写回
	 * 
	 * @param student
	 * @return 一个字符串，是否update成功 
	 */
	public String UpdateStudent(Student student)
	{
		indexLock.lock();
		Index index = indexTree.getTreeMap().get(student.getid());//学号对应的index
		
		if(index == null)//根本找不到这个学生
		{
			indexLock.unlock();
			return "student not exist";
		}
		indexLock.unlock();
		
		ReentrantLock lock = FileLocks.get(index.getFileNum());
		lock.lock();
//		if (indexTree.getTreeMap().get(student.getid()) == null){//？？
//			lock.unlock();
//			return "学生不存在";
//		}
		
		String data = student.getid() + "~" + student.getStudentClass() + "~" + student.getName() + "~" + student.getSex();
		int distance = 64 - data.getBytes().length;
		StringBuilder stringBuilder = new StringBuilder(data);
		//不够64补充*填满
		for(int i = 0;i < distance ;i++)
		{
			stringBuilder.append("*");
		}
		//stringBuilder.append("\n");
		try(
				RandomAccessFile raf = new RandomAccessFile(new File(index.getFilePath()), "rw");
		) {
			raf.skipBytes(66 * (index.getLineNum() - 1));
			raf.write(stringBuilder.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			lock.unlock();
			return "write file exception";
		}
		
		lock.unlock();
		return "success";
	}
	
	public String delete(String key,Object object)
	{
		ChoiceTable choiceTable = (ChoiceTable)object;
		String result = deleteStudent(key, choiceTable);
		return result;
	}
	/**
	 * 删除学生记录
	 * 注意：按照最后一次课的精神，仅标记删除，不真正删除
	 * 找到index
	 * 将数据读出到字符串srcData
	 * 删除srcData中的*，加上+号表示删除
	 * 补足长度64写回文件
	 * 
	 * @param id 学生学号
	 * @param choiceTable 学生选课表
	 * @return 一个字符串，表示删除情况
	 */
	public String deleteStudent(String id,ChoiceTable choiceTable)
	{
		indexLock.lock();
		Index index = indexTree.getTreeMap().get(id);
		
		if(index == null)
		{
			indexLock.unlock();
			return "student not exist";
		}
		indexLock.unlock();
		
		ReentrantLock lock = FileLocks.get(index.getFileNum());
		lock.lock();
		
		//lock.unlock();
		try {
			if(indexTree.getTreeMap().get(id) == null)
			{
				lock.unlock();
				return "student not exist";
			}
			choiceTable.indexLock.lock();
			for(String key : choiceTable.indexTree.getTreeMap().keySet())
			{
				if(key.split("-")[0].equals(id))
                {
                    choiceTable.indexLock.unlock();
                    return "student has his choice";
                }
			}
			choiceTable.indexLock.unlock();
			
			String srcData = "";//存放读出的数据
			try (
					RandomAccessFile raf = new RandomAccessFile(new File(index.getFilePath()), "rw");
			){	
				raf.skipBytes(66 * (index.getLineNum() - 1));
				byte[] b = new byte[64];
				raf.read(b);
				srcData = new String(b);//将原来文件中的信息读进srcData中 
			} catch (Exception e) {
				e.printStackTrace();
				return "file read exception";
			}
			
			srcData = srcData.replace("*", "") + "+";//文件中读出的字符串去掉所有*加上+表示目前已删除
			//写回文件
			int distance = 64 - srcData.getBytes().length;
			StringBuilder stringBuilder = new StringBuilder(srcData);
            for (int i = 0; i < distance; i++) {
                stringBuilder.append("*");
            }
            try (
                    RandomAccessFile raf = new RandomAccessFile(new File(index.getFilePath()), "rw");
            ) {
                raf.skipBytes(66 * (index.getLineNum() - 1));
                raf.write(stringBuilder.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return "file write exception";
            }
            
            indexLock.lock();
            indexTree.getTreeMap().remove(id);
            //indexFile
            writeIndex();
            
            //readIndex();
            indexLock.unlock();
            
            return "success";
            
		}finally {
			lock.unlock();
		}
	}
	
	public String getRecordByName(String name){
		String s= "";
		StringBuilder stringbuilder = new StringBuilder(s);
		for(File f :dataFileSet){
			int fileNum=Integer.valueOf(f.getName().split(".data")[0]);
			ReentrantLock fLock=FileLocks.get(fileNum);
			fLock.lock();
			try(
					FileReader fr = new FileReader(f);
					BufferedReader br = new BufferedReader(fr);
			) 
			{
				String line = null;
				while((line = br.readLine()) != null)
				{
					if(!line.contains("+"))
					{
						String[] datas = line.replace("*", "").split("~");
						if(datas[2].equals(name)){
							stringbuilder.append(line.replace("*", ""));
							stringbuilder.append("\n");
						}
					}
				}
			} catch (FileNotFoundException e) {
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
	
	public static void main(String[] args) throws Exception {
        CourseTable courseTable = new CourseTable();
        StudentTable studentTable = new StudentTable();
        ChoiceTable choiceTable = new ChoiceTable(studentTable, courseTable);
        //Tool_StudentAndCourse tool_StudentAndCourse = 
        
//        Thread_InsertStudent myThread = new Thread_InsertStudent(studentTable);
//        List<Thread> threads = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            threads.add(new Thread(myThread));
//        }
//        for (int i = 0; i < 30; i++) {
//        	threads.get(i).start();
//        }
        
//        Thread_InsertCourse myThread_InsertCourse = new Thread_InsertCourse(courseTable);
//        List<Thread> threads = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            threads.add(new Thread(myThread_InsertCourse));
//        }
//        for (int i = 0; i < 30; i++) {
//        	threads.get(i).start();
//        }
        
//        Thread_InsertChoice myThread_InsertChoice = new Thread_InsertChoice(choiceTable);
//        List<Thread> threads = new ArrayList<>();
//        for (int i = 0; i < 200; i++) {
//            threads.add(new Thread(myThread_InsertChoice));
//        }
//        for (int i = 0; i < 200; i++) {
//        	threads.get(i).start();
//        }
        
//        Thread_DeleteChoice myThread_DeleteChoice = new Thread_DeleteChoice(choiceTable);
//        List<Thread> threads = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            threads.add(new Thread(myThread_DeleteChoice));
//        }
//        for (int i = 0; i < 10; i++) {
//        	threads.get(i).start();
//        }
//        System.out.println(studentTable.getRecordByName("Student16"));
//
        
        for(int i = 1;i < 200 ;i++)
        {
        	System.out.println(studentTable.InsertStudent(new Student(i + "", "Student" + i, "Class1702", (i%2==0)?true:false)));
        }
        for(int i = 1;i < 200 ;i++)
        	System.out.println(studentTable.getRecord(i+""));

        for(int i = 0;i <= 4;i++)
		{
			courseTable.insertCourse(new Course(i + "","Course" + i ,1000,1000));
		}



//        for(int i = 1;i < 31;i+=2)
//        {
//        	System.out.println(studentTable.deleteStudent(i + "", choiceTable));
//        }
//        for(int i = 1;i < 31 ;i++)
//        	System.out.println(studentTable.getRecord(i+""));
        
//        for(int i=0; i<30 ; i++)
//        {
//            threads.get(i).start();
//        }

//        for(int i = 1;i < 201;i++)
//        {
//            System.out.println(System.out.printf(studentTable.in));
//        }
//        studentTable.InsertStudent(new Student("123","子贤","Class12",true));
//        System.out.println(studentTable.getRecord("123"));
//        studentTable.InsertStudent(new Student("1234","海东","Class02",false));
//        System.out.println(studentTable.getRecord("1234"));
        //Student s = new Student("1234","汉字","Class10",false);
        //studentTable.InsertStudent(new Student("234","海东","Class02",false));
        //System.out.println(studentTable.UpdateStudent(s));
        //System.out.println(studentTable.getRecord("234"));
        
        //System.out.println(studentTable.deleteStudent("234", choiceTable));
        //studentTable.indexTree.getClass();
    }
	
	
}
