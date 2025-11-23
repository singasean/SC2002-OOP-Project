import java.util.*;
/**
 * Controller class that manages the User Interface for Students.
 * <p>
 * This class acts as a <b>Boundary</b> between the User (via Console) and the System Logic.
 * It interprets user input commands (e.g., "Apply", "Filter") and delegates the actual
 * processing to {@link ApplicationService} or {@link InternshipRepository}.
 * </p>
 * <p>
 * It maintains the state of the user's current session, such as their active
 * filter preferences (Level) and sorting preferences (Alphabetical vs. Date).
 * </p>
 */
public class StudentMenuController implements IMenuController {
    private final Student currentStudent;
    private final IApplicationService applicationService;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;
    private final IAuthenticationService authService;
    private final Scanner scanner;

    private String filterLevel = "all";
    private String sortOrder = "alphabetical";
    /**
     * Constructs the StudentMenuController with all necessary dependencies.
     *
     * @param student            The currently logged-in student.
     * @param applicationService Service to handle application logic.
     * @param internshipRepo     Repository to read internship data.
     * @param outputService      Service to handle console output.
     * @param authService        Service to handle password changes.
     * @param scanner            Scanner for reading user input.
     */
    public StudentMenuController(Student student,
                                 IApplicationService applicationService,
                                 IInternshipRepository internshipRepo,
                                 IOutputService outputService,
                                 IAuthenticationService authService,
                                 Scanner scanner) {
        this.currentStudent = student;
        this.applicationService = applicationService;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
        this.authService = authService;
        this.scanner = scanner;
    }
    /**
     * Displays the main dashboard options for the student.
     */
    @Override
    public void displayMenu() {
        outputService.displayMessage("\n===== Student Menu =====");
        outputService.displayMessage("1. View Available Internships");
        outputService.displayMessage("2. Filter Internships");
        outputService.displayMessage("3. Apply for Internship");
        outputService.displayMessage("4. View My Applications");
        outputService.displayMessage("5. Withdraw Application");
        outputService.displayMessage("6. Confirm Accepted Placement");
        outputService.displayMessage("7. Change Password");
        outputService.displayMessage("8. Logout");
    }
    /**
     * Processes the user's menu selection.
     *
     * @param input The raw string input from the user.
     */
    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                viewAvailableInternships();
                break;
            case "2":
                filterInternships();
                break;
            case "3":
                applyForInternship();
                break;
            case "4":
                viewMyApplications();
                break;
            case "5":
                withdrawApplication();
                break;
            case "6":
                confirmPlacement();
                break;
            case "7":
                changePassword();
                break;
            case "8":
                break;
            default:
                outputService.displayError("Invalid choice!");
        }
    }
    /**
     * Handles the confirmation of an offer.
     * Allows the student to pick one "Approved" application to finalize.
     * Automatically withdraws all other pending/approved applications.
     */
    private void confirmPlacement() {
        String acceptedID = currentStudent.getAcceptedPlacementID();
        if (acceptedID != null && !acceptedID.isEmpty()) {
            outputService.displayMessage("You have already confirmed your placement: " + acceptedID);
            Internship confirmedInternship = internshipRepo.getById(acceptedID);
            if (confirmedInternship != null) {
                outputService.displayMessage(String.format("Company: %s\nTitle: %s",
                        confirmedInternship.getCompanyName(), confirmedInternship.getTitle()));
            }
            return;
        }

        List<String> appIDs = currentStudent.getApplicationIDs();
        List<String> approvedInternships = new ArrayList<>();

        for (String id : appIDs) {
            Internship i = internshipRepo.getById(id);
            if (i != null && "Approved".equals(i.getStudentStatus(currentStudent.getUserID()))) {
                approvedInternships.add(id);
            }
        }

        if (approvedInternships.isEmpty()) {
            outputService.displayMessage("You have no approved internships to confirm yet.");
            outputService.displayMessage("Please wait for companies to review your applications.");
            return;
        }

        outputService.displayMessage("\n===== Your Approved Internships =====");
        outputService.displayMessage("Choose one to confirm as your final placement:");
        outputService.displayMessage("");

        for (String id : approvedInternships) {
            Internship i = internshipRepo.getById(id);
            outputService.displayMessage(String.format("%s - %s\n  Company: %s | Level: %s\n  Slots: %d/%d\n",
                    id, i.getTitle(), i.getCompanyName(), i.getLevel(),
                    i.getConfirmedSlots(), i.getTotalSlots()));
        }

        outputService.displayMessage("Enter Internship ID to confirm as your placement (or 'back'):");
        String internshipID = scanner.nextLine().trim();

        if ("back".equals(internshipID)) {
            return;
        }

        if (!approvedInternships.contains(internshipID)) {
            outputService.displayError("Invalid choice. Please select from your approved internships.");
            return;
        }

        Internship selectedInternship = internshipRepo.getById(internshipID);

        if (selectedInternship != null && !selectedInternship.hasAvailableSlots()) {
            outputService.displayError("Sorry, this internship is now full. Please select another internship.");
            return;
        }

        currentStudent.setAcceptedPlacement(internshipID);

        if (selectedInternship != null) {
            selectedInternship.incrementConfirmedSlots();
            selectedInternship.setStudentStatus(currentStudent.getUserID(), "Confirmed");
        }

        for (String otherID : appIDs) {
            if (!otherID.equals(internshipID)) {
                applicationService.withdrawApplication(currentStudent.getUserID(), otherID);
            }
        }

        outputService.displayMessage("\n==============================================");
        outputService.displayMessage("✓ Placement Confirmed Successfully!");
        outputService.displayMessage("==============================================");

        if (selectedInternship != null) {
            outputService.displayMessage(String.format("\nYour Confirmed Internship:\nTitle: %s\nCompany: %s\nLevel: %s\nSlots Updated: %d/%d",
                    selectedInternship.getTitle(), selectedInternship.getCompanyName(), selectedInternship.getLevel(),
                    selectedInternship.getConfirmedSlots(), selectedInternship.getTotalSlots()));
        }

        outputService.displayMessage("\nAll other applications have been automatically withdrawn.");
    }
    /**
     * Handles the withdrawal process.
     * Supports withdrawing from Pending, Approved, and Confirmed states.
     */
    private void withdrawApplication() {
        outputService.displayMessage("Enter Internship ID to withdraw (or 'back'):");
        String internshipID = scanner.nextLine().trim();

        if ("back".equals(internshipID)) {
            return;
        }

        Internship internship = internshipRepo.getById(internshipID);
        if (internship == null) {
            outputService.displayError("Internship not found.");
            return;
        }

        String status = internship.getStudentStatus(currentStudent.getUserID());

        if ("Pending".equals(status)) {
            boolean success = applicationService.withdrawApplication(currentStudent.getUserID(), internshipID);
        } else if ("Approved".equals(status) || "Confirmed".equals(status)) {
            outputService.displayMessage("Enter reason for withdrawal (optional, press Enter to skip):");
            String reason = scanner.nextLine().trim();
            if (reason.isEmpty()) {
                reason = "No reason provided";
            }
            boolean success = applicationService.requestWithdrawal(currentStudent.getUserID(), internshipID, reason);
            if (!success) {
                outputService.displayError("Failed to submit withdrawal request.");
            }
        } else {
            outputService.displayError("Cannot withdraw from this application (Status: " + status + ").");
        }
    }

    private void viewAvailableInternships() {
        List<Internship> allInternships = internshipRepo.getAll();
        List<Internship> filtered = new ArrayList<>();

        for (Internship i : allInternships) {
            if (!isInternshipAvailableForStudent(i)) {
                continue;
            }

            boolean matches = true;

            if (!filterLevel.equalsIgnoreCase("all") && !i.getLevel().equalsIgnoreCase(filterLevel)) {
                matches = false;
            }

            if (matches) {
                filtered.add(i);
            }
        }

        if (sortOrder.equals("alphabetical")) {
            filtered.sort(new AlphabeticalSorter());
        } else {
            filtered.sort(new ClosingDateSorter());
        }

        if (filtered.isEmpty()) {
            outputService.displayMessage("No internships available.");
            return;
        }

        outputService.displayMessage("\n===== Available Internships =====");
        outputService.displayMessage("Active Filters: Level=" + filterLevel + " | Sort=" + sortOrder);
        outputService.displayMessage("");
        displayInternshipList(filtered);
    }
    /**
     * Sub-menu for adjusting filter and sort settings.
     */
    private void filterInternships() {
        while (true) {
            outputService.displayMessage("\n===== Filter Settings =====");
            outputService.displayMessage("Current Filters:");
            outputService.displayMessage("  Level: " + filterLevel);
            outputService.displayMessage("  Sort: " + sortOrder);
            outputService.displayMessage("");
            outputService.displayMessage("1. Change Level Filter");
            outputService.displayMessage("2. Change Sort Order");
            outputService.displayMessage("3. Reset All Filters");
            outputService.displayMessage("4. Back");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    outputService.displayMessage("Enter level (Basic/Intermediate/Advanced/all):");
                    filterLevel = scanner.nextLine().trim();
                    if (filterLevel.isEmpty()) filterLevel = "all";
                    break;
                case "2":
                    outputService.displayMessage("Sort by:");
                    outputService.displayMessage("1. Alphabetical (Title)");
                    outputService.displayMessage("2. Closing Date");
                    String sortChoice = scanner.nextLine();
                    sortOrder = "1".equals(sortChoice) ? "alphabetical" : "closing_date";
                    break;
                case "3":
                    filterLevel = "all";
                    sortOrder = "alphabetical";
                    outputService.displayMessage("All filters reset to default.");
                    break;
                case "4":
                    return;
                default:
                    outputService.displayError("Invalid choice!");
            }
        }
    }
    /**
     * Checks strict business rules for visibility.
     */
    private boolean isInternshipAvailableForStudent(Internship internship) {
        if (!"Approved".equals(internship.getStatus())) {
            return false;
        }

        if (!internship.isVisible()) {
            return false;
        }

        if (!internship.hasAvailableSlots()) {
            return false;
        }

        if (!internship.getPreferredMajor().equalsIgnoreCase(currentStudent.getMajor()) &&
                !internship.getPreferredMajor().equalsIgnoreCase("all")) {
            return false;
        }

        int year = currentStudent.getYearOfStudy();
        String level = internship.getLevel();

        if (year <= 2 && !level.equalsIgnoreCase("Basic")) {
            return false;
        }

        return true;
    }

    private void displayInternshipList(List<Internship> internships) {
        for (Internship i : internships) {
            outputService.displayMessage(String.format(
                    "%s - %s\n  Company: %s\n  Level: %s | Major: %s\n  Slots: %d/%d | Closing: %s\n",
                    i.getInternshipID(),
                    i.getTitle(),
                    i.getCompanyName(),
                    i.getLevel(),
                    i.getPreferredMajor(),
                    i.getConfirmedSlots(),
                    i.getTotalSlots(),
                    i.getClosingDate()
            ));
        }
    }
    /**
     * Handles the flow for applying to a new internship.
     */
    private void applyForInternship() {
        List<Internship> allInternships = internshipRepo.getAll();
        List<Internship> filtered = new ArrayList<>();

        for (Internship i : allInternships) {
            if (!isInternshipAvailableForStudent(i)) {
                continue;
            }

            boolean matches = true;

            if (!filterLevel.equalsIgnoreCase("all") && !i.getLevel().equalsIgnoreCase(filterLevel)) {
                matches = false;
            }

            if (matches) {
                filtered.add(i);
            }
        }

        if (sortOrder.equals("alphabetical")) {
            filtered.sort(new AlphabeticalSorter());
        } else {
            filtered.sort(new ClosingDateSorter());
        }

        if (filtered.isEmpty()) {
            outputService.displayMessage("No internships available to apply.");
            return;
        }

        outputService.displayMessage("\n===== Available Internships =====");
        outputService.displayMessage("Active Filters: Level=" + filterLevel + " | Sort=" + sortOrder);
        outputService.displayMessage("");
        displayInternshipList(filtered);

        outputService.displayMessage("Enter Internship ID to apply (or 'back'):");
        String internshipID = scanner.nextLine();

        if ("back".equals(internshipID)) {
            return;
        }

        applicationService.applyForInternship(currentStudent.getUserID(), internshipID);
    }
    /**
     * Displays the status of all applications made by the student.
     */
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

    private void changePassword() {
        outputService.displayMessage("Enter current password:");
        String oldPassword = scanner.nextLine();
        outputService.displayMessage("Enter new password:");
        String newPassword = scanner.nextLine();
        outputService.displayMessage("Confirm new password:");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            outputService.displayError("Passwords do not match!");
            return;
        }

        ((AuthenticationService) authService).changePassword(
                currentStudent.getUserID(), oldPassword, newPassword);
    }
}
