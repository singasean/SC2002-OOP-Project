import java.util.*;

public class Student extends User {
    private int yearOfStudy;
    private String major;
    private boolean isVisible = true;
    private final List<String> applicationIDs; // Store IDs instead of objects
    private String acceptedPlacementID;
    private final Set<String> withdrawalRequestedIDs;
    private static final int MAX_APPLICATIONS = 3;

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

    public boolean canApply() {
        return applicationIDs.size() < MAX_APPLICATIONS && acceptedPlacementID == null;
    }

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