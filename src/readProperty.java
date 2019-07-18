import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class readProperty {
    private static void readProperties(String key,StringBuffer configStr) {
        ResourceBundle resource = ResourceBundle.getBundle("SystemConfig");
        String test = resource.getString(key);
        configStr.append(test);
    }

//    public static void main(String[] args){
//        String key = "ServerPort";
//        StringBuffer configStr = new StringBuffer();
//        readProperties(key,configStr);
//        System.out.println(configStr);
//    }
}
