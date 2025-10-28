import java.util.*;

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private String email;
    private String status;
    private final List<Internship> internships;
    private static final int MAX_INTERNSHIPS = 5;

    public CompanyRepresentative(String userID, String name, String password,
                                 String companyName, String department, String position, String email) {
        super(userID, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.email = email;
        this.status = "Pending"; // default = pending
        this.internships = new ArrayList<>();
    }
    //load details from csv.
    public static List<CompanyRepresentative> loadFromCSVLines(List<String> csvLines) {
        List<CompanyRepresentative> representatives = new ArrayList<>();
        for (int i = 1; i < csvLines.size(); i++) {
            String line = csvLines.get(i).trim();
            if (line.isEmpty()) continue; // Skip empty lines

            String[] data = line.split(",");
            if (data.length < 7) continue; // Skip incomplete data

            String id = data[0];
            String name = data[1];
            String companyName = data[2];
            String department = data[3];
            String position = data[4];
            String email = data[5];
            String status = data[6];
            String password = "password"; // Default password

            CompanyRepresentative rep = new CompanyRepresentative(
                    id, name, password, companyName, department, position, email
            );
            rep.setStatus(status);
            representatives.add(rep);
        }
        return representatives;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public List<Internship> getInternships() {
        return Collections.unmodifiableList(internships);
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean login(String inputUserID, String inputPassword) {
        if (!status.equalsIgnoreCase("Approved")) {
            System.out.println("Login failed! Your account is still pending approval from Career Center Staff.");
            return false;
        }
        return super.login(inputUserID, inputPassword);
    }

    public boolean hasReachedMaxInternships() {
        return internships.size() >= MAX_INTERNSHIPS;
    }

    public boolean addInternship(Internship internship) {
        if (hasReachedMaxInternships()) {
            System.out.println("Cannot add internship: reached maximum of " + MAX_INTERNSHIPS + " internships.");
            return false;
        }
        internships.add(internship);
        System.out.println("Internship '" + internship.getTitle() + "' added to your list.");
        return true;
    }

    public void viewInternships() {
        if (internships.isEmpty()) {
            System.out.println("No internship opportunities created yet.");
            return;
        }

        System.out.println("\nYour Internship Opportunities:");
        System.out.println("=".repeat(80));
        for (int i = 0; i < internships.size(); i++) {
            Internship internship = internships.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Level: " + internship.getLevel());
            System.out.println("-".repeat(80));
        }
    }

    public void viewInternshipDetails(Internship internship) {
        if (!internships.contains(internship)) {
            System.out.println("Error: You are not the representative for this internship.");
            return;
        }

        System.out.println("\nInternship Details:");
        System.out.println("Title: " + internship.getTitle());
        System.out.println("Level: " + internship.getLevel());
    }

    public boolean approveApplication(Internship internship, String studentID) {
        if (!internships.contains(internship)) {
            System.out.println("Error: You are not the representative for this internship.");
            return false;
        }

        String currentStatus = internship.getStatusForStudent(studentID);
        if (!currentStatus.equals("Pending")) {
            System.out.println("Error: Application has already been processed. Current status: " + currentStatus);
            return false;
        }

        internship.setStatusForStudent(studentID, "Successful");
        System.out.println("Application approved for student " + studentID);
        return true;
    }

    public boolean rejectApplication(Internship internship, String studentID) {
        if (!internships.contains(internship)) {
            System.out.println("Error: You are not the representative for this internship.");
            return false;
        }

        String currentStatus = internship.getStatusForStudent(studentID);
        if (!currentStatus.equals("Pending")) {
            System.out.println("Error: Application has already been processed. Current status: " + currentStatus);
            return false;
        }

        internship.setStatusForStudent(studentID, "Unsuccessful");
        System.out.println("Application rejected for student " + studentID);
        return true;
    }

    public boolean removeInternship(Internship internship) {
        if (!internships.contains(internship)) {
            System.out.println("Error: You are not the representative for this internship.");
            return false;
        }

        internships.remove(internship);
        System.out.println("Internship '" + internship.getTitle() + "' removed from your list.");
        return true;
    }

    @Override
    public String toString() {
        return "CompanyRepresentative{" +
                "userID='" + getUserID() + "'" +
                ", name='" + getName() + "'" +
                ", companyName='" + companyName + "'" +
                ", department='" + department + "'" +
                ", position='" + position + "'" +
                ", email='" + email + "'" +
                ", status='" + status + "'" +
                ", internships=" + internships.size() +
                '}';
    }
}
