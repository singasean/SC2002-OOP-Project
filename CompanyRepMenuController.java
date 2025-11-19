import java.util.*;

public class CompanyRepMenuController implements IMenuController {
    private final CompanyRepresentative currentRep;
    private final IInternshipRepository internshipRepo;
    private final IApplicationService applicationService;
    private final IOutputService outputService;
    private final Scanner scanner;

    public CompanyRepMenuController(CompanyRepresentative rep,
                                   IInternshipRepository internshipRepo,
                                   IApplicationService applicationService,
                                   IOutputService outputService,
                                   Scanner scanner) {
        this.currentRep = rep;
        this.internshipRepo = internshipRepo;
        this.applicationService = applicationService;
        this.outputService = outputService;
        this.scanner = scanner;
    }

    @Override
    public void displayMenu() {
        outputService.displayMessage("\n===== Company Representative Menu =====");
        outputService.displayMessage("1. Post New Internship");
        outputService.displayMessage("2. View My Internships");
        outputService.displayMessage("3. Review Applications");
        outputService.displayMessage("4. Logout");
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
                reviewApplications();
                break;
            case "4":
                outputService.displayMessage("Logging out...");
                break;
            default:
                outputService.displayError("Invalid choice!");
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
        outputService.displayMessage("Enter level (Undergraduate/Graduate):");
        String level = scanner.nextLine();
        outputService.displayMessage("Enter preferred major:");
        String major = scanner.nextLine();
        outputService.displayMessage("Enter opening date (YYYY-MM-DD):");
        String opening = scanner.nextLine();
        outputService.displayMessage("Enter closing date (YYYY-MM-DD):");
        String closing = scanner.nextLine();
        outputService.displayMessage("Enter total slots:");
        int slots = Integer.parseInt(scanner.nextLine());

        String internshipID = internshipRepo.generateNextID();
        Internship internship = new Internship(internshipID, title, desc, level, major,
            opening, closing, slots, currentRep.getCompanyName(), currentRep.getUserID());

        internshipRepo.add(internship);
        currentRep.addInternship(internshipID);
        outputService.displayMessage("Internship posted! (Pending approval)");
    }

    private void viewMyInternships() {
        List<Internship> myInternships = internshipRepo.getByRepresentativeID(currentRep.getUserID());
        if (myInternships.isEmpty()) {
            outputService.displayMessage("No internships posted.");
            return;
        }

        for (Internship i : myInternships) {
            outputService.displayMessage(String.format("%s - %s [%s] (Slots: %d/%d)",
                i.getInternshipID(), i.getTitle(), i.getStatus(),
                i.getConfirmedSlots(), i.getTotalSlots()));
        }
    }

    private void reviewApplications() {
        outputService.displayMessage("Enter Internship ID:");
        String internshipID = scanner.nextLine();
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

        for (Map.Entry<String, String> entry : statuses.entrySet()) {
            if ("Pending".equals(entry.getValue())) {
                outputService.displayMessage(String.format("Student %s - Status: %s",
                    entry.getKey(), entry.getValue()));
            }
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
}