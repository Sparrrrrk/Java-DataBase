package dbsocket;
/**
 * 数据库段的socket服务启动
 */

import java.io.*;
import java.net.*;
import java.util.Properties;


public class DataBaseSocket {
    public static void main(String[] args) {
        try {
            String key = "DataBasePort";
            Properties p = new Properties();
            p.load(new FileInputStream("src/SystemConfig.properties"));
            String s = p.getProperty(key);
            int port = Integer.parseInt(s);

            ServerSocket serverSocket = new ServerSocket(port);//创建绑定到特定端口的服务器Socket。
            Socket socket = null;//需要接收的客户端Socket
            int count = 0;//记录客户端数量
            System.out.println("socket连接启动");
            InetAddress addr = InetAddress.getLocalHost();
    		System.out.println("本地host:" + addr.getHostName());
            //定义一个死循环，不停的接收客户端连接
            while (true) {
                socket = serverSocket.accept();//侦听并接受到此套接字的连接
                InetAddress inetAddress=socket.getInetAddress();//获取客户端的连接
                DataThread thread=new DataThread(socket,inetAddress);//自己创建的线程类
                thread.start();//启动线程
                count++;//如果正确建立连接
                System.out.println("连接数量：" + count);//打印客户端数量
            }
        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}
