import java.util.*;

/**
 * Represents a specific Internship job posting.
 * <p>
 * This is a core entity class that acts as a <b>State Machine</b> for the application process.
 * It tracks not only the job details (slots, dates) but also the specific status of
 * every student who has applied via the {@code statusByStudent} map.
 * </p>
 */
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
    /**
     * Constructs a new Internship posting.
     * Initial status is "Pending" (awaiting Staff approval).
     */
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
    /**
     * Checks if there are vacancy slots available.
     *
     * @return {@code true} if confirmed slots < total slots.
     */
    public boolean hasAvailableSlots() {
        return confirmedSlots < totalSlots;
    }
    /**
     * Increments the count of confirmed slots.
     * Updates the status to "Filled" if the limit is reached.
     */
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
    /**
     * Updates the application status for a specific student.
     *
     * @param studentID The ID of the student.
     * @param status    The new status (e.g., "Pending", "Approved", "Withdrawn").
     */
    public void setStudentStatus(String studentID, String status) {
        statusByStudent.put(studentID, status);
    }

    public String getStudentStatus(String studentID) {
        return statusByStudent.getOrDefault(studentID, "Not Applied");
    }

    public Map<String, String> getAllStudentStatuses() {
        return new HashMap<>(statusByStudent);
    }
    /**
     * Initiates a withdrawal request for a student.
     * Stores the reason for withdrawal and updates status to "Pending Withdrawal".
     *
     * @param studentID The ID of the requesting student.
     * @param reason    The reason provided for withdrawal.
     */
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
