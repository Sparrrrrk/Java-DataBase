package client;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Runnable;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateThread implements Runnable{
    private AtomicInteger counter  = new AtomicInteger(0);
    //private  String studentid;
    private  int studentid;
    private String url;
    @Override
    public void run() {
        //学生ID从1--200   choice都是选课
        //4门课 50个人选一个  课余量都是50
        //courseID 1234
        synchronized (counter) {
            String key = "ServerAddress";
            Properties p = new Properties();
            try {
                p.load(new FileInputStream("src/SystemConfig.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String serverAddress = p.getProperty(key);
            counter.addAndGet(1);
            studentid = counter.intValue();
            if(studentid<50){

                url =serverAddress +
                        "3"+
                        "&" + studentid +
                        "~" + 1 +
                        "~" + 0;

//                String type = "POST";
//                String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
//            client.HttpURLConnectionHelper.respond(url);
                //System.out.println(ret);
            }else if(studentid>=50 && studentid< 100){
               url =serverAddress +
                        "3"+
                        "&" + studentid +
                        "~" + 2 +
                        "~" + 0;

//                String type = "POST";
//                String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
//            client.HttpURLConnectionHelper.respond(url);
            }else if (studentid>= 100 && studentid <150){
                url =serverAddress +
                        "3"+
                        "&" + studentid +
                        "~" + 3 +
                        "~" + 0;

//                String type = "POST";
//                String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
//            client.HttpURLConnectionHelper.respond(url);
            }else if(studentid >= 150 && studentid <= 200){
                 url =serverAddress +
                        "3"+
                        "&" + studentid +
                        "~" + 4 +
                        "~" + 0;

//                String type = "POST";
//                String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
//            client.HttpURLConnectionHelper.respond(url);
            }
        }
        String type = "POST";
        
		String ret =  client.HttpURLConnectionHelper.sendRequest(url,type);
		System.out.println(ret);
            

    }
}
