// Interface for application management
public interface IApplicationService {
    boolean applyForInternship(String studentID, String internshipID);
    boolean withdrawApplication(String studentID, String internshipID);
    boolean approveApplication(String internshipID, String studentID);
    boolean rejectApplication(String internshipID, String studentID);
}