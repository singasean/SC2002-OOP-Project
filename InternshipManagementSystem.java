import java.util.*;

// Main application class - uses Dependency Injection
public class InternshipManagementSystem {
    private final IAuthenticationService authService;
    private final IUserRepository<Student> studentRepo;
    private final IUserRepository<CompanyRepresentative> companyRepo;
    private final IUserRepository<CareerCenterStaff> staffRepo;
    private final IInternshipRepository internshipRepo;
    private final IApplicationService applicationService;
    private final IApprovalService approvalService;
    private final IDataLoader dataLoader;
    private final IOutputService outputService;
    private final MenuControllerFactory controllerFactory;
    private final Scanner scanner;

    public InternshipManagementSystem(IAuthenticationService authService,
                                      IUserRepository<Student> studentRepo,
                                      IUserRepository<CompanyRepresentative> companyRepo,
                                      IUserRepository<CareerCenterStaff> staffRepo,
                                      IInternshipRepository internshipRepo,
                                      IApplicationService applicationService,
                                      IApprovalService approvalService,
                                      IDataLoader dataLoader,
                                      IOutputService outputService,
                                      MenuControllerFactory controllerFactory,
                                      Scanner scanner) {
        this.authService = authService;
        this.studentRepo = studentRepo;
        this.companyRepo = companyRepo;
        this.staffRepo = staffRepo;
        this.internshipRepo = internshipRepo;
        this.applicationService = applicationService;
        this.approvalService = approvalService;
        this.dataLoader = dataLoader;
        this.outputService = outputService;
        this.controllerFactory = controllerFactory;
        this.scanner = scanner;
    }

    public void loadInitialData(String studentCSV, String companyCSV, String staffCSV) {
        List<Student> students = dataLoader.loadStudents(studentCSV);
        for (Student s : students) {
            studentRepo.add(s);
            authService.registerUser(s.getUserID(), "password");
        }

        List<CompanyRepresentative> reps = dataLoader.loadCompanyReps(companyCSV);
        for (CompanyRepresentative r : reps) {
            companyRepo.add(r);
            authService.registerUser(r.getUserID(), "password");
        }

        List<CareerCenterStaff> staff = dataLoader.loadStaff(staffCSV);
        for (CareerCenterStaff s : staff) {
            staffRepo.add(s);
            authService.registerUser(s.getUserID(), "password");
        }

        outputService.displayMessage("Data loaded successfully!");
    }

    public void run() {
        boolean running = true;

        while (running) {
            outputService.displayMessage("\n===== Internship Management System =====");
            outputService.displayMessage("1. Student Login");
            outputService.displayMessage("2. Company Representative Login");
            outputService.displayMessage("3. Career Center Staff Login");
            outputService.displayMessage("4. Register Company Representative");
            outputService.displayMessage("5. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": handleLogin(studentRepo); break;
                case "2": handleLogin(companyRepo); break;
                case "3": handleLogin(staffRepo); break;
                case "4": registerCompanyRep(); break;
                case "5": running = false; outputService.displayMessage("Goodbye!"); break;
                default: outputService.displayError("Invalid choice!");
            }
        }
    }

    private <T extends User> void handleLogin(IUserRepository<T> repo) {
        outputService.displayMessage("Enter User ID:");
        String userID = scanner.nextLine();
        outputService.displayMessage("Enter Password:");
        String password = scanner.nextLine();

        if (authService.authenticate(userID, password)) {
            T user = repo.getById(userID);
            if (user != null) {
                runUserSession(user);
                authService.logout(userID);
            }
        }
    }

    private void runUserSession(User user) {
        IMenuController controller = controllerFactory.createController(user);
        boolean loggedIn = true;

        while (loggedIn) {
            controller.displayMenu();
            String choice = scanner.nextLine();

            if (isLogoutChoice(choice, user)) {
                loggedIn = false;
            } else {
                controller.handleInput(choice);
            }
        }
    }

    private boolean isLogoutChoice(String choice, User user) {
        if (user instanceof Student) return "6".equals(choice);
        if (user instanceof CompanyRepresentative) return "4".equals(choice);
        if (user instanceof CareerCenterStaff) return "5".equals(choice);
        return false;
    }

    private void registerCompanyRep() {
        outputService.displayMessage("Enter your name:");
        String name = scanner.nextLine();
        outputService.displayMessage("Enter company name:");
        String company = scanner.nextLine();
        outputService.displayMessage("Enter department:");
        String dept = scanner.nextLine();
        outputService.displayMessage("Enter position:");
        String position = scanner.nextLine();
        outputService.displayMessage("Enter email:");
        String email = scanner.nextLine();
        outputService.displayMessage("Enter password:");
        String password = scanner.nextLine();

        String id = "CR" + (companyRepo.getAll().size() + 1);
        CompanyRepresentative rep = new CompanyRepresentative(id, name, company, dept, position, email);

        companyRepo.add(rep);
        authService.registerUser(id, password);
        outputService.displayMessage("Registration successful! Your ID: " + id + " (Pending approval)");
    }
}
