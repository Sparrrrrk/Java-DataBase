package thread;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;

import table.StudentTable;

public class Thread_GetStudentByName implements Runnable {
	private String key;
	private StudentTable table;
	
	private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;
    
    public Thread_GetStudentByName(String key,StudentTable studentTable,OutputStreamWriter writer,InetAddress inetAddress) {
		this.key = key;
		this.table = studentTable;
		
		this.writer =writer;
//		this.userIP = userIP;
//        this.userPort = userPort;
        this.inetAddress = inetAddress;
	}
    
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
            String result = new String();
            System.out.println(table.getRecordByName(key));
            result = table.getRecordByName(key);
            System.out.println(result);
            writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    }
}
