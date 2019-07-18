package entity;
import java.io.Serializable;

public class Course implements Serializable {
	private String id;
	private String name;
	public int TotalSize;
	public int RemainSize;
	public int SelectedNum;
	private static final long serialVersionUID = 1460859984555261402L;
	
	public Course(String id, String name, int totalSize, int remainSize) {
        this.id = id;
        this.name = name;
        this.TotalSize = totalSize;
        this.RemainSize = remainSize;
        this.SelectedNum = totalSize-remainSize;
    }
	
	public String getId() {
        return id;
    }
	
	public String getName() {
        return name;
    }
	
	public int getTotalSize() {
        return TotalSize;
    }
	
	public int getRemainSize() {
        return RemainSize;
    }
	
	@Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", totalSize=" + TotalSize +
                ", remainSize=" + RemainSize +
                ", selectedNum" + SelectedNum+
                '}';
    }
}
