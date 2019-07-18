package server;

import java.io.*;
import java.net.*;
import java.util.*;


public class SocketServer {
    static String Ip;
    static int port;
    public SocketServer(String Ip, int port){
        this.Ip = Ip;
        this.port = port;
    }
    public static void start(String userMessage,StringBuffer respondMessage){
        try {
            Socket socket;   //ip & port
            socket = new Socket(Ip,port);
            OutputStream outputStream = socket.getOutputStream();//得到一个输出流，用于向服务器发送数据
            OutputStreamWriter writer=new OutputStreamWriter(outputStream,"UTF-8");//将写入的字符编码成字节后写入一个字节流

			String data = userMessage;
            System.out.println("socket:"+data);
            writer.write(data);
            writer.flush();//刷新缓冲
            socket.shutdownOutput();//只关闭输出流而不关闭连接
            //获取服务器端的响应数据

            InputStream inputStream = socket.getInputStream();//得到一个输入流，用于接收服务器响应的数据
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");//将一个字节流中的字节解码成字符
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//为输入流添加缓冲
            String info = "no respond";
//            System.out.println("数据库IP地址:"+socket.getInetAddress().getHostAddress());
            //输出数据库端处理后的数据
            while ((info = bufferedReader.readLine()) != null) {
                System.out.println("数据库respond：" + info);   //收到的内容都在info里
                respondMessage.append(info);
            }
            //关闭资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            writer.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
