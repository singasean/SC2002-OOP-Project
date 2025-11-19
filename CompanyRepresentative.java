import java.util.*;

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private String email;
    private String status;
    private final List<String> internshipIDs;
    private static final int MAX_INTERNSHIPS = 5;

    public CompanyRepresentative(String userID, String name, String companyName,
                                 String department, String position, String email) {
        super(userID, name);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.email = email;
        this.status = "Pending";
        this.internshipIDs = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "Company Representative";
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

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isApproved() {
        return "Approved".equals(status);
    }

    public List<String> getInternshipIDs() {
        return new ArrayList<>(internshipIDs);
    }

    public boolean canPostInternship() {
        return isApproved() && internshipIDs.size() < MAX_INTERNSHIPS;
    }

    public boolean addInternship(String internshipID) {
        if (canPostInternship()) {
            internshipIDs.add(internshipID);
            return true;
        }
        return false;
    }

    public void removeInternship(String internshipID) {
        internshipIDs.remove(internshipID);
    }
}