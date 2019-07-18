package thread;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;

import table.CourseTable;

public class Thread_GetCourse implements Runnable {
	private String key;
	private CourseTable courseTable;
	
	private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;
    
    public Thread_GetCourse(String key,CourseTable courseTable,OutputStreamWriter writer,InetAddress inetAddress) {
		this.key = key;
		this.courseTable = courseTable;
		
		this.writer = writer;
//		this.userIP = userIP;
//        this.userPort = userPort;
        this.inetAddress = inetAddress;
	}
    
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
            String result = courseTable.getRecord(key);
            writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    }

}
