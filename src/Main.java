import java.util.*;
/**
 * The Entry Point of the Internship Management System (IMS).
 * <p>
 * <b>Architectural Role:</b>
 * This class acts as the <b>Composition Root</b>. In a Dependency Injection architecture,
 * objects should not create their own dependencies (e.g., a Controller shouldn't say {@code new Repository()}).
 * Instead, this Main class creates <i>everything</i> and wires them together.
 * </p>
 * <p>
 * <b>Responsibilities:</b>
 * <ol>
 * <li><b>Instantiation:</b> Creates concrete instances of all Services, Repositories, and Utilities.</li>
 * <li><b>Data Loading:</b> Triggers the {@link CSVDataLoader} to populate memory from files.</li>
 * <li><b>Dependency Injection:</b> Passes these instances into constructors (e.g., giving the Repo to the Service).</li>
 * <li><b>Lifecycle Management:</b> Registers the Shutdown Hook to ensure data is saved via {@link CSVDataSaver} on exit.</li>
 * </ol>
 * </p>
 */
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
