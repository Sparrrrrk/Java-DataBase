package client;


import java.beans.Encoder;
import java.io.*;
import java.net.*;
import java.util.*;


public class HttpURLConnectionHelper {
    public static String sendRequest(String urlParam, String requestType) {


        HttpURLConnection con = null;
        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;
        try {
            URL url = new URL(urlParam);
            //得到连接对象
            con = (HttpURLConnection) url.openConnection();
            //设置请求类型
            con.setRequestMethod(requestType);
            //设置请求需要返回的数据类型和字符集类型
            con.setRequestProperty("Content-Type", "application/json;charset=GBK");
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);
            //得到响应码
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //得到响应流
                InputStream inputStream = con.getInputStream();
                //将响应流转换成字符串
                resultBuffer = new StringBuffer();
                String line;
                buffer = new BufferedReader(new InputStreamReader(inputStream, "GBK")); //"UTF-8"
                while ((line = buffer.readLine()) != null) {
                    resultBuffer.append(line);
                }
                return resultBuffer.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

//    public static void main(String[] args) throws UnsupportedEncodingException {
//        final Base64.Decoder decoder = Base64.getDecoder();
//        final Base64.Encoder encoder = Base64.getEncoder();
//    /**
//     * url中"http://localhost:23333/dbms?"不变 为服务器的ip port 目录
//     * 后面的内容根据用户进程填写 操作序号与具体参数之间以&分隔
//     * 具体内容中以~分隔
//     * 1&1~良好的~class1702~true
//     */
//        String url ="http://localhost:23333/dbms?1&2~良好的~class1702~true";
//        String[] initUrl = url.split("&");
//        String[] initUser = initUrl[1].split("~");
//        String userName = initUser[1];
//        final byte[] userNameByte = userName.getBytes("UTF-8");
//        final String userEncodeNanme = encoder.encodeToString(userNameByte);
//        userName = userEncodeNanme;
//        url = initUrl[0] + "&" + initUser[0] +"~"+ userName+"~"+initUser[2]+"~"+initUser[3];
//        System.out.println(sendRequest(url,"POST"));
//    }
}
