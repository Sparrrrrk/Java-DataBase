package thread;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import entity.Student;
import table.StudentTable;
import table.Tool_StudentAndCourse;

//import entity.Student;
//import table.StudentTable;

public class Thread_InsertStudent implements Runnable{
	private StudentTable table;
    //private AtomicInteger i;
    
    private String StudentInfo;//从调用出接收未解析的学生信息字符串
    
    private String studentID;
    private String studentname;
    private String studentclass;
    private boolean gender;
    private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;
    
    /**
     * Thread_InsertStudent 构造函数
     * 
     * @param student 学生表，StudentTable类型
     * @param StudentInfo 包含全部学生信息的字符串，格式为id~姓名~班级~性别
     * @param writer OutputStreamWriter类型，写信息给服务器
     * @param userIP 客户端ip
     * @param userPort 客户端端口
     * @param inetAddress 
     */
    public Thread_InsertStudent(StudentTable student,String StudentInfo,OutputStreamWriter writer,InetAddress inetAddress)
    {
        this.table = student;
        //i= new AtomicInteger(0);
        this.StudentInfo = StudentInfo;
        /*
         * splitStudentInfo[0] = studentid
         * splitStudentInfo[1] = studentname
         * splitStudentInfo[2] = studentclass
         * splitStudentInfo[3] = gender
         * 
         */
        String[] splitStudentInfo = StudentInfo.split("~");
        
        this.studentID = splitStudentInfo[0];
        this.studentname = splitStudentInfo[1];
        this.studentclass = splitStudentInfo[2];
        this.gender = (splitStudentInfo[3] == "true")?true:false;
        
        this.writer = writer;
//        this.userIP = userIP;
//        this.userPort = userPort;
        this.inetAddress = inetAddress;
//        try {
//			this.writer.write("sbsbsbsbs-");
//		} catch (IOException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
    }


    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
        	String result = new String();
            result = table.InsertStudent(new Student(studentID,studentname,studentclass,gender));
            writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    //    System.out.println(table.removeRemainSize("Thread-291561911264968"));
    }
}
