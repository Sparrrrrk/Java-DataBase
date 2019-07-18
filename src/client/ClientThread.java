package client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;


public class ClientThread {
    //200个线程
    public static void main(String[] args){
        CreateThread myThread = new CreateThread();
        List<Thread> threads = new ArrayList<>();
        for ( int i=0; i<200;i++)
        {
            threads.add(new Thread(myThread));
        }

        for(int i=0; i<200;i++)
        {
//            try {
//                sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            threads.get(i).start();
            try {
				threads.get(i).sleep(50);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
        }
    }
}
