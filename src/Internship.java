import java.util.*;

public class Internship {
    private String internshipID;
    private String title;
    private String description;
    private String level;
    private String preferredMajor;
    private String openingDate;
    private String closingDate;
    private String status;
    private String companyName;
    private String representativeID;
    private int totalSlots;
    private int confirmedSlots;
    private boolean isVisible = true;
    private final Map<String, String> statusByStudent;
    private final Map<String, String> withdrawalReasons;

    public Internship(String internshipID, String title, String description, String level,
                      String preferredMajor, String openingDate, String closingDate,
                      int totalSlots, String companyName, String representativeID) {
        this.internshipID = internshipID;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.totalSlots = totalSlots;
        this.companyName = companyName;
        this.representativeID = representativeID;
        this.status = "Pending";
        this.confirmedSlots = 0;
        this.statusByStudent = new HashMap<>();
        this.withdrawalReasons = new HashMap<>();
    }

    public String getInternshipID() { return internshipID; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLevel() { return level; }
    public String getPreferredMajor() { return preferredMajor; }
    public String getOpeningDate() { return openingDate; }
    public String getClosingDate() { return closingDate; }
    public String getStatus() { return status; }
    public String getCompanyName() { return companyName; }
    public String getRepresentativeID() { return representativeID; }
    public int getTotalSlots() { return totalSlots; }
    public int getConfirmedSlots() { return confirmedSlots; }
    public boolean isVisible() { return isVisible; }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean hasAvailableSlots() {
        return confirmedSlots < totalSlots;
    }

    public void incrementConfirmedSlots() {
        if (hasAvailableSlots()) {
            confirmedSlots++;
            if (confirmedSlots >= totalSlots) {
                status = "Filled";
            }
        }
    }

    public void decrementConfirmedSlots() {
        if (confirmedSlots > 0) {
            confirmedSlots--;
            if ("Filled".equals(status)) {
                status = "Approved";
            }
        }
    }

    public void setStudentStatus(String studentID, String status) {
        statusByStudent.put(studentID, status);
    }

    public String getStudentStatus(String studentID) {
        return statusByStudent.getOrDefault(studentID, "Not Applied");
    }

    public Map<String, String> getAllStudentStatuses() {
        return new HashMap<>(statusByStudent);
    }

    public void requestWithdrawal(String studentID, String reason) {
        setStudentStatus(studentID, "Pending Withdrawal");
        withdrawalReasons.put(studentID, reason != null ? reason : "No reason provided");
    }

    public boolean approveWithdrawal(String studentID) {
        if ("Pending Withdrawal".equals(getStudentStatus(studentID))) {
            setStudentStatus(studentID, "Withdrawn");
            withdrawalReasons.remove(studentID);
            decrementConfirmedSlots();
            return true;
        }
        return false;
    }

    public boolean rejectWithdrawal(String studentID) {
        if ("Pending Withdrawal".equals(getStudentStatus(studentID))) {
            setStudentStatus(studentID, "Confirmed");
            withdrawalReasons.remove(studentID);
            return true;
        }
        return false;
    }

    public List<String> getPendingWithdrawalStudents() {
        List<String> students = new ArrayList<>();
        for (Map.Entry<String, String> entry : statusByStudent.entrySet()) {
            if ("Pending Withdrawal".equals(entry.getValue())) {
                students.add(entry.getKey());
            }
        }
        return students;
    }

    public String getWithdrawalReason(String studentID) {
        return withdrawalReasons.getOrDefault(studentID, "No reason provided");
    }
}
