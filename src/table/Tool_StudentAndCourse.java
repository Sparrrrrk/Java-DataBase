package table;

public interface Tool_StudentAndCourse {
	String insert(Object object);
	String delete(String key,Object object);
	String get(String key);
	String update(Object object);
}
