import java.util.*;

// Factory Pattern - creates appropriate controllers
// Open-Closed - can add new controller types without modifying existing code
public class MenuControllerFactory {
    private final IApplicationService applicationService;
    private final IApprovalService approvalService;
    private final IUserRepository<Student> studentRepo;
    private final IUserRepository<CompanyRepresentative> companyRepo;
    private final IUserRepository<CareerCenterStaff> staffRepo;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;
    private final Scanner scanner;

    public MenuControllerFactory(IApplicationService applicationService,
                                IApprovalService approvalService,
                                IUserRepository<Student> studentRepo,
                                IUserRepository<CompanyRepresentative> companyRepo,
                                IUserRepository<CareerCenterStaff> staffRepo,
                                IInternshipRepository internshipRepo,
                                IOutputService outputService,
                                Scanner scanner) {
        this.applicationService = applicationService;
        this.approvalService = approvalService;
        this.studentRepo = studentRepo;
        this.companyRepo = companyRepo;
        this.staffRepo = staffRepo;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
        this.scanner = scanner;
    }

    // Polymorphism - returns appropriate controller based on user type
    public IMenuController createController(User user) {
        if (user instanceof Student) {
            return new StudentMenuController((Student) user, applicationService,
                internshipRepo, outputService, scanner);
        } else if (user instanceof CompanyRepresentative) {
            return new CompanyRepMenuController((CompanyRepresentative) user,
                internshipRepo, applicationService, outputService, scanner);
        } else if (user instanceof CareerCenterStaff) {
            return new StaffMenuController((CareerCenterStaff) user, approvalService,
                companyRepo, studentRepo, internshipRepo, outputService, scanner);
        }
        throw new IllegalArgumentException("Unknown user type");
    }
}