import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create all dependencies (Dependency Injection setup)
        IOutputService outputService = new ConsoleOutputService();

        // Create repositories
        IUserRepository<Student> studentRepo = new UserRepository<>();
        IUserRepository<CompanyRepresentative> companyRepo = new UserRepository<>();
        IUserRepository<CareerCenterStaff> staffRepo = new UserRepository<>();
        IInternshipRepository internshipRepo = new InternshipRepository();

        // Create services
        IAuthenticationService authService = new AuthenticationService(outputService);
        IApplicationService applicationService = new ApplicationService(studentRepo, internshipRepo, outputService);
        IApprovalService approvalService = new ApprovalService(companyRepo, internshipRepo, outputService);
        IDataLoader dataLoader = new CSVDataLoader();

        // Create factory
        MenuControllerFactory controllerFactory = new MenuControllerFactory(
                applicationService, approvalService, studentRepo, companyRepo,
                staffRepo, internshipRepo, outputService, scanner);

        // Create main application
        InternshipManagementSystem system = new InternshipManagementSystem(
                authService, studentRepo, companyRepo, staffRepo, internshipRepo,
                applicationService, approvalService, dataLoader, outputService,
                controllerFactory, scanner);

        // Load initial data
        system.loadInitialData(
                "sample_student_list.csv",
                "sample_company_representative_list.csv",
                "sample_staff_list.csv"
        );

        // Run the application
        system.run();

        scanner.close();
    }
}
