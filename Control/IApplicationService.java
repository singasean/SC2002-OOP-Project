public interface IApplicationService {
    boolean applyForInternship(String studentID, String internshipID);
    boolean withdrawApplication(String studentID, String internshipID);
    boolean approveApplication(String internshipID, String studentID);
    boolean rejectApplication(String internshipID, String studentID);
    boolean requestWithdrawal(String studentID, String internshipID, String reason);
    boolean approveWithdrawal(String internshipID, String studentID);
    boolean rejectWithdrawal(String internshipID, String studentID);
}
