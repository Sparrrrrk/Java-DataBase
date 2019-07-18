package thread;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;

import entity.Student;
import table.StudentTable;

public class Thread_UpdateStudent implements Runnable{
	private StudentTable table;
    
    private String StudentInfo;//从调用出接收未解析的学生信息字符串
    
    private String studentID;
    private String studentname;
    private String studentclass;
    private boolean gender;
    private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;
    
    public Thread_UpdateStudent(StudentTable student,String StudentInfo,OutputStreamWriter writer,InetAddress inetAddress)
    {
        this.table = student;
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
    }


	@Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
            String result = table.UpdateStudent(new Student(studentID,studentname,studentclass,gender));
            writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    //    System.out.println(table.removeRemainSize("Thread-291561911264968"));
    }
}
