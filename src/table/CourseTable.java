package table;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import entity.Course;

public class CourseTable  implements Serializable{
	
    protected ReentrantLock fileLock;			 //数据文件锁
    protected File dataFile;					 //数据文件
    protected TreeMap<String,Course> courseTree; //内存中的记录
    private static final long serialVersionUID = -2283394827630681559L;
    public CourseTable() {
    	
        this.dataFile=new File("dir/course","1.data");
        this.fileLock=new ReentrantLock(true);
        
    	this.courseTree=new TreeMap<>();
    	if(dataFile.exists()){
    		fileLock.lock();
    		readData();
    		fileLock.unlock();
    	}

        System.out.println("课程信息初始化完成");
    }

    
    @SuppressWarnings("unchecked")
	private void readData(){
    	if(!dataFile.exists())
    		return;
    	try(
    			FileInputStream fis = new FileInputStream(dataFile);
                ObjectInputStream ois = new ObjectInputStream(fis)
               ){
              courseTree = (TreeMap<String,Course>) ois.readObject();
              ois.close();
              fis.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private void writeData(){
    	try (
                FileOutputStream fos = new FileOutputStream(dataFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(courseTree);
            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String insertCourse(Course course) throws Exception  {
    	fileLock.lock();
    	if (courseTree.containsKey(course.getId())) {
    		fileLock.unlock();
            return "key exesits";
        }
    	courseTree.put(course.getId(), course);
    	writeData();
        fileLock.unlock();
        return "success";
    }



    public String getRecord(String key) {
        fileLock.lock();
        if(!courseTree.containsKey(key)){
        	fileLock.unlock();
        	return "course not found";
        }
        String record =courseTree.get(key).toString();
        fileLock.unlock();
        return record;
    }

    public Course getCourse(String key) {
        fileLock.lock();
        if(!courseTree.containsKey(key)){
        	fileLock.unlock();
        	return null;
        }
        Course temp = courseTree.get(key);
        Course cour = new Course(temp.getId(),temp.getName(),
        					temp.getTotalSize(),temp.getRemainSize());
        fileLock.unlock();
        return cour;
    }



    public String updateCourse(Course course) throws Exception{
    	fileLock.lock();
    	if(!courseTree.containsKey(course.getId())){
        	fileLock.unlock();
        	return "course not found";
        }
    	course.SelectedNum = course.TotalSize-course.RemainSize;
    	courseTree.put(course.getId(), course);
    	writeData();
    	fileLock.unlock();
    	return "success";
    }

    public String deleteCourse(String id, ChoiceTable choiceTable) {
    	fileLock.lock();
    	if(!courseTree.containsKey(id)){
    		fileLock.unlock();
    		return "course not found";
    	}
    	
            choiceTable.indexLock.lock();
            for (String key : choiceTable.indexTree.getTreeMap().keySet()) {
                if (key.split("-")[1].equals(id)) {
                    choiceTable.indexLock.unlock();
                    return "The course has his choice";
                }
            }
            choiceTable.indexLock.unlock();
            
            courseTree.remove(id);
            writeData();
            fileLock.unlock();
            return "success";
}

    public static void main(String[] args) throws Exception  {
//        RandomAccessFile raf = new RandomAccessFile(new File("dir"+"/"+"course"+"/data/3.data"),"rw");
//        raf.skipBytes(0);
//        raf.write("子贤".getBytes());
//        System.out.println(raf.readLine());
//        raf.close();
//        String str = "1-2-3-4";
//        String res = String.format("%10s", str);
//        res = res.replaceAll("\\s", "*");
//        System.out.println(res);
        CourseTable courseTable = new CourseTable();
        
        StudentTable studentTable = new StudentTable();
        ChoiceTable choiceTable =  new ChoiceTable(studentTable, courseTable) ;
//        Course c1= new  Course("1","a",300,20);
//        Course c2= new Course("2","b",300,20);
//        Course c3= new Course("3","c",300,20);
//        Course c4= new Course("4","d",300,20);
//        for(int i = 1;i < 11;i++)
//        {
//        	System.out.println(courseTable.insertCourse(new Course(i + "", "course" + i, 100, 50)));
//        }
//        System.out.println(courseTable.insertCourse(c1));
//        System.out.println(courseTable.insertCourse(c2));
//        System.out.println(courseTable.insertCourse(c3));
        
        for(int i = 1;i < 31;i++)
        	System.out.println(courseTable.getRecord(i + ""));
        //System.out.println(courseTable.getRecord("4"));
        
        //Course c= courseTable.getCourse("1");
        //c.66
       // System.out.println(courseTable.updateCourse(c));
        //System.out.println(courseTable.getRecord("2"));
        
        //System.out.println(courseTable.deleteCourse("1", choiceTable));
        //System.out.println(courseTable.getRecord("1"));
        
        //System.out.println(courseTable.deleteCourse("1", choiceTable));

        
       // System.out.println(courseTable.addRemainSize("Thread-191561910818941"));
   /*     MyThread3 myThread = new MyThread3(courseTable);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            threads.add(new Thread(myThread));
        }

        for(int i=0; i<30 ; i++)
        {
            threads.get(i).start();
        }*/
    }
    
    

}