package sc2002OOPproject;

import java.util.HashMap;
import java.util.Map;

public class Internship {
	private final String title;
	private final String level;
	
	private final Map<String, String> statusByStudent = new HashMap<>();
	
	public Internship(String title, String level) {
		this.title = title;
		this.level = level;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void addApplicant(Student s) {
		statusByStudent.putIfAbsent(s.getUserID(), "Pending");
	}
	
	public void setStatusForStudent(String studentID, String status) {
		if(status != null) {
			statusByStudent.put(studentID, status);
		}
	}
}
