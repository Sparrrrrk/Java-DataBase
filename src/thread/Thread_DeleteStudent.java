package thread;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import table.ChoiceTable;
import table.StudentTable;

public class Thread_DeleteStudent implements Runnable{
	private StudentTable studentTable;
	private ChoiceTable choiceTable;
    //private AtomicInteger i;
    
    private String StudentInfo;//从调用出接收未解析的学生信息字符串
    
    private String studentID;
    
    private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;
    
    /**
     * 构造函数
     * 
     * @param student 学生表，StudentTable类型
     * @param choiceTable 选课表 ChoiceTable类型
     * @param StudentInfo 包含全部学生信息的字符串，当前版本仅学生id
     * @param writer OutputStreamWriter类型，写信息给服务器
     * @param userIP 客户端ip
     * @param userPort 客户端端口
     * @param inetAddress
     */
    public Thread_DeleteStudent(StudentTable student,ChoiceTable choiceTable,String StudentInfo,OutputStreamWriter writer,InetAddress inetAddress)
    {
        this.studentTable = student;
        //i= new AtomicInteger(0);
        this.choiceTable = choiceTable;
        this.StudentInfo = StudentInfo;
        
        //String[] splitStudentInfo = StudentInfo.split("~");
        this.studentID = StudentInfo;//删除学生只需要提供学生id
        
        this.writer = writer;
//        this.userIP = userIP;
//        this.userPort = userPort;
        this.inetAddress = inetAddress;
        
    }
    
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
            String result = new String();
            result = studentTable.deleteStudent(studentID,choiceTable);
            writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    }
}


