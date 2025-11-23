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
        if (year <= 2 && !level.equalsIgnoreCase("Basic")) {
            outputService.displayError("Year 1 and 2 students can only apply for Basic-level internships.");
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

    @Override
    public boolean requestWithdrawal(String studentID, String internshipID, String reason) {
        Student student = studentRepo.getById(studentID);
        Internship internship = internshipRepo.getById(internshipID);

        if (student == null || internship == null) {
            outputService.displayError("Student or internship not found.");
            return false;
        }

        String currentStatus = internship.getStudentStatus(studentID);
        if (!"Confirmed".equals(currentStatus) && !"Approved".equals(currentStatus)) {
            outputService.displayError("Can only withdraw from confirmed or approved placements.");
            return false;
        }

        internship.requestWithdrawal(studentID, reason);
        outputService.displayMessage("Withdrawal request submitted. Awaiting staff approval.");
        return true;
    }

    @Override
    public boolean approveWithdrawal(String internshipID, String studentID) {
        Internship internship = internshipRepo.getById(internshipID);
        Student student = studentRepo.getById(studentID);

        if (internship == null || student == null) {
            return false;
        }

        boolean approved = internship.approveWithdrawal(studentID);
        if (approved) {
            if (internshipID.equals(student.getAcceptedPlacementID())) {
                student.setAcceptedPlacement(null);
            }
            student.removeApplication(internshipID);
            outputService.displayMessage("Withdrawal approved.");
        }
        return approved;
    }

    @Override
    public boolean rejectWithdrawal(String internshipID, String studentID) {
        Internship internship = internshipRepo.getById(internshipID);

        if (internship == null) {
            return false;
        }

        boolean rejected = internship.rejectWithdrawal(studentID);
        if (rejected) {
            outputService.displayMessage("Withdrawal rejected. Status reverted to Confirmed.");
        }
        return rejected;
    }
}
