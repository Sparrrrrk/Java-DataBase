package thread;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import entity.Choice;
import table.ChoiceTable;

//import entity.Choice;
//import table.ChoiceTable;

public class Thread_InsertChoice implements Runnable{
	private ChoiceTable table;
    private AtomicInteger i;

    private String ChoiceInfo;
    private String Studentid;
    private String Courseid;
    private String time;

    private OutputStreamWriter writer;
//    private String userIP;
//    private String userPort;
    private InetAddress inetAddress;

    public  Thread_InsertChoice(ChoiceTable choiceTable)
    {
        this.table = choiceTable;
        //i.addAndGet(1);
        i = new AtomicInteger(0);
        //this.ChoiceInfo = ChoiceInfo;
    }
    public Thread_InsertChoice(ChoiceTable student,String ChoiceInfo,OutputStreamWriter writer,InetAddress inetAddress) {
        this.table = student;
        //i = new AtomicInteger(0);

        this.ChoiceInfo = ChoiceInfo;
        String[] splitChoiceInfo = ChoiceInfo.split("~");
        /*
         * splitChoiceInfo[0] = Studentid
         * splitChoiceInfo[1] = Courseid
         * splitChoiceInfo[2] = time
         */
        this.Studentid = splitChoiceInfo[0];
        this.Courseid = splitChoiceInfo[1];
        this.time = splitChoiceInfo[2];

        this.writer = writer;
//        this.userIP = userIP;
//        this.userPort = userPort;
        this.inetAddress = inetAddress;
    }

    /*
    @Override
    public void run() {
    	System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
            synchronized (i)
            {
                if(i.addAndGet(1) <= 100)
                {
                    String result = table.insertChoice(new Choice(i.addAndGet(0)+"","1","timexxx"));
                    System.out.println(result);
                }
                else
                {
                    String result = table.insertChoice(new Choice(i.addAndGet(0)+"","2","timexxx"));
                    System.out.println(result);
                }

            }

            //writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
}
    */
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"start");
        try {
                String result = table.insertChoice(new Choice(Studentid,Courseid));
                writer.write("{'to_client':'" + inetAddress.getHostAddress() + "','data':" + result +"}");
            }
         catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+new Date().getTime()+"end");
    }
}



