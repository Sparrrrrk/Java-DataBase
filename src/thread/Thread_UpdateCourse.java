package thread;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;

import entity.Course;
import table.CourseTable;

public class Thread_UpdateCourse implements Runnable{
	private CourseTable table;
    //private AtomicInteger i;
    
    private String CourseInfo;
    
    private String courseid;
	private String name;
	private int TotalSize;
	private int RemainSize;
	
    private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;
    
    public Thread_UpdateCourse(CourseTable courseTable,String CourseInfo,OutputStreamWriter writer,InetAddress inetAddress)
    {
        this.table = courseTable;
       // i = new AtomicInteger(0);
        
        this.CourseInfo = CourseInfo;
        
        String[] splitCourseInfo = CourseInfo.split("~");
        /*
         * splitCourseInfo[0] = courseid
         * splitCourseInfo[1] = name
         * splitCourseInfo[2] = TotalSize
         * splitCourseInfo[3] = RemainSize
         * 
         */
        this.courseid = splitCourseInfo[0];
        this.name = splitCourseInfo[1];
        this.TotalSize = Integer.parseInt(splitCourseInfo[2]);
        this.RemainSize = Integer.parseInt(splitCourseInfo[3]);
        
        this.writer = writer;
//        this.userIP = userIP;
//        this.userPort = userPort;
        this.inetAddress = inetAddress;
        
    }
    
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
			String result = table.updateCourse(new Course(courseid,name,TotalSize,RemainSize));
			writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    }
}
