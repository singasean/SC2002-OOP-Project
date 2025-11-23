/**
 * Service class that encapsulates the core business logic for Internship Applications.
 * <p>
 * This class acts as the <b>Control Layer</b> for student-internship interactions.
 * It enforces all business rules regarding eligibility, such as:
 * <ul>
 * <li>Students typically must be Year 3 or above (unless applying for Basic level).</li>
 * <li>Students cannot exceed the maximum application limit.</li>
 * <li>Students cannot apply if they have already confirmed a placement.</li>
 * </ul>
 * </p>
 */
public class ApplicationService implements IApplicationService {
    private final IUserRepository<Student> studentRepo;
    private final IInternshipRepository internshipRepo;
    private final IOutputService outputService;
    /**
     * Constructs the ApplicationService with necessary dependencies.
     *
     * @param studentRepo    Repository to access student data.
     * @param internshipRepo Repository to access internship data.
     * @param outputService  Service to display messages to the user.
     */
    public ApplicationService(IUserRepository<Student> studentRepo,
                              IInternshipRepository internshipRepo,
                              IOutputService outputService) {
        this.studentRepo = studentRepo;
        this.internshipRepo = internshipRepo;
        this.outputService = outputService;
    }
    /**
     * Processes a student's application for an internship.
     * <p>
     * <b>Business Rules:</b>
     * 1. Student and Internship must exist.
     * 2. Student must not have reached application limit (3) or already accepted an offer.
     * 3. Internship must be "Approved" and visible.
     * 4. Internship must have available slots.
     * 5. Student's major must match (or be "All").
     * 6. Year 1 & 2 students can only apply for "Basic" level.
     * </p>
     *
     * @param studentID    The applicant's ID.
     * @param internshipID The target internship ID.
     * @return {@code true} if the application was successfully submitted.
     */
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
    /**
     * Immediately withdraws a student's application.
     * <p>
     * <b>Note:</b> This is typically used for cleanup (e.g., auto-withdrawing other applications
     * after confirming a placement). For user-initiated withdrawals from active placements,
     * use {@link #requestWithdrawal}.
     * </p>
     *
     * @param studentID    The student ID.
     * @param internshipID The internship ID.
     * @return {@code true} if successful.
     */
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
    /**
     * Approves a specific student's application (Used by Company Rep).
     *
     * @param internshipID The internship ID.
     * @param studentID    The student to approve.
     * @return {@code true} if approved.
     */
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
    /**
     * Rejects a specific student's application (Used by Company Rep).
     *
     * @param internshipID The internship ID.
     * @param studentID    The student to reject.
     * @return {@code true} if rejected.
     */
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
    /**
     * Submits a withdrawal request for a student.
     * <p>
     * If the student is already "Confirmed" or "Approved", this request requires
     * Staff approval and will set the status to "Pending Withdrawal".
     * </p>
     *
     * @param studentID    The student requesting withdrawal.
     * @param internshipID The internship ID.
     * @param reason       The reason for withdrawal.
     * @return {@code true} if the request was submitted.
     */
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
    /**
     * Approves a student's withdrawal request (Used by Staff).
     * <p>
     * 1. Updates status to "Withdrawn".
     * 2. Frees up a slot if the student was previously "Confirmed".
     * 3. Removes the application from the student's record.
     * </p>
     *
     * @param internshipID The internship ID.
     * @param studentID    The student ID.
     * @return {@code true} if approved.
     */
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
    /**
     * Rejects a student's withdrawal request (Used by Staff).
     * <p>
     * Reverts the student's status to their previous state (e.g., back to "Confirmed").
     * </p>
     *
     * @param internshipID The internship ID.
     * @param studentID    The student ID.
     * @return {@code true} if rejected.
     */
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
