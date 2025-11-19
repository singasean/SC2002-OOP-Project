import java.util.*;

public class StaffMenuController implements IMenuController {
    private final CareerCenterStaff currentStaff;
    private final IApprovalService approvalService;
    private final IUserRepository<CompanyRepresentative> companyRepo;
    private final IUserRepository<Student> studentRepo;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;
    private final Scanner scanner;

    public StaffMenuController(CareerCenterStaff staff,
                              IApprovalService approvalService,
                              IUserRepository<CompanyRepresentative> companyRepo,
                              IUserRepository<Student> studentRepo,
                              IInternshipRepository internshipRepo,
                              IOutputService outputService,
                              Scanner scanner) {
        this.currentStaff = staff;
        this.approvalService = approvalService;
        this.companyRepo = companyRepo;
        this.studentRepo = studentRepo;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
        this.scanner = scanner;
    }

    @Override
    public void displayMenu() {
        outputService.displayMessage("\n===== Career Center Staff Menu =====");
        outputService.displayMessage("1. Review Company Representatives");
        outputService.displayMessage("2. Review Internships");
        outputService.displayMessage("3. View All Students");
        outputService.displayMessage("4. Generate Reports");
        outputService.displayMessage("5. Logout");
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                reviewCompanyReps();
                break;
            case "2":
                reviewInternships();
                break;
            case "3":
                viewStudents();
                break;
            case "4":
                generateReports();
                break;
            case "5":
                outputService.displayMessage("Logging out...");
                break;
            default:
                outputService.displayError("Invalid choice!");
        }
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
            outputService.displayMessage(String.format("%s - %s (Year %d, %s) [Visible: %s]",
                s.getUserID(), s.getName(), s.getYearOfStudy(), s.getMajor(),
                s.isVisible() ? "Yes" : "No"));
        }
    }

    private void generateReports() {
        outputService.displayMessage("Report generation feature coming soon!");
    }
}