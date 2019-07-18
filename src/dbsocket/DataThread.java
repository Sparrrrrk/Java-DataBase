package dbsocket;
import java.io.*;
import java.net.*;

import table.ChoiceTable;
import table.CourseTable;
import table.StudentTable;
import thread.Thread_DeleteChoice;
import thread.Thread_DeleteCourse;
import thread.Thread_DeleteStudent;
import thread.Thread_GetChoiceByCourseid;
import thread.Thread_GetChoiceByStudentid;
import thread.Thread_GetCourse;
import thread.Thread_GetInformation;
import thread.Thread_GetStudentByName;
import thread.Thread_InsertChoice;
import thread.Thread_InsertCourse;
import thread.Thread_InsertStudent;
import thread.Thread_UpdateCourse;
import thread.Thread_UpdateStudent;

/**
 * 用于规定数据库与服务器间交互的信息格式
 */

public class DataThread extends Thread {

    Socket socket = null;
    InetAddress inetAddress=null;//接收服务器端的连接
    
    protected StudentTable studentTable;
    protected CourseTable courseTable;
    protected ChoiceTable choiceTable;

    public DataThread(Socket socket,InetAddress inetAddress) {
        this.socket = socket;
        this.inetAddress=inetAddress;
        
        this.studentTable = new StudentTable();
        this.courseTable = new CourseTable();
        this.choiceTable = new ChoiceTable(studentTable, courseTable);
    }

    @Override
    public void run(){
        InputStream inputStream = null;//字节输入流
        InputStreamReader inputStreamReader = null;//将一个字节流中的字节解码成字符
        BufferedReader bufferedReader = null;//为输入流添加缓冲
        OutputStream outputStream = null;//字节输出流
        OutputStreamWriter writer = null;//将写入的字符编码成字节后写入一个字节流
        try {
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String info = null;//临时

            //循环读取服务端处理过的用户信息
            while ((info = bufferedReader.readLine()) != null) {
                //获取客户端的ip地址及发送数据
                System.out.println("数据库端接收:"+info); //server发来的信息
                
                /**
                 * 这里通过&分割接收到的数据
             * splitInfo[0] = userI +
             * splitInfo[1] = operation 即用户的操作类型
             * （eg: 1 == 插入学生 
             * 2 == 删除学生 
             * 3 == 选课 
             * 4 == 退课 
             * 5 == 插入课程 
             * 6 == 删除课程 
             * 7 == 查询学生 by id
             * 8 == 查询学生 by name
             * 9 == 查询课程
             * 10 == 查询选课
             * 11 == 更新学生
             * 12 == 更新课程
             * ）
             * splitInfo[2] = userString 需要用的用户信息（eg: "username = "小明" ~ class = 1701"）??
             */
            String[] splitInfo = info.split("&");
            socket.shutdownInput();//关闭输入流

            /**
             * 这里可以填充相应的处理函数
             */
       
            outputStream = socket.getOutputStream();//在这里初始化流，将流传到处理线程中，在线程中将字符串写回，目前写回的字符串中没有ip和port信息，每一种操作(增删改查)对应着一个Thread_xxx类
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            if(splitInfo[1].equals("1"))
            {
            	Thread_InsertStudent myThread_InsertStudent = new Thread_InsertStudent(studentTable, splitInfo[2], writer, inetAddress);
            	Thread thread = new Thread(myThread_InsertStudent);
            	
            	thread.start();
            }
            else if(splitInfo[1].equals("2"))
            {
            	Thread_DeleteStudent myThread_DeleteStudent = new Thread_DeleteStudent(studentTable, choiceTable, splitInfo[2], writer, inetAddress);
            	Thread thread = new Thread(myThread_DeleteStudent);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
            }
            else if (splitInfo[1].equals("3")) {
            	Thread_InsertChoice myThread_InsertChoice = new Thread_InsertChoice(choiceTable,splitInfo[2],writer,inetAddress);
            	Thread thread = new Thread(myThread_InsertChoice);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("4")) {
            	Thread_DeleteChoice myThread_DeleteChoice = new Thread_DeleteChoice(choiceTable,splitInfo[2],writer,inetAddress);
            	Thread thread = new Thread(myThread_DeleteChoice);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("5")) {
            	Thread_InsertCourse myThread_InsertCourse = new Thread_InsertCourse(courseTable, splitInfo[2], writer,inetAddress);
            	Thread thread = new Thread(myThread_InsertCourse);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("6")) {
            	Thread_DeleteCourse myThread_DeleteCourse = new Thread_DeleteCourse(courseTable,choiceTable, splitInfo[2], writer,inetAddress);
            	Thread thread = new Thread(myThread_DeleteCourse);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if(splitInfo[1].equals("7")){
            	Thread_GetInformation myThread_GetInformaitin = new Thread_GetInformation(splitInfo[2],studentTable, writer, inetAddress);
            	Thread thread = new Thread(myThread_GetInformaitin);
            	thread.start();
//            	try {
//					//thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if(splitInfo[1].equals("8")){
            	Thread_GetStudentByName myThread_GetStudentByName = new Thread_GetStudentByName(splitInfo[2],studentTable, writer,inetAddress);
            	Thread thread = new Thread(myThread_GetStudentByName);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("9")) {
            	Thread_GetCourse myThread_GetCourse = new Thread_GetCourse(splitInfo[2],courseTable, writer, inetAddress);
            	Thread thread = new Thread(myThread_GetCourse);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("10")) {
            	Thread_GetInformation myThread_GetChoice = new Thread_GetInformation(splitInfo[2], choiceTable , writer, inetAddress);
            	Thread thread = new Thread(myThread_GetChoice);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("11")) {
            	Thread_UpdateStudent myThread_UpdateStudent = new Thread_UpdateStudent(studentTable, splitInfo[2], writer, inetAddress);
            	Thread thread = new Thread(myThread_UpdateStudent);
            	thread.start();
//            	try {
//					//thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("12")) {
            	Thread_UpdateCourse myThread_UpdateCourse = new Thread_UpdateCourse(courseTable, splitInfo[2] , writer, inetAddress);
            	Thread thread = new Thread(myThread_UpdateCourse);
            	thread.start();
//            	try {
//					thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("13")) {
            	Thread_GetChoiceByStudentid myThread_GetChoiceByStudentid=new Thread_GetChoiceByStudentid( splitInfo[2] ,choiceTable, writer, inetAddress);
            	Thread thread = new Thread(myThread_GetChoiceByStudentid);
            	thread.start();
//            	try {
//					thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}
            else if (splitInfo[1].equals("14")) {
            	Thread_GetChoiceByCourseid myThread_GetChoiceByCourseid=new Thread_GetChoiceByCourseid( splitInfo[2] ,choiceTable, writer, inetAddress);
            	Thread thread = new Thread(myThread_GetChoiceByCourseid);
            	thread.start();
//            	try {
//					thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				}
			}

            //响应服务器请求
            
            //writer.write("{'to_client':'"+inetAddress.getHostAddress()+"','data':'我是数据库数据'}"); //db返回的信息

            //writer.flush();//清空缓冲区数据??
            }
            

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                if (writer != null) {
                	try {
						sleep(500);
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
                    writer.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
