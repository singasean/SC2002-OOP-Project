import java.util.*;
/**
 * Represents a Company Representative user.
 * <p>
 * This class extends {@link User} and manages company-specific details.
 * It includes logic for account approval status (Pending/Approved) and tracks
 * the list of job postings (Internships) created by this representative.
 * </p>
 */
public class CompanyRepresentative extends User {
    /**
     * Constructs a new Company Representative.
     * Initial status is set to "Pending" by default.
     *
     * @param userID      The unique user ID.
     * @param name        The representative's name.
     * @param companyName The name of the company they represent.
     * @param department  The department they work in.
     * @param position    The job title.
     * @param email       The contact email.
     */
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
    /**
     * Checks if the representative's account has been approved by staff.
     *
     * @return {@code true} if status is "Approved".
     */
    public boolean isApproved() {
        return "Approved".equals(status);
    }

    public List<String> getInternshipIDs() {
        return new ArrayList<>(internshipIDs);
    }
    /**
     * Checks if the representative is allowed to post a new internship.
     * <p>
     * <b>Business Rule:</b> Requires the account to be approved and the number of
     * posted internships to be under the system limit.
     * </p>
     *
     * @return {@code true} if posting is allowed.
     */
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