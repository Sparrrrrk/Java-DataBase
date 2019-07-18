package thread;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import table.ChoiceTable;
import table.CourseTable;

//import entity.Choice;
//import table.ChoiceTable;

public class Thread_DeleteCourse implements Runnable{
	private CourseTable courseTable;
	private ChoiceTable choiceTable;
    //private AtomicInteger i;
    
    private String CourseInfo;
    
    private String courseid;
    
    private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;
    
    public Thread_DeleteCourse(CourseTable courseTable,ChoiceTable choiceTable,String CourseInfo,OutputStreamWriter writer,InetAddress inetAddress)

    {
        this.courseTable = courseTable;
        this.choiceTable = choiceTable;
        //i = new AtomicInteger(0);
        
        this.CourseInfo = CourseInfo;
        
        this.courseid = CourseInfo;
        
        this.writer = writer;
//        this.userIP = userIP;
//        this.userPort = userPort;
        this.inetAddress = inetAddress;
    }
    
    @Override
    public void run() {
    	System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
            String result = courseTable.deleteCourse(courseid,choiceTable);
            writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    }
}
