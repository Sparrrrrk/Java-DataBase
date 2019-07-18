package server;

import com.sun.net.httpserver.HttpServer;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class Server {


    public static void startServer() throws Exception
    {
        String key = "ServerPort";
        Properties p = new Properties();
        p.load(new FileInputStream("src/SystemConfig.properties"));
        String s = p.getProperty(key);
        int port = Integer.parseInt(s);


        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(null);
        httpServer.createContext("/dbms",new Handler());
        httpServer.start();
        System.out.println("服务器初始化成功");
    }

    public static void main(String[] args) throws Exception{
        startServer();
    }
}
