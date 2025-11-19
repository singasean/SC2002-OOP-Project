// Single Responsibility - handles application business logic
public class ApplicationService implements IApplicationService {
    private final IUserRepository<Student> studentRepo;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;

    // Dependency Injection
    public ApplicationService(IUserRepository<Student> studentRepo,
                             IInternshipRepository internshipRepo,
                             IOutputService outputService) {
        this.studentRepo = studentRepo;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
    }

    @Override
    public boolean applyForInternship(String studentID, String internshipID) {
        Student student = studentRepo.getById(studentID);
        Internship internship = internshipRepo.getById(internshipID);

        if (student == null || internship == null) {
            outputService.displayError("Student or internship not found.");
            return false;
        }

        if (!student.canApply()) {
            outputService.displayError("Cannot apply: limit reached or already accepted.");
            return false;
        }

        if (!"Approved".equals(internship.getStatus()) || !internship.isVisible()) {
            outputService.displayError("Internship is not available.");
            return false;
        }

        student.addApplication(internshipID);
        internship.setStudentStatus(studentID, "Pending");
        outputService.displayMessage("Application submitted successfully!");
        return true;
    }

    @Override
    public boolean withdrawApplication(String studentID, String internshipID) {
        Student student = studentRepo.getById(studentID);
        Internship internship = internshipRepo.getById(internshipID);

        if (student == null || internship == null) {
            return false;
        }

        student.removeApplication(internshipID);
        internship.setStudentStatus(studentID, "Withdrawn");
        outputService.displayMessage("Application withdrawn successfully!");
        return true;
    }

    @Override
    public boolean approveApplication(String internshipID, String studentID) {
        Student student = studentRepo.getById(studentID);
        Internship internship = internshipRepo.getById(internshipID);

        if (student == null || internship == null) {
            return false;
        }

        if (!internship.hasAvailableSlots()) {
            outputService.displayError("No available slots.");
            return false;
        }

        internship.setStudentStatus(studentID, "Approved");
        internship.incrementConfirmedSlots();
        student.setAcceptedPlacement(internshipID);
        outputService.displayMessage("Application approved!");
        return true;
    }

    @Override
    public boolean rejectApplication(String internshipID, String studentID) {
        Internship internship = internshipRepo.getById(internshipID);

        if (internship == null) {
            return false;
        }

        internship.setStudentStatus(studentID, "Rejected");
        outputService.displayMessage("Application rejected.");
        return true;
    }
}