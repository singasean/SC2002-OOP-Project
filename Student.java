import java.util.*;

public class Student extends User{
    private int yearOfStudy;
    private String major;
    private boolean isVisible = true;
    private final List<Internship> applications;
    private Internship acceptedPlacement;
    private final Set<Internship> withdrawalRequested;
    private static final int MAX_APPLICATIONS = 3;

    public Student(String userID, String name, String password, int yearOfStudy, String major) {
        super(userID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.applications = new ArrayList<>();
        this.withdrawalRequested = new HashSet<>();
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

    public boolean isVisible(){
        return isVisible;
    }

    public Internship getAcceptedPlacement(){
        return acceptedPlacement;
    }

    public List<Internship> getApplications(){
        return Collections.unmodifiableList(applications);
    }

    //setters
    public void setYearOfStudy(int yearOfStudy){
        this.yearOfStudy = yearOfStudy;
    }

    public void setMajor(String major){
        this.major = major;
    }

    public void setVisible(boolean visible){
        this.isVisible = visible;
    }

    public void toggleVisibility(){
        this.isVisible = !this.isVisible;
        System.out.println("Profile visibility set to " + (this.isVisible ?"ON" : "OFF"));
    }

    //helper checks
    public boolean hasReachedMaxApplications(){
        return applications.size() >= MAX_APPLICATIONS;
    }

    public boolean hasAppliedTo(Internship internship){
        return applications.contains(internship);
    }

    public boolean canApplyToLevel(String level){
        if (level == null) return true;
        return (yearOfStudy >= 3) || level.equalIgnoreCase("Basic");
    }

    public void applyForInternship(Internship internship) {
        if(!isVisible){
            System.out.println("Cannot apply: your profile visibility is OFF.");
            return;
        }
        
        if (hasReachedMaxApplications()) {
            System.out.println("Cannot apply: reached max " + MAX_APPLICATIONS + " applications.");
            return;
        }
        if (hasAppliedTo(internship)){
            System.out.println("You have already applied to " + internship.getTitle() + ".");
            return;
        }
        if (!canApplyToLevel(internship.getLevel())) {
            System.out.println("Cannot apply: Year 1-2 students can only apply for Basic-level internships.");
            return;
        }
        applications.add(internship);
        internship.addApplicant(this);
        System.out.println("Applied to " + internship.getTitle() + " successfully!");
    }

    public boolean acceptPlacement(Internship internship){
        if(!applications.contains(internship)){
            System.out.println("You have not applied to " + internship.getTitle() + ".");
            return false;
        }
        if (acceptedPlacement != null){
            System.out.println("You have already accepted a placement: " + acceptedPlacement.getTitle() + ".");
            return false;
        }
        String status = internship.getStatusForStudent(this.getUserID());
        if(!"Successful".equalsIgnoreCase(status)){
            System.out.println("Cannot accept: current status is \"" + status + "\" (needs to be \"Successful\"). ");
            return false;
        }

        acceptedPlacement = internship;

        for (Internship other : applications){
            if (other != internship){
                withdrawalRequested.add(other);
            }
        }
        System.out.println("Placement accepted for " + internship.getTitle() + ". All other applications have been marked for withdrawal.");
        return true;
    }

    public boolean requestWithdrawal(Internship internship){
        if(!applications.contains(internship)){
            System.out.println("No application found for " + internship.getTitle() + ".");
            return false;
        }
        if (withdrawalRequested.contains(internship)){
            System.out.println("Withdrawal has already been requested for " + internship.getTitle() + ".");
            return false;   
        }
        withdrawalRequested.add(internship);
        System.out.println("Withdrawal request submitted for " + internship.getTitle() + ". Awaiting Career Center Staff approval.");
        return true;
    }

    public boolean hasRequestedWithdrawal(Internship internship){
        return withdrawalRequested.contains(internship);
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
