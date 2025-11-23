// Single Responsibility - handles approval business logic
/**
 * Service class responsible for administrative approval workflows.
 * <p>
 * This service is primarily used by {@link CareerCenterStaff} to moderate the system.
 * It handles the approval chains for both new User accounts (Company Reps) and
 * new Content (Internship postings).
 * </p>
 * <p>
 * This class adheres to the <b>Single Responsibility Principle</b> by focusing solely
 * on the state transitions (Approved/Rejected) of system entities, separate from
 * the logic of creating them or applying to them.
 * </p>
 */
public class ApprovalService implements IApprovalService {
    private final IUserRepository<CompanyRepresentative> companyRepo;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;
    /**
     * Constructs the ApprovalService with necessary repositories.
     *
     * @param companyRepo    Repository to access Company Representative data.
     * @param internshipRepo Repository to access Internship data.
     * @param outputService  Service to display confirmation messages.
     */
    public ApprovalService(IUserRepository<CompanyRepresentative> companyRepo,
                          IInternshipRepository internshipRepo,
                          IOutputService outputService) {
        this.companyRepo = companyRepo;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
    }
    /**
     * Approves a pending Company Representative account.
     * <p>
     * <b>Effect:</b> The representative's status is set to "Approved".
     * This unlocks their account, allowing them to log in via the {@link AuthenticationService}
     * and post internships.
     * </p>
     *
     * @param repID The ID of the representative to approve.
     * @return {@code true} if the representative was found and approved; {@code false} otherwise.
     */
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
    /**
     * Rejects a pending Company Representative account.
     * <p>
     * <b>Effect:</b> The representative's status is set to "Rejected".
     * They will be denied login access.
     * </p>
     *
     * @param repID The ID of the representative to reject.
     * @return {@code true} if the representative was found and rejected; {@code false} otherwise.
     */
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
    /**
     * Approves a pending Internship posting.
     * <p>
     * <b>Effect:</b> The internship's status is set to "Approved".
     * This makes the internship visible to Students in the {@link StudentMenuController},
     * provided the visibility flag is also true.
     * </p>
     *
     * @param internshipID The ID of the internship.
     * @return {@code true} if the internship was found and approved; {@code false} otherwise.
     */
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
    /**
     * Rejects a pending Internship posting.
     * <p>
     * <b>Effect:</b> The internship's status is set to "Rejected".
     * It will not appear in student searches.
     * </p>
     *
     * @param internshipID The ID of the internship.
     * @return {@code true} if the internship was found and rejected; {@code false} otherwise.
     */
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