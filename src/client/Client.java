package client;
import java.util.*;
import java.io.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.Socket;


public class Client {
  public static Socket socket=null;
  public static DataInputStream dis = null;
  public static DataOutputStream dos = null;
  //public static String input = null;
  private static int choice = 0;
  private static String sid = null;   //学生id
  private static String sex = null;   //学生性别
  private static String sname= null;  //学生姓名
  private static String sclass = null; //学生班级

  private static String cid = null;   //课程id
  private static String cname = null;
  private static String cnum = null;
  private static String cfree = null;
  private static String ctm = null;   //课程时间

   /*** public static void Client(String id){
        Client c = new Client();
        c.sid = id;
    }***/
   public static void setStudentid(String id){
       sid =id;
   }
    public static void setCourseid(String id){
        cid =id;
    }
    public static void setCoursetm(String tm){
        ctm = tm;
    }
    public static void setChoice(int c){
        choice = c;
    }
    public static void main(String[] args) throws UnknownHostException,IOException{
        //Socket client1 = new Socket("localhost",8888);//建立连接 使用socket创建客户端+服务的地址和端口
        //BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        //DataInputStream dis = new DataInputStream(client1.getInputStream());
        //DataOutputStream dos = new DataOutputStream(client1.getOutputStream());
        //dos.writeUTF(sid);
        //dos.flush();

        System.out.println("欢迎进入选课系统");
        //System.out.println("请输入你的学号");
        //Scanner studentid = new Scanner( System.in);
        //sid = studentid.nextLine();
        int input=0;
        String key = "ServerAddress";
        Properties p = new Properties();
        p.load(new FileInputStream("src/SystemConfig.properties"));
        String serverAddress = p.getProperty(key);

        while(input!=4) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入操作：1为插入学生，2为删除学生，3为选课，4为退课，" +
                    "5为加课程，6为删除课程，7为查询学生ID，8为查询学生姓名，9为查询课程，" +
                    "10为查询选课，11为更新学生，12为更新课程");
            input = scanner.nextInt();
            switch(input) {
                case 1: {
                    choice = 1;
                    System.out.println("插入学生请输入信息");
                    System.out.println("请输入学号");
                    Scanner studentid = new Scanner(System.in);
                    sid = studentid.nextLine();
                    System.out.println("请输入姓名");
                    Scanner studentname = new Scanner(System.in);
                    sname= studentname.nextLine();
                    System.out.println("请输入班级");
                    Scanner studentclass = new Scanner(System.in);
                    sclass = studentclass.nextLine();
                    System.out.println("请输入性别");
                    Scanner studentsex = new Scanner(System.in);
                    sex = studentsex.nextLine();

                    String url = serverAddress +
                                 choice+
                                "&" + sid +
                                "~" + sname +
                                "~" + sclass+
                                "~"+sex;

                   final Base64.Encoder encoder = Base64.getEncoder();
                    String type = "POST";
                    String[] initUrl = url.split("&");
                    String[] initUser = initUrl[1].split("~");
                    String userName = initUser[1];
                    final byte[] userNameByte = userName.getBytes("UTF-8");
                    final String userEncodeNanme = encoder.encodeToString(userNameByte);
                    userName = userEncodeNanme;
                    url = initUrl[0] + "&" + initUser[0] +"~"+ userName+"~"+initUser[2]+"~"+initUser[3];
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);


                    break;
                }
                case 2: {
                    choice = 2;
                    System.out.println("删除学生请输入信息");
                    Scanner deletesid = new Scanner (System.in);
                    sid =deletesid.nextLine();
                    String url =serverAddress +
                            choice+
                            "&" + sid ;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);

                    break;
                }
                case 3:{
                    choice = 3;
                    System.out.println("选课请输入信息");
                    System.out.println("请输入学号");
                    Scanner studentid = new Scanner(System.in);
                    sid = studentid.nextLine();
                    System.out.println("选课请输入选课序号");
                    Scanner choicecourseid = new Scanner (System.in);
                    cid =choicecourseid.nextLine();
                    System.out.println("输入选课时间(周几)");
                    Scanner choicecoursetime = new Scanner (System.in);
                    ctm =choicecoursetime.nextLine();
                    String url =serverAddress +
                             choice+
                            "&" + sid +
                            "~" + cid +
                            "~" + ctm;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 4:{
                    choice = 4;
                    System.out.println("退课请输入信息");
                    System.out.println("请输入学号");
                    Scanner studentid = new Scanner(System.in);
                    sid = studentid.nextLine();
                    System.out.println("选课请输入选课序号");
                    Scanner choicecourseid = new Scanner (System.in);
                    cid =choicecourseid.nextLine();
                    System.out.println("输入选课时间(周几)");
                    Scanner choicecoursetime = new Scanner (System.in);
                    ctm =choicecoursetime.nextLine();
                    String url = serverAddress + choice+
                                    "&" + sid +
                                    "~" + cid +
                                    "~" + ctm;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 5:{
                    choice = 5;
                    System.out.println("加入课程请输入信息");
                    System.out.println("请输入课程编号");
                    Scanner studentid = new Scanner(System.in);
                    cid = studentid.nextLine();
                    System.out.println("请输入课程名字");
                    Scanner coursename = new Scanner (System.in);
                    cname =coursename.nextLine();
                    System.out.println("请输入课程总容量");
                    Scanner coursenumber = new Scanner (System.in);
                    cnum =coursenumber.nextLine();
                    System.out.println("请输入课程剩余容量");
                    Scanner coursefree = new Scanner (System.in);
                    cfree =coursefree.nextLine();
                    String url = serverAddress + choice+
                                    "&" + cid +
                                    "~"+cname+
                                    "~" + cnum +
                                    "~" + cfree;

                    final Base64.Encoder encoder = Base64.getEncoder();
                    String type = "POST";
                    String[] initUrl = url.split("&");
                    String[] initUser = initUrl[1].split("~");
                    String userName = initUser[1];
                    final byte[] userNameByte = userName.getBytes("UTF-8");
                    final String userEncodeNanme = encoder.encodeToString(userNameByte);
                    userName = userEncodeNanme;
                    url = initUrl[0] + "&" + initUser[0] +"~"+ userName+"~"+initUser[2]+"~"+initUser[3];
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 6:{
                    choice = 6;
                    System.out.println("删除请输入信息");
                    System.out.println("请输入课程号");
                    Scanner courseid = new Scanner(System.in);
                    cid = courseid.nextLine();
                    String url =serverAddress +
                            choice+
                                    "&" + cid ;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 7:{
                    choice = 7;
                    System.out.println("查找学生ID请输入信息");
                    System.out.println("请输入学号");
                    Scanner studentid = new Scanner(System.in);
                    sid = studentid.nextLine();
                    String url =serverAddress + choice+
                                    "&" + sid ;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 8:{
                    choice = 8;
                    System.out.println("查找学生姓名请输入信息");
                    System.out.println("请输入学生姓名");
                    Scanner studentid = new Scanner(System.in);
                    sid = studentid.nextLine();
                    String url = serverAddress + choice+
                                    "&" + sid ;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 9:{
                    choice = 9;
                    System.out.println("查找课程ID请输入信息");
                    System.out.println("请输入课程ID");
                    Scanner courseid = new Scanner(System.in);
                    cid = courseid.nextLine();
                    String url =serverAddress +
                            choice+
                                    "&" + cid ;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 10:{
                    choice = 10;
                    System.out.println("查询选课情况请输入信息");
                    System.out.println("请输入学生学号");
                    Scanner studentid = new Scanner(System.in);
                    sid = studentid.nextLine();
                    System.out.println("选课请输入课程ID");
                    Scanner choicecourseid = new Scanner (System.in);
                    cid =choicecourseid.nextLine();
                    String url = serverAddress + choice+
                                    "&" + sid +
                                    "-" + cid ;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 11:{
                    choice = 11;
                    System.out.println("更新学生请输入信息");
                    System.out.println("请输入学生学号");
                    Scanner studentid = new Scanner(System.in);
                    sid = studentid.nextLine();
                    System.out.println("请输入学生姓名");
                    Scanner studentname = new Scanner (System.in);
                    sname = studentname.nextLine();
                    System.out.println("请输入学生班级");
                    Scanner studentclass = new Scanner (System.in);
                    sclass = studentclass.nextLine();
                    System.out.println("请输入学生性别");
                    Scanner studentsex = new Scanner (System.in);
                    sex = studentsex.nextLine();
                    String url =  serverAddress + choice+
                                    "&" + sid +
                                    "~" + sname +
                                    "~" + sclass+
                                    "~"+sex;

                    final Base64.Encoder encoder = Base64.getEncoder();
                    String type = "POST";
                    String[] initUrl = url.split("&");
                    String[] initUser = initUrl[1].split("~");
                    String userName = initUser[1];
                    final byte[] userNameByte = userName.getBytes("UTF-8");
                    final String userEncodeNanme = encoder.encodeToString(userNameByte);
                    userName = userEncodeNanme;
                    url = initUrl[0] + "&" + initUser[0] +"~"+ userName+"~"+initUser[2]+"~"+initUser[3];
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 12:{
                    choice = 12;
                    System.out.println("更新课程请输入信息");
                    System.out.println("请输入课程ID");
                    Scanner courseid = new Scanner(System.in);
                    cid = courseid.nextLine();
                    System.out.println("请输入课程姓名");
                    Scanner coursename = new Scanner (System.in);
                    cname =coursename.nextLine();
                    System.out.println("请输入课程课容量");
                    Scanner coursenumber = new Scanner (System.in);
                    cnum = coursenumber.nextLine();
                    System.out.println("请输入课程剩余容量");
                    Scanner coursefree = new Scanner (System.in);
                    cfree = coursefree.nextLine();
                    String url = serverAddress + choice+
                            "&" + cid +
                            "~" + cname +
                            "~" + cnum+
                            "~"+ cfree;

                     final Base64.Encoder encoder = Base64.getEncoder();
                    String type = "POST";
                    String[] initUrl = url.split("&");
                    String[] initUser = initUrl[1].split("~");
                    String userName = initUser[1];
                    final byte[] userNameByte = userName.getBytes("UTF-8");
                    final String userEncodeNanme = encoder.encodeToString(userNameByte);
                    userName = userEncodeNanme;
                    url = initUrl[0] + "&" + initUser[0] +"~"+ userName+"~"+initUser[2]+"~"+initUser[3];
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 13:{
                    choice = 13;
                    System.out.println("查找学生具体选课信息");
                    System.out.println("请输入学生ID");
                    Scanner studentid = new Scanner(System.in);
                    sid = studentid.nextLine();
                    String url = serverAddress + choice+
                                    "&" + sid ;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }
                case 14:{
                    choice = 14;
                    System.out.println("查找课程具体选课信息");
                    System.out.println("请输入课程ID");
                    Scanner courseid = new Scanner(System.in);
                    cid = courseid.nextLine();
                    String url = serverAddress + choice+
                            "&" + cid ;

                    String type = "POST";
                    String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
                    System.out.println(ret);
                    break;
                }

            }
           // scanner.close();

        }
    }
}
