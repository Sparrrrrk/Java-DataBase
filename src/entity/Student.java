package entity;
import java.io.Serializable;

//import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

public class Student implements Serializable{
	
	private static final String classroom = null;
	private String id;
	private String Name;
	private String Class;
	private boolean sex;//true-男 false-女
	private static final long serialVersionUID = 7215081435240984955L;
	 public Student(String id,String Name,String Class,boolean sex) //构造函数
	 {
		this.Name = Name;
		this.id = id;
		this.Class = Class;
		this.sex = sex;
	}
	 
	 public String getName()//返回名字
	{
		return Name;
	}
		
	public String getid()//返回id
	{
		return id;
	}
	public String getStudentClass()//返回班级
	{
		return Class;
	}
	public boolean getSex()//返回性别
	{
		return sex;
	}
	
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", classroom='" + Class + '\'' +
                ", name='" + Name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
	
	
	
	
}
