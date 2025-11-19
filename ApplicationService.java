public class ApplicationService implements IApplicationService {
    private final IUserRepository<Student> studentRepo;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;

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
            outputService.displayError("Cannot apply: limit reached (max 3) or already accepted.");
            return false;
        }

        if (!"Approved".equals(internship.getStatus()) || !internship.isVisible()) {
            outputService.displayError("Internship is not available.");
            return false;
        }

        if (!internship.hasAvailableSlots()) {
            outputService.displayError("No available slots for this internship.");
            return false;
        }

        String preferredMajor = internship.getPreferredMajor();
        if (!preferredMajor.equalsIgnoreCase(student.getMajor()) &&
                !preferredMajor.equalsIgnoreCase("all")) {
            outputService.displayError("Your major does not match the internship requirements.");
            return false;
        }

        int year = student.getYearOfStudy();
        String level = internship.getLevel();

        if (year <= 2) {
            if (!level.equalsIgnoreCase("Basic")) {
                outputService.displayError("Year 1 and 2 students can only apply for Basic-level internships.");
                return false;
            }
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
            outputService.displayError("Student or internship not found.");
            return false;
        }

        internship.updateStudentStatus(studentID, "Approved");
        outputService.displayMessage("Application accepted!");
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
