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
        // Load students
        List<Student> students = dataLoader.loadStudents(studentCSV);
        for (Student s : students) {
            studentRepo.add(s);
            authService.registerUser(s.getUserID(), "password");
        }

        // Load company reps - REGISTER EMAILS!
        List<CompanyRepresentative> reps = dataLoader.loadCompanyReps(companyCSV);
        for (CompanyRepresentative r : reps) {
            companyRepo.add(r);
            authService.registerUser(r.getUserID(), "password");
            authService.registerEmail(r.getEmail(), r.getUserID()); // NEW LINE!
        }

        // Load staff
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
            outputService.displayMessage("1. Student Login (use Student ID)");
            outputService.displayMessage("2. Company Representative Login (use Email)");  // Updated
            outputService.displayMessage("3. Career Center Staff Login (use Staff ID)");
            outputService.displayMessage("4. Register Company Representative");
            outputService.displayMessage("5. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLogin(studentRepo);
                    break;
                case "2":
                    handleLogin(companyRepo);
                    break;
                case "3":
                    handleLogin(staffRepo);
                    break;
                case "4":
                    registerCompanyRep();
                    break;
                case "5":
                    running = false;
                    outputService.displayMessage("Goodbye!");
                    break;
                default:
                    outputService.displayError("Invalid choice!");
            }
        }
    }


    private <T extends User> void handleLogin(IUserRepository<T> repo) {
        outputService.displayMessage("Enter User ID or Email:");  // Updated prompt
        String userIDOrEmail = scanner.nextLine();
        outputService.displayMessage("Enter Password:");
        String password = scanner.nextLine();

        if (authService.authenticate(userIDOrEmail, password)) {
            // Get actual userID (in case email was entered)
            String userID = userIDOrEmail;
            if (userIDOrEmail.contains("@")) {
                userID = authService.getUserIDFromEmail(userIDOrEmail);
            }

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
        if (user instanceof Student) return "8".equals(choice);
        if (user instanceof CompanyRepresentative) return "7".equals(choice);
        if (user instanceof CareerCenterStaff) return "8".equals(choice);
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

        // Email validation with specific error messages
        String email;
        while (true) {
            outputService.displayMessage("Enter email (format: name@example.com):");
            email = scanner.nextLine().trim();

            String validationError = validateEmail(email);
            if (validationError == null) {
                break;  // Email is valid
            } else {
                outputService.displayError(validationError);
            }
        }

        outputService.displayMessage("Enter password:");
        String password = scanner.nextLine();

        String id = "CR" + (companyRepo.getAll().size() + 1);
        CompanyRepresentative rep = new CompanyRepresentative(id, name, company, dept, position, email);

        companyRepo.add(rep);
        authService.registerUser(id, password);
        authService.registerEmail(email, id);
        outputService.displayMessage("Registration successful! Login with your email: " + email + " (Pending approval)");
    }

    // Returns null if valid, or error message if invalid
    private String validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Email cannot be empty!";
        }

        if (email.contains(" ")) {
            return "Email cannot contain spaces!";
        }

        if (!email.contains("@")) {
            return "Email must contain @ symbol! (e.g., user@company.com)";
        }

        int atIndex = email.indexOf("@");

        if (atIndex == 0) {
            return "Email must have characters before @ symbol!";
        }

        if (atIndex == email.length() - 1) {
            return "Email must have domain after @ symbol!";
        }

        // Check for multiple @ symbols
        if (email.indexOf("@") != email.lastIndexOf("@")) {
            return "Email can only have one @ symbol!";
        }

        String afterAt = email.substring(atIndex + 1);

        if (!afterAt.contains(".")) {
            return "Email domain must contain a dot! (e.g., @company.com)";
        }

        int dotIndex = afterAt.lastIndexOf(".");
        if (dotIndex == afterAt.length() - 1) {
            return "Email must have extension after dot! (e.g., .com, .org)";
        }

        if (dotIndex == 0) {
            return "Email domain must have name before dot!";
        }

        return null;  // Email is valid
    }

}
