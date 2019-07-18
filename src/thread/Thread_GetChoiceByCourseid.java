package thread;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;

import table.ChoiceTable;
import table.Table;

public class Thread_GetChoiceByCourseid implements Runnable{
//	private StudentTable studentTable;
//	private CourseTable courseTable;
//	private ChoiceTable choiceTable;
	
	private String key;
	private ChoiceTable table;
	
	private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;
    
    public Thread_GetChoiceByCourseid(String key,ChoiceTable table,OutputStreamWriter writer,InetAddress inetAddress) {
		this.key = key;
		this.table = table;
		
		this.writer = writer;
//		this.userIP = userIP;
//        this.userPort = userPort;
        this.inetAddress = inetAddress;
	}
    
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
            String result = new String();
            result = table.getchoiceByCourseid(key);
            writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    }

}
