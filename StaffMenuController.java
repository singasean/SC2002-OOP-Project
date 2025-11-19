import java.util.*;

public class StaffMenuController implements IMenuController {
    private final CareerCenterStaff currentStaff;
    private final IApprovalService approvalService;
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

    public StaffMenuController(CareerCenterStaff staff,
                               IApprovalService approvalService,
                               IUserRepository<CompanyRepresentative> companyRepo,
                               IUserRepository<Student> studentRepo,
                               IInternshipRepository internshipRepo,
                               IOutputService outputService,
                               IAuthenticationService authService,
                               Scanner scanner) {
        this.currentStaff = staff;
        this.approvalService = approvalService;
        this.companyRepo = companyRepo;
        this.studentRepo = studentRepo;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
        this.authService = authService;
        this.scanner = scanner;
    }

    @Override
    public void displayMenu() {
        outputService.displayMessage("\n===== Career Center Staff Menu =====");
        outputService.displayMessage("1. Review Company Representatives");
        outputService.displayMessage("2. View All Internships");
        outputService.displayMessage("3. Filter Internships");
        outputService.displayMessage("4. Review Internships");
        outputService.displayMessage("5. View All Students");
        outputService.displayMessage("6. View Placement Report");
        outputService.displayMessage("7. Change Password");
        outputService.displayMessage("8. Logout");
    }

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
                viewStudents();
                break;
            case "6":
                viewPlacementReport();
                break;
            case "7":
                changePassword();
                break;
            case "8":
                outputService.displayMessage("Logging out...");
                break;
            default:
                outputService.displayError("Invalid choice!");
        }
    }

    private void viewPlacementReport() {
        List<Internship> allInternships = internshipRepo.getAll();
        List<Student> allStudents = studentRepo.getAll();

        outputService.displayMessage("\n===== Placement Report =====");
        outputService.displayMessage("");

        int totalPlacements = 0;

        for (Internship i : allInternships) {
            if (!"Approved".equals(i.getStatus())) {
                continue;
            }

            String confirmedStudentID = null;
            for (Student s : allStudents) {
                if (i.getInternshipID().equals(s.getAcceptedPlacementID())) {
                    confirmedStudentID = s.getUserID();
                    totalPlacements++;
                    break;
                }
            }

            outputService.displayMessage(String.format(
                    "%s - %s\n  Company: %s | Level: %s | Major: %s\n  Confirmed Student: %s\n",
                    i.getInternshipID(), i.getTitle(), i.getCompanyName(),
                    i.getLevel(), i.getPreferredMajor(),
                    confirmedStudentID != null ? confirmedStudentID : "None"));
        }

        outputService.displayMessage("===== Summary =====");
        outputService.displayMessage("Total Confirmed Placements: " + totalPlacements);
    }


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

        for (CompanyRepresentative rep : pending) {
            outputService.displayMessage(String.format("%s - %s (%s)",
                    rep.getUserID(), rep.getName(), rep.getCompanyName()));
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
