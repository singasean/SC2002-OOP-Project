import java.util.HashMap;
import java.util.Map;

public class Internship {
    private String title;
    private String description;
    private String level;
    private String preferredMajor;
    private String openingDate;
    private String closingDate;
    private String status; // "Pending", "Approved", "Rejected", "Filled"
    private String companyName;
    private String representativeID;
    private int totalSlots;
    private int confirmedSlots;
    private boolean isVisible = true; // Default visibility is ON

    private final Map<String, String> statusByStudent;

    // NEW Constructor with all required fields
    public Internship(String title, String description, String level,
                      String preferredMajor, String openingDate, String closingDate,
                      int totalSlots, String companyName, String representativeID) {
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.totalSlots = totalSlots;
        this.companyName = companyName;
        this.representativeID = representativeID;
        this.status = "Pending"; // Default status
        this.confirmedSlots = 0;
        this.statusByStudent = new HashMap<>();
    }
    public String getTitle() {
        return title;
    }

    public String getLevel() {
        return level;
    }
    public String getStatusForStudent(String studentID) {
        return statusByStudent.getOrDefault(studentID, "Not Applied");
    }

    public void addApplicant(Student s) {
        statusByStudent.putIfAbsent(s.getUserID(), "Pending");
    }

    public void setStatusForStudent(String studentID, String status) {
        if(status != null) {
            statusByStudent.put(studentID, status);
        }
    }
    // Add these getters to Internship.java
    public String getDescription() {
        return description;
    }

    public String getPreferredMajor() {
        return preferredMajor;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRepresentativeID() {
        return representativeID;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public int getConfirmedSlots() {
        return confirmedSlots;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // Visibility getters and setters
    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public void toggleVisibility() {
        this.isVisible = !this.isVisible;
    }
    public void incrementConfirmedSlots() {
        if (confirmedSlots < totalSlots) {
            confirmedSlots++;
        }
    }
}
