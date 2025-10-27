import java.util.*;

public class Student extends User{
    private int yearOfStudy;
    private String major;
    private List<Internship> applications;
    private static final int MAX_APPLICATIONS = 3;

    public Student(String userID, String name, String password, int yearOfStudy, String major) {
        super(userID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.applications = new ArrayList<>();
    }

    //load student details
    public static List<Student> loadFromCSVLines(List<String> csvLines) {
        List<Student> students = new ArrayList<>();
        for (int i = 1; i < csvLines.size(); i++) {
            String[] data = csvLines.get(i).split(",");
            String id = data[0];
            String name = data[1];
            String major = data[2];
            int year = Integer.parseInt(data[3]);
            String password = "password";
            students.add(new Student(id, name, password, year, major));
        }
        return students;
    }

    //getters
    public int getYearOfStudy() { 
        return yearOfStudy; 
    }
    
    public String getMajor() { 
        return major; 
    }

    public void applyForInternship(Internship internship) {
        if (applications.size() >= MAX_APPLICATIONS) {
            System.out.println("Cannot apply: reached max " + MAX_APPLICATIONS + " applications.");
            return;
        }
        if (yearOfStudy < 3 && !internship.getLevel().equalsIgnoreCase("Basic")) {
            System.out.println("Cannot apply: Year 1-2 students can only apply for Basic-level internships.");
            return;
        }
        applications.add(internship);
        internship.addApplicant(this);
        System.out.println("Applied to " + internship.getTitle() + " successfully!");
    }

    public void viewApplications() {
        if (applications.isEmpty()) {
            System.out.println("No applications yet.");
            return;
        }
        for (Internship i : applications) {
            System.out.println(i.getTitle() + " | Status: " + i.getStatusForStudent(this.getUserID()));
        }
    }
    
}
