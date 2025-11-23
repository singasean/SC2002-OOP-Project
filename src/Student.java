import java.util.*;
/**
 * Represents a Student user in the system.
 * <p>
 * This class extends {@link User} and adds specific academic attributes and
 * application tracking capabilities. It manages the state of the student's
 * internship applications and their final placement acceptance.
 * </p>
 */
public class Student extends User {
    private int yearOfStudy;
    private String major;
    private boolean isVisible = true;
    private final List<String> applicationIDs; // Store IDs instead of objects
    private String acceptedPlacementID;
    private final Set<String> withdrawalRequestedIDs;
    private static final int MAX_APPLICATIONS = 3;
    /**
     * Constructs a new Student.
     *
     * @param userID      The unique student ID (e.g., "STU001").
     * @param name        The student's full name.
     * @param yearOfStudy The current academic year (1-4).
     * @param major       The student's major (e.g., "Computer Science").
     */
    public Student(String userID, String name, int yearOfStudy, String major) {
        super(userID, name);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.applicationIDs = new ArrayList<>();
        this.withdrawalRequestedIDs = new HashSet<>();
    }

    @Override
    public String getRole() {
        return "Student";
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public String getMajor() {
        return major;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public List<String> getApplicationIDs() {
        return new ArrayList<>(applicationIDs);
    }
    /**
     * Checks if the student is eligible to apply for a new internship.
     * <p>
     * <b>Business Rule:</b> A student cannot apply if they have already accepted a placement
     * or if they have reached the maximum allowed concurrent applications.
     * </p>
     *
     * @return {@code true} if eligible; {@code false} otherwise.
     */
    public boolean canApply() {
        return applicationIDs.size() < MAX_APPLICATIONS && acceptedPlacementID == null;
    }
    /**
     * Records a new application for this student.
     *
     * @param internshipID The ID of the internship being applied for.
     */
    public boolean addApplication(String internshipID) {
        if (canApply()) {
            applicationIDs.add(internshipID);
            return true;
        }
        return false;
    }

    public void removeApplication(String internshipID) {
        applicationIDs.remove(internshipID);
    }

    public String getAcceptedPlacementID() {
        return acceptedPlacementID;
    }
    /**
     * Sets the confirmed placement for this student.
     *
     * @param internshipID The ID of the internship the student has accepted.
     */
    public void setAcceptedPlacement(String internshipID) {
        this.acceptedPlacementID = internshipID;
    }

    public void requestWithdrawal(String internshipID) {
        withdrawalRequestedIDs.add(internshipID);
    }

    public boolean hasWithdrawalRequest(String internshipID) {
        return withdrawalRequestedIDs.contains(internshipID);
    }

    public void clearWithdrawalRequest(String internshipID) {
        withdrawalRequestedIDs.remove(internshipID);
    }
}