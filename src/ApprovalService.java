// Single Responsibility - handles approval business logic
public class ApprovalService implements IApprovalService {
    private final IUserRepository<CompanyRepresentative> companyRepo;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;

    public ApprovalService(IUserRepository<CompanyRepresentative> companyRepo,
                          IInternshipRepository internshipRepo,
                          IOutputService outputService) {
        this.companyRepo = companyRepo;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
    }

    @Override
    public boolean approveCompanyRep(String repID) {
        CompanyRepresentative rep = companyRepo.getById(repID);
        if (rep == null) {
            return false;
        }
        rep.setStatus("Approved");
        outputService.displayMessage("Company representative approved: " + rep.getName());
        return true;
    }

    @Override
    public boolean rejectCompanyRep(String repID) {
        CompanyRepresentative rep = companyRepo.getById(repID);
        if (rep == null) {
            return false;
        }
        rep.setStatus("Rejected");
        outputService.displayMessage("Company representative rejected: " + rep.getName());
        return true;
    }

    @Override
    public boolean approveInternship(String internshipID) {
        Internship internship = internshipRepo.getById(internshipID);
        if (internship == null) {
            return false;
        }
        internship.setStatus("Approved");
        outputService.displayMessage("Internship approved: " + internship.getTitle());
        return true;
    }

    @Override
    public boolean rejectInternship(String internshipID) {
        Internship internship = internshipRepo.getById(internshipID);
        if (internship == null) {
            return false;
        }
        internship.setStatus("Rejected");
        outputService.displayMessage("Internship rejected: " + internship.getTitle());
        return true;
    }
}