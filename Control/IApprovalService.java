// Interface for approval operations
public interface IApprovalService {
    boolean approveCompanyRep(String repID);
    boolean rejectCompanyRep(String repID);
    boolean approveInternship(String internshipID);
    boolean rejectInternship(String internshipID);
}