import java.util.*;

// Single Responsibility - handles student menu logic only
public class StudentMenuController implements IMenuController {
    private final Student currentStudent;
    private final IApplicationService applicationService;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;
    private final Scanner scanner;

    public StudentMenuController(Student student,
                                IApplicationService applicationService,
                                IInternshipRepository internshipRepo,
                                IOutputService outputService,
                                Scanner scanner) {
        this.currentStudent = student;
        this.applicationService = applicationService;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
        this.scanner = scanner;
    }

    @Override
    public void displayMenu() {
        outputService.displayMessage("\n===== Student Menu =====");
        outputService.displayMessage("1. View Available Internships");
        outputService.displayMessage("2. Apply for Internship");
        outputService.displayMessage("3. View My Applications");
        outputService.displayMessage("4. Withdraw Application");
        outputService.displayMessage("5. Toggle Profile Visibility");
        outputService.displayMessage("6. Logout");
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                viewAvailableInternships();
                break;
            case "2":
                applyForInternship();
                break;
            case "3":
                viewMyApplications();
                break;
            case "4":
                withdrawApplication();
                break;
            case "5":
                toggleVisibility();
                break;
            case "6":
                outputService.displayMessage("Logging out...");
                break;
            default:
                outputService.displayError("Invalid choice!");
        }
    }

    private void viewAvailableInternships() {
        List<Internship> internships = internshipRepo.getAll();
        List<Internship> available = new ArrayList<>();

        for (Internship i : internships) {
            if ("Approved".equals(i.getStatus()) && i.isVisible() && i.hasAvailableSlots()) {
                available.add(i);
            }
        }

        if (available.isEmpty()) {
            outputService.displayMessage("No internships available.");
            return;
        }

        for (Internship i : available) {
            outputService.displayMessage(String.format("%s - %s (%s) [%s]",
                i.getInternshipID(), i.getTitle(), i.getCompanyName(), i.getLevel()));
        }
    }

    private void applyForInternship() {
        outputService.displayMessage("Enter Internship ID:");
        String internshipID = scanner.nextLine();
        applicationService.applyForInternship(currentStudent.getUserID(), internshipID);
    }

    private void viewMyApplications() {
        List<String> appIDs = currentStudent.getApplicationIDs();
        if (appIDs.isEmpty()) {
            outputService.displayMessage("No applications found.");
            return;
        }

        for (String id : appIDs) {
            Internship i = internshipRepo.getById(id);
            if (i != null) {
                String status = i.getStudentStatus(currentStudent.getUserID());
                outputService.displayMessage(String.format("%s - %s [Status: %s]",
                    id, i.getTitle(), status));
            }
        }
    }

    private void withdrawApplication() {
        outputService.displayMessage("Enter Internship ID to withdraw:");
        String internshipID = scanner.nextLine();
        applicationService.withdrawApplication(currentStudent.getUserID(), internshipID);
    }

    private void toggleVisibility() {
        currentStudent.setVisible(!currentStudent.isVisible());
        outputService.displayMessage("Profile visibility: " + 
            (currentStudent.isVisible() ? "ON" : "OFF"));
    }
}