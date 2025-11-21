import java.util.*;

public class CompanyRepMenuController implements IMenuController {
    private final CompanyRepresentative currentRep;
    private final IInternshipRepository internshipRepo;
    private final IApplicationService applicationService;
    private final IOutputService outputService;
    private final IAuthenticationService authService;
    private final Scanner scanner;

    private String filterStatus = "all";
    private String filterMajor = "all";
    private String filterLevel = "all";
    private String sortOrder = "alphabetical";

    public CompanyRepMenuController(CompanyRepresentative rep,
                                    IInternshipRepository internshipRepo,
                                    IApplicationService applicationService,
                                    IOutputService outputService,
                                    IAuthenticationService authService,
                                    Scanner scanner) {
        this.currentRep = rep;
        this.internshipRepo = internshipRepo;
        this.applicationService = applicationService;
        this.outputService = outputService;
        this.authService = authService;
        this.scanner = scanner;
    }

    @Override
    public void displayMenu() {
        outputService.displayMessage("\n===== Company Representative Menu =====");
        outputService.displayMessage("1. Post New Internship");
        outputService.displayMessage("2. View My Internships");
        outputService.displayMessage("3. Filter My Internships");
        outputService.displayMessage("4. Review Applications");
        outputService.displayMessage("5. Toggle Internship Visibility");
        outputService.displayMessage("6. Change Password");
        outputService.displayMessage("7. Logout");
    }

    @Override
    public void handleInput(String input) {
        if (!currentRep.isApproved()) {
            outputService.displayError("Your account is pending approval.");
            return;
        }

        switch (input) {
            case "1":
                postInternship();
                break;
            case "2":
                viewMyInternships();
                break;
            case "3":
                filterMyInternships();
                break;
            case "4":
                reviewApplications();
                break;
            case "5":
                toggleInternshipVisibility();
                break;
            case "6":
                changePassword();
                break;
            case "7":
                outputService.displayMessage("Logging out...");
                break;
            default:
                outputService.displayError("Invalid choice!");
        }
    }

    private void viewMyInternships() {
        List<Internship> myInternships = internshipRepo.getByRepresentativeID(currentRep.getUserID());

        if (myInternships.isEmpty()) {
            outputService.displayMessage("No internships posted.");
            return;
        }

        List<Internship> filtered = new ArrayList<>();

        for (Internship i : myInternships) {
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

            if (matches) {
                filtered.add(i);
            }
        }

        if (sortOrder.equals("alphabetical")) {
            filtered.sort(new AlphabeticalSorter());
        } else {
            filtered.sort(new ClosingDateSorter());
        }

        outputService.displayMessage("\n===== My Internships =====");
        outputService.displayMessage("Active Filters: Status=" + filterStatus + " | Major=" + filterMajor +
                " | Level=" + filterLevel + " | Sort=" + sortOrder);
        outputService.displayMessage("");

        for (Internship i : filtered) {
            outputService.displayMessage(String.format("%s - %s [%s] (Slots: %d/%d) [Visible: %s]",
                    i.getInternshipID(), i.getTitle(), i.getStatus(),
                    i.getConfirmedSlots(), i.getTotalSlots(), i.isVisible() ? "Yes" : "No"));
        }
    }

    private void filterMyInternships() {
        while (true) {
            outputService.displayMessage("\n===== Filter Settings =====");
            outputService.displayMessage("Current Filters:");
            outputService.displayMessage("  Status: " + filterStatus);
            outputService.displayMessage("  Major: " + filterMajor);
            outputService.displayMessage("  Level: " + filterLevel);
            outputService.displayMessage("  Sort: " + sortOrder);
            outputService.displayMessage("");
            outputService.displayMessage("1. Change Status Filter");
            outputService.displayMessage("2. Change Major Filter");
            outputService.displayMessage("3. Change Level Filter");
            outputService.displayMessage("4. Change Sort Order");
            outputService.displayMessage("5. Reset All Filters");
            outputService.displayMessage("6. Back");

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
                    outputService.displayMessage("Sort by:");
                    outputService.displayMessage("1. Alphabetical (Title)");
                    outputService.displayMessage("2. Closing Date");
                    String sortChoice = scanner.nextLine();
                    sortOrder = "1".equals(sortChoice) ? "alphabetical" : "closing_date";
                    break;
                case "5":
                    filterStatus = "all";
                    filterMajor = "all";
                    filterLevel = "all";
                    sortOrder = "alphabetical";
                    outputService.displayMessage("All filters reset to default.");
                    break;
                case "6":
                    return;
                default:
                    outputService.displayError("Invalid choice!");
            }
        }
    }

    private void postInternship() {
        if (!currentRep.canPostInternship()) {
            outputService.displayError("Cannot post more internships (limit: 5)");
            return;
        }

        outputService.displayMessage("Enter internship title:");
        String title = scanner.nextLine();
        outputService.displayMessage("Enter description:");
        String desc = scanner.nextLine();

        String level;
        while (true) {
            outputService.displayMessage("Enter level (Basic/Intermediate/Advanced):");
            level = scanner.nextLine().trim();
            if (level.equalsIgnoreCase("Basic") ||
                    level.equalsIgnoreCase("Intermediate") ||
                    level.equalsIgnoreCase("Advanced")) {
                break;
            } else {
                outputService.displayError("Invalid level! Please enter 'Basic', 'Intermediate', or 'Advanced'");
            }
        }

        outputService.displayMessage("Enter preferred major:");
        String major = scanner.nextLine();

        String opening;
        while (true) {
            outputService.displayMessage("Enter opening date (DD-MM-YYYY):");
            opening = scanner.nextLine().trim();
            String error = validateDate(opening);
            if (error == null) {
                break;
            } else {
                outputService.displayError(error);
            }
        }

        String closing;
        while (true) {
            outputService.displayMessage("Enter closing date (DD-MM-YYYY):");
            closing = scanner.nextLine().trim();
            String error = validateDate(closing);
            if (error != null) {
                outputService.displayError(error);
                continue;
            }

            if (!isClosingAfterOpening(opening, closing)) {
                outputService.displayError("Closing date must be after opening date!");
                continue;
            }

            break;
        }

        int slots;
        while (true) {
            outputService.displayMessage("Enter total slots (1-10):");
            String slotsInput = scanner.nextLine().trim();
            try {
                slots = Integer.parseInt(slotsInput);
                if (slots < 1 || slots > 10) {
                    outputService.displayError("Slots must be between 1 and 10!");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                outputService.displayError("Invalid input! Please enter a number between 1 and 10.");
            }
        }

        String internshipID = internshipRepo.generateNextID();
        Internship internship = new Internship(internshipID, title, desc, level, major,
                opening, closing, slots, currentRep.getCompanyName(), currentRep.getUserID());

        internshipRepo.add(internship);
        currentRep.addInternship(internshipID);
        outputService.displayMessage("Internship posted! (Pending approval)");
    }

    private void toggleInternshipVisibility() {
        List<Internship> myInternships = internshipRepo.getByRepresentativeID(currentRep.getUserID());

        if (myInternships.isEmpty()) {
            outputService.displayMessage("No internships posted.");
            return;
        }

        outputService.displayMessage("\n===== Your Internships =====");
        for (Internship i : myInternships) {
            outputService.displayMessage(String.format("%s - %s [%s] (Slots: %d/%d) [Visible: %s]",
                    i.getInternshipID(), i.getTitle(), i.getStatus(),
                    i.getConfirmedSlots(), i.getTotalSlots(), i.isVisible() ? "Yes" : "No"));
        }

        outputService.displayMessage("\nEnter Internship ID to toggle visibility (or 'back'):");
        String internshipID = scanner.nextLine();

        if ("back".equals(internshipID)) {
            return;
        }

        Internship internship = internshipRepo.getById(internshipID);

        if (internship == null || !internship.getRepresentativeID().equals(currentRep.getUserID())) {
            outputService.displayError("Internship not found or not yours.");
            return;
        }

        internship.setVisible(!internship.isVisible());
        outputService.displayMessage("Internship visibility: " + (internship.isVisible() ? "ON" : "OFF"));
    }

    private void reviewApplications() {
        List<Internship> myInternships = internshipRepo.getByRepresentativeID(currentRep.getUserID());

        if (myInternships.isEmpty()) {
            outputService.displayMessage("No internships posted.");
            return;
        }

        outputService.displayMessage("\n===== Your Internships =====");
        for (Internship i : myInternships) {
            Map<String, String> statuses = i.getAllStudentStatuses();
            int pendingCount = 0;
            for (String status : statuses.values()) {
                if ("Pending".equals(status)) {
                    pendingCount++;
                }
            }

            outputService.displayMessage(String.format("%s - %s [%s] (Pending Applications: %d)",
                    i.getInternshipID(), i.getTitle(), i.getStatus(), pendingCount));
        }

        outputService.displayMessage("\nEnter Internship ID (or 'back'):");
        String internshipID = scanner.nextLine();

        if ("back".equals(internshipID)) {
            return;
        }

        Internship internship = internshipRepo.getById(internshipID);

        if (internship == null || !internship.getRepresentativeID().equals(currentRep.getUserID())) {
            outputService.displayError("Internship not found or not yours.");
            return;
        }

        Map<String, String> statuses = internship.getAllStudentStatuses();
        if (statuses.isEmpty()) {
            outputService.displayMessage("No applications yet.");
            return;
        }

        boolean hasPending = false;
        outputService.displayMessage("\n===== Applications for " + internship.getTitle() + " =====");
        for (Map.Entry<String, String> entry : statuses.entrySet()) {
            outputService.displayMessage(String.format("Student %s - Status: %s",
                    entry.getKey(), entry.getValue()));
            if ("Pending".equals(entry.getValue())) {
                hasPending = true;
            }
        }

        if (!hasPending) {
            outputService.displayMessage("\nNo pending applications to review.");
            return;
        }

        outputService.displayMessage("\nEnter Student ID to approve/reject (or 'back'):");
        String studentID = scanner.nextLine();
        if ("back".equals(studentID)) return;

        outputService.displayMessage("Approve or Reject? (a/r):");
        String decision = scanner.nextLine();

        if ("a".equals(decision)) {
            applicationService.approveApplication(internshipID, studentID);
        } else if ("r".equals(decision)) {
            applicationService.rejectApplication(internshipID, studentID);
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
                currentRep.getUserID(), oldPassword, newPassword);
    }

    private String validateDate(String date) {
        if (date == null || date.isEmpty()) {
            return "Date cannot be empty!";
        }

        if (!date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return "Invalid date format! Please use DD-MM-YYYY (e.g., 25-12-2025)";
        }

        String[] parts = date.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (year < 2000 || year > 2100) {
            return "Year must be between 2000 and 2100!";
        }

        if (month < 1 || month > 12) {
            return "Month must be between 01 and 12!";
        }

        if (day < 1) {
            return "Day must be at least 01!";
        }

        if (day > 31) {
            return "Day cannot exceed 31!";
        }

        if (month == 2) {
            boolean isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            int maxDay = isLeap ? 29 : 28;
            if (day > maxDay) {
                return "February " + year + " has only " + maxDay + " days!";
            }
        }

        if (month == 4 || month == 6 || month == 9 || month == 11) {
            if (day > 30) {
                return "Month " + String.format("%02d", month) + " has only 30 days!";
            }
        }

        return null;
    }

    private boolean isClosingAfterOpening(String opening, String closing) {
        String[] openParts = opening.split("-");
        String[] closeParts = closing.split("-");

        int openDay = Integer.parseInt(openParts[0]);
        int openMonth = Integer.parseInt(openParts[1]);
        int openYear = Integer.parseInt(openParts[2]);

        int closeDay = Integer.parseInt(closeParts[0]);
        int closeMonth = Integer.parseInt(closeParts[1]);
        int closeYear = Integer.parseInt(closeParts[2]);

        if (closeYear > openYear) return true;
        if (closeYear < openYear) return false;

        if (closeMonth > openMonth) return true;
        if (closeMonth < openMonth) return false;

        return closeDay > openDay;
    }
}
