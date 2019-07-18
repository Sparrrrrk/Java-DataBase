package server;

//import api.API;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class WorkThread implements Runnable{
    private HttpExchange httpExchange;

    public WorkThread(HttpExchange httpExchange)
    {
        this.httpExchange = httpExchange;
    }


    @Override
    public void run() {
        //接收到的信息
        final Base64.Decoder decoder = Base64.getDecoder();
        String requestData = httpExchange.getRequestURI().getRawQuery();
        System.out.println(requestData);
		
        String[] initRequest = requestData.split("&");
        String[] initUserName = initRequest[1].split("~");
        String userName = "";
		if(initRequest[0].equals("1") || initRequest[0].equals("5") || initRequest[0].equals("11") || initRequest[0].equals("12")){
			int len = initUserName.length;
			try {

				userName = new String(decoder.decode(initUserName[1]),"UTF-8");
                System.out.println(userName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
//            System.out.println(userName);
			requestData = initRequest[0] + "&" + initUserName[0] + "~" + userName;
			for(int i = 2; i < len; i++){				
				requestData +="~" + initUserName[i];			
			}
		}
//        System.out.println(requestData);
        String serverIP = httpExchange.getLocalAddress().toString();
        String userIP = httpExchange.getRemoteAddress().toString();

        String userMessage = userIP + "&" + requestData;
//        System.out.println(userMessage);
        OutputStream outputStream = httpExchange.getResponseBody();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

        try {
            SocketServer socketServer = new SocketServer("localhost",23334);
            StringBuffer dataBaseMessage = new StringBuffer();
            socketServer.start(userMessage,dataBaseMessage);
            httpExchange.sendResponseHeaders(200, dataBaseMessage.toString().length());
            outputStreamWriter.write(dataBaseMessage.toString());
            outputStreamWriter.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
