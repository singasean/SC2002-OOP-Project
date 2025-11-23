import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        IOutputService outputService = new ConsoleOutputService();

        IUserRepository<Student> studentRepo = new UserRepository<>();
        IUserRepository<CompanyRepresentative> companyRepo = new UserRepository<>();
        IUserRepository<CareerCenterStaff> staffRepo = new UserRepository<>();
        IInternshipRepository internshipRepo = new InternshipRepository();

        IAuthenticationService authService = new AuthenticationService(outputService);
        ((AuthenticationService) authService).setCompanyRepository(companyRepo);

        IApplicationService applicationService = new ApplicationService(studentRepo, internshipRepo, outputService);
        IApprovalService approvalService = new ApprovalService(companyRepo, internshipRepo, outputService);
        IDataLoader dataLoader = new CSVDataLoader();
        IDataSaver dataSaver = new CSVDataSaver();

        MenuControllerFactory controllerFactory = new MenuControllerFactory(
                applicationService, approvalService, studentRepo, companyRepo,
                staffRepo, internshipRepo, outputService, authService, scanner);

        InternshipManagementSystem system = new InternshipManagementSystem(
                authService, studentRepo, companyRepo, staffRepo, internshipRepo,
                applicationService, approvalService, dataLoader, outputService,
                controllerFactory, scanner);

        system.loadInitialData(
                "sample_student_list.csv",
                "sample_company_representative_list.csv",
                "sample_staff_list.csv"
        );

        system.run();

        System.out.println("Saving data...");
        dataSaver.saveCompanyReps("sample_company_representative_list.csv", companyRepo.getAll());

        scanner.close();
    }
}
