import java.util.*;

/**
 * Controller class that manages the User Interface for Career Center Staff.
 * <p>
 * <b>Architectural Role:</b>
 * This class acts as the <b>Administrative Boundary</b>. It aggregates data from multiple
 * repositories to generate reports and delegates high-privilege actions (like Approvals)
 * to the {@link ApprovalService}.
 * </p>
 * <p>
 * <b>Key Responsibilities:</b>
 * <ul>
 * <li><b>Gatekeeping:</b> Moderating new Company Representative accounts.</li>
 * <li><b>Content Moderation:</b> Approving or rejecting new Internship postings.</li>
 * <li><b>Dispute Resolution:</b> Adjudicating student withdrawal requests.</li>
 * <li><b>Reporting:</b> Aggregating data to view placement statistics.</li>
 * </ul>
 * </p>
 */
public class StaffMenuController implements IMenuController {
    private final CareerCenterStaff currentStaff;
    private final IApprovalService approvalService;
    private final IApplicationService applicationService;
    private final IUserRepository<CompanyRepresentative> companyRepo;
    private final IUserRepository<Student> studentRepo;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;
    private final IAuthenticationService authService;
    private final Scanner scanner;

    private String filterStatus = "all";
    private String filterMajor = "all";
    private String filterLevel = "all";
    private String filterCompany = "all";
    private String sortOrder = "alphabetical";
    /**
     * Constructs the StaffMenuController.
     * <p>
     * <b>Dependency Injection Note:</b> This controller requires the widest range of dependencies
     * because Staff are the "Superusers" of the system.
     * </p>
     *
     * @param staff              The logged-in staff member.
     * @param approvalService    Handles Rep/Internship approvals.
     * @param applicationService Handles Withdrawal approvals.
     * @param companyRepo        Access to Company Rep data.
     * @param studentRepo        Access to Student data.
     * @param internshipRepo     Access to Internship data.
     * @param outputService      Console output.
     * @param authService        Password management.
     * @param scanner            User input.
     */
    public StaffMenuController(CareerCenterStaff staff,
                               IApprovalService approvalService,
                               IApplicationService applicationService,
                               IUserRepository<CompanyRepresentative> companyRepo,
                               IUserRepository<Student> studentRepo,
                               IInternshipRepository internshipRepo,
                               IOutputService outputService,
                               IAuthenticationService authService,
                               Scanner scanner) {
        this.currentStaff = staff;
        this.approvalService = approvalService;
        this.applicationService = applicationService;
        this.companyRepo = companyRepo;
        this.studentRepo = studentRepo;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
        this.authService = authService;
        this.scanner = scanner;
    }
    /**
     * Displays the dashboard options for the staff member.
     */
    @Override
    public void displayMenu() {
        outputService.displayMessage("\n===== Career Center Staff Menu =====");
        outputService.displayMessage("1. Review Company Representatives");
        outputService.displayMessage("2. View All Internships");
        outputService.displayMessage("3. Filter Internships");
        outputService.displayMessage("4. Review Internships");
        outputService.displayMessage("5. Review Withdrawals");
        outputService.displayMessage("6. View All Students");
        outputService.displayMessage("7. View Placement Report");
        outputService.displayMessage("8. Change Password");
        outputService.displayMessage("9. Logout");
    }
    /**
     * Processes the user's menu selection.
     *
     * @param input The raw input string.
     */
    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                reviewCompanyReps();
                break;
            case "2":
                viewAllInternships();
                break;
            case "3":
                filterInternships();
                break;
            case "4":
                reviewInternships();
                break;
            case "5":
                reviewWithdrawals();
                break;
            case "6":
                viewStudents();
                break;
            case "7":
                viewPlacementReport();
                break;
            case "8":
                changePassword();
                break;
            case "9":
                outputService.displayMessage("Logging out...");
                break;
            default:
                outputService.displayError("Invalid choice!");
        }
    }
    /**
     * Dispute Resolution workflow for Internship Withdrawals.
     * <p>
     * <b>Deep Dive into Logic (The "Join" Problem):</b>
     * A withdrawal request is stored inside an {@link Internship} object (in the {@code statusByStudent} map).
     * However, the Internship object does not know the Student's Name (it only knows the ID).
     * <br>
     * To display a helpful request to the Staff (e.g., "John Doe wants to withdraw because..."),
     * this method performs a <b>Manual Join</b>:
     * <ol>
     * <li><b>Outer Loop:</b> Iterate through ALL Internships to find flags.</li>
     * <li><b>Inner Check:</b> Call {@code getPendingWithdrawalStudents()} on each internship.</li>
     * <li><b>The Lookup:</b> If a request is found (e.g., for "STU001"), use {@code studentRepo.getById("STU001")}
     * to fetch the Student Entity and retrieve their real Name.</li>
     * </ol>
     * This demonstrates why the Controller needs access to multiple Repositories.
     * </p>
     */
    private void reviewWithdrawals() {
        List<Internship> allInternships = internshipRepo.getAll();
        List<Internship> withPending = new ArrayList<>();

        for (Internship i : allInternships) {
            if (!i.getPendingWithdrawalStudents().isEmpty()) {
                withPending.add(i);
            }
        }

        if (withPending.isEmpty()) {
            outputService.displayMessage("No pending withdrawal requests.");
            return;
        }

        outputService.displayMessage("\n===== Pending Withdrawal Requests =====");
        for (Internship i : withPending) {
            List<String> pendingStudents = i.getPendingWithdrawalStudents();
            for (String studentID : pendingStudents) {
                Student s = studentRepo.getById(studentID);
                String name = s != null ? s.getName() : "Unknown";
                String reason = i.getWithdrawalReason(studentID);
                outputService.displayMessage(String.format(
                        "Internship: %s - %s | Company: %s\n  Student: %s (%s)\n  Reason: %s\n",
                        i.getInternshipID(), i.getTitle(), i.getCompanyName(), name, studentID, reason));
            }
        }

        outputService.displayMessage("\nEnter 'InternshipID StudentID' to review (e.g., INT123 STU456) or 'back':");
        String input = scanner.nextLine().trim();

        if ("back".equals(input)) {
            return;
        }

        String[] parts = input.split(" ");
        if (parts.length != 2) {
            outputService.displayError("Invalid format. Use: InternshipID StudentID");
            return;
        }

        String internshipID = parts[0].trim();
        String studentID = parts[1].trim();

        Internship selected = internshipRepo.getById(internshipID);
        if (selected == null || !"Pending Withdrawal".equals(selected.getStudentStatus(studentID))) {
            outputService.displayError("No pending withdrawal found.");
            return;
        }

        outputService.displayMessage("Approve (a) or Reject (r) withdrawal?");
        String decision = scanner.nextLine().trim();

        if ("a".equals(decision)) {
            applicationService.approveWithdrawal(internshipID, studentID);
        } else if ("r".equals(decision)) {
            applicationService.rejectWithdrawal(internshipID, studentID);
        } else {
            outputService.displayError("Invalid decision.");
        }
    }
    /**
     * Generates a summary report of all successful placements.
     * <p>
     * <b>Deep Dive into Logic:</b>
     * This is a <b>Read-Only Aggregation</b> operation.
     * <br>
     * Instead of querying students ("Where are you working?"), it queries Internships
     * ("How many confirmed slots do you have?").
     * <br>
     * <b>Why?</b> This is more efficient (O(N) on internships vs O(M) on students)
     * and relies on the Internship being the "Source of Truth" for slot counts.
     * </p>
     */
    private void viewPlacementReport() {
        List<Internship> allInternships = internshipRepo.getAll();
        List<Student> allStudents = studentRepo.getAll();

        outputService.displayMessage("\n===== Placement Report =====");
        outputService.displayMessage("");

        int totalPlacements = 0;

        for (Internship i : allInternships) {
            if (!"Approved".equals(i.getStatus()) && !"Filled".equals(i.getStatus())) {
                continue;
            }

            List<String> confirmedStudents = new ArrayList<>();
            for (Student s : allStudents) {
                String studentPlacementID = s.getAcceptedPlacementID();
                if (studentPlacementID != null && studentPlacementID.equals(i.getInternshipID())) {
                    confirmedStudents.add(s.getUserID() + " (" + s.getName() + ")");
                    totalPlacements++;
                }
            }

            outputService.displayMessage(String.format(
                    "%s - %s\n  Company: %s | Level: %s | Major: %s\n  Confirmed Students: %s\n",
                    i.getInternshipID(), i.getTitle(), i.getCompanyName(),
                    i.getLevel(), i.getPreferredMajor(),
                    confirmedStudents.isEmpty() ? "None" : String.join(", ", confirmedStudents)));
        }

        outputService.displayMessage("===== Summary =====");
        outputService.displayMessage("Total Confirmed Placements: " + totalPlacements);
    }
    /**
     * Handles the approval workflow for new Company Representatives.
     * <p>
     * <b>Deep Dive into Logic:</b>
     * This method acts as a manual filter for the "Registration" process.
     * <br>
     * <b>1. Aggregation:</b> It fetches the entire list of representatives from the {@code companyRepo}.
     * <br>
     * <b>2. Filtering:</b> It manually iterates to find only those with "Pending" status.
     * <i>(Note: In a SQL-based system, this would be a "SELECT * FROM Users WHERE Status='Pending'" query.
     * Since we are in-memory, we filter in the controller.)</i>
     * <br>
     * <b>3. Vetting Display:</b> Unlike other lists that just show IDs, this view dumps
     * <i>all</i> available info (Department, Email, Position). This is intentional:
     * the Staff member needs this context to decide if the person is legitimate.
     * <br>
     * <b>4. State Transition:</b> It does not modify the status directly. It delegates to
     * {@link IApprovalService}, which handles the "Unlock Account" logic.
     * </p>
     */
    private void reviewCompanyReps() {
        List<CompanyRepresentative> reps = companyRepo.getAll();
        List<CompanyRepresentative> pending = new ArrayList<>();

        for (CompanyRepresentative rep : reps) {
            if ("Pending".equals(rep.getStatus())) {
                pending.add(rep);
            }
        }

        if (pending.isEmpty()) {
            outputService.displayMessage("No pending company representatives.");
            return;
        }

        outputService.displayMessage("\n===== Pending Applications =====");
        for (CompanyRepresentative rep : pending) {
            outputService.displayMessage("------------------------------------------------");
            outputService.displayMessage("ID:           " + rep.getUserID());
            outputService.displayMessage("Name:         " + rep.getName());
            outputService.displayMessage("Company:      " + rep.getCompanyName());
            outputService.displayMessage("Department:   " + rep.getDepartment());
            outputService.displayMessage("Position:     " + rep.getPosition());
            outputService.displayMessage("Email:        " + rep.getEmail());
            outputService.displayMessage("Status:       " + rep.getStatus());
            outputService.displayMessage("------------------------------------------------");
        }

        outputService.displayMessage("\nEnter Rep ID to review (or 'back'):");
        String repID = scanner.nextLine();
        if ("back".equals(repID)) return;

        outputService.displayMessage("Approve or Reject? (a/r):");
        String decision = scanner.nextLine();

        if ("a".equals(decision)) {
            approvalService.approveCompanyRep(repID);
        } else if ("r".equals(decision)) {
            approvalService.rejectCompanyRep(repID);
        }
    }
    /**
     * View all internships with support for administrative filters.
     * <p>
     * <b>Difference from Student View:</b>
     * The {@code StudentMenuController} contains strict logic to HIDE internships that are:
     * <ul>
     * <li>Not Approved</li>
     * <li>Hidden by Company</li>
     * <li>Already Filled</li>
     * </ul>
     * <br>
     * <b>This Admin View</b> intentionally bypasses those checks. It shows EVERYTHING.
     * This allows Staff to debug issues (e.g., "Why can't a student see this internship?
     * Oh, because the Company set it to 'Hidden'.").
     * </p>
     */
    private void viewAllInternships() {
        List<Internship> allInternships = internshipRepo.getAll();
        List<Internship> filtered = new ArrayList<>();

        for (Internship i : allInternships) {
            boolean matches = true;

            if (!filterStatus.equalsIgnoreCase("all") && !i.getStatus().equalsIgnoreCase(filterStatus)) {
                matches = false;
            }

            if (!filterMajor.equalsIgnoreCase("all") && !i.getPreferredMajor().equalsIgnoreCase(filterMajor)) {
                matches = false;
            }

            if (!filterLevel.equalsIgnoreCase("all") && !i.getLevel().equalsIgnoreCase(filterLevel)) {
                matches = false;
            }

            if (!filterCompany.equalsIgnoreCase("all") && !i.getCompanyName().equalsIgnoreCase(filterCompany)) {
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

        outputService.displayMessage("\n===== All Internships =====");
        outputService.displayMessage("Active Filters: Status=" + filterStatus + " | Major=" + filterMajor +
                " | Level=" + filterLevel + " | Company=" + filterCompany + " | Sort=" + sortOrder);
        outputService.displayMessage("Total Found: " + filtered.size());
        outputService.displayMessage("");

        if (filtered.isEmpty()) {
            outputService.displayMessage("No internships found.");
            return;
        }

        for (Internship i : filtered) {
            outputService.displayMessage(String.format(
                    "%s - %s\n  Company: %s | Level: %s | Major: %s\n  Status: %s | Slots: %d/%d | Visible: %s\n  Opening: %s | Closing: %s\n",
                    i.getInternshipID(), i.getTitle(), i.getCompanyName(),
                    i.getLevel(), i.getPreferredMajor(), i.getStatus(),
                    i.getConfirmedSlots(), i.getTotalSlots(), i.isVisible() ? "Yes" : "No",
                    i.getOpeningDate(), i.getClosingDate()));
        }

        int totalSlots = 0;
        int filledSlots = 0;
        for (Internship i : filtered) {
            totalSlots += i.getTotalSlots();
            filledSlots += i.getConfirmedSlots();
        }

        outputService.displayMessage("===== Summary =====");
        outputService.displayMessage("Total Slots Available: " + totalSlots);
        outputService.displayMessage("Total Slots Filled: " + filledSlots);
        outputService.displayMessage("Fill Rate: " + (totalSlots > 0 ? (filledSlots * 100 / totalSlots) : 0) + "%");
    }

    private void filterInternships() {
        while (true) {
            outputService.displayMessage("\n===== Filter Settings =====");
            outputService.displayMessage("Current Filters:");
            outputService.displayMessage("  Status: " + filterStatus);
            outputService.displayMessage("  Major: " + filterMajor);
            outputService.displayMessage("  Level: " + filterLevel);
            outputService.displayMessage("  Company: " + filterCompany);
            outputService.displayMessage("  Sort: " + sortOrder);
            outputService.displayMessage("");
            outputService.displayMessage("1. Change Status Filter");
            outputService.displayMessage("2. Change Major Filter");
            outputService.displayMessage("3. Change Level Filter");
            outputService.displayMessage("4. Change Company Filter");
            outputService.displayMessage("5. Change Sort Order");
            outputService.displayMessage("6. Reset All Filters");
            outputService.displayMessage("7. Back");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    outputService.displayMessage("Enter status (Approved/Pending/Rejected/all):");
                    filterStatus = scanner.nextLine().trim();
                    if (filterStatus.isEmpty()) filterStatus = "all";
                    break;
                case "2":
                    outputService.displayMessage("Enter major (or 'all'):");
                    filterMajor = scanner.nextLine().trim();
                    if (filterMajor.isEmpty()) filterMajor = "all";
                    break;
                case "3":
                    outputService.displayMessage("Enter level (Basic/Intermediate/Advanced/all):");
                    filterLevel = scanner.nextLine().trim();
                    if (filterLevel.isEmpty()) filterLevel = "all";
                    break;
                case "4":
                    outputService.displayMessage("Enter company name (or 'all'):");
                    filterCompany = scanner.nextLine().trim();
                    if (filterCompany.isEmpty()) filterCompany = "all";
                    break;
                case "5":
                    outputService.displayMessage("Sort by:");
                    outputService.displayMessage("1. Alphabetical (Title)");
                    outputService.displayMessage("2. Closing Date");
                    String sortChoice = scanner.nextLine();
                    sortOrder = "1".equals(sortChoice) ? "alphabetical" : "closing_date";
                    break;
                case "6":
                    filterStatus = "all";
                    filterMajor = "all";
                    filterLevel = "all";
                    filterCompany = "all";
                    sortOrder = "alphabetical";
                    outputService.displayMessage("All filters reset to default.");
                    break;
                case "7":
                    return;
                default:
                    outputService.displayError("Invalid choice!");
            }
        }
    }
    /**
     * Content Moderation workflow for new Job Postings.
     * <p>
     * <b>Architectural Role:</b>
     * This ensures that the {@link StudentMenuController} only ever displays "Safe" (Approved) content.
     * By keeping the "Pending" internships invisible in the Student view but visible here,
     * we create a staging environment.
     * </p>
     * <p>
     * <b>Logic Flow:</b>
     * 1. Retrieve all internships.
     * 2. Filter for status equals "Pending".
     * 3. Allow Staff to inspect details (Company, Description).
     * 4. Delegate to {@link IApprovalService} to flip the boolean visibility switch.
     * </p>
     */
    private void reviewInternships() {
        List<Internship> internships = internshipRepo.getAll();
        List<Internship> pending = new ArrayList<>();

        for (Internship i : internships) {
            if ("Pending".equals(i.getStatus())) {
                pending.add(i);
            }
        }

        if (pending.isEmpty()) {
            outputService.displayMessage("No pending internships.");
            return;
        }

        for (Internship i : pending) {
            outputService.displayMessage(String.format("%s - %s (%s)",
                    i.getInternshipID(), i.getTitle(), i.getCompanyName()));
        }

        outputService.displayMessage("\nEnter Internship ID to review (or 'back'):");
        String internshipID = scanner.nextLine();
        if ("back".equals(internshipID)) return;

        Internship internship = internshipRepo.getById(internshipID);
        if (internship == null) {
            outputService.displayError("Internship ID not found!");
            return;
        }

        if (!"Pending".equals(internship.getStatus())) {
            outputService.displayError("This internship is not pending review!");
            return;
        }

        outputService.displayMessage("Approve or Reject? (a/r):");
        String decision = scanner.nextLine();

        if ("a".equals(decision)) {
            approvalService.approveInternship(internshipID);
        } else if ("r".equals(decision)) {
            approvalService.rejectInternship(internshipID);
        }
    }

    private void viewStudents() {
        List<Student> students = studentRepo.getAll();
        for (Student s : students) {
            outputService.displayMessage(String.format("%s - %s (Year %d, %s)",
                    s.getUserID(), s.getName(), s.getYearOfStudy(), s.getMajor()));
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
                currentStaff.getUserID(), oldPassword, newPassword);
    }
}
