package entity;

import java.io.Serializable;

public class Choice implements Serializable {
	private String student_id;
	private String course_id;
	private String time;
    private static final long serialVersionUID = -3219278370156937654L;

	public Choice(String student_id, String course_id, String time) {
        this.student_id = student_id;
        this.course_id = course_id;
        this.time = time;
    }
	
	public Choice(String student_id, String course_id) {
        this.student_id = student_id;
        this.course_id = course_id;
    }
	
	public String getTime() {
        return time;
    }
	
	public String getStudent_id() {
        return student_id;
    }
	
	public String getCourse_id() {
        return course_id;
    }
	
	@Override
    public String toString() {
        return "Choice{" +
                "student_id='" + student_id + '\'' +
                ", course_id='" + course_id + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
