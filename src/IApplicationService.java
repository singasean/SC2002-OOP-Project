/**
 * Interface defining the business logic contract for Internship Applications.
 * <p>
 * <b>Architectural Role:</b>
 * This interface sits in the <b>Service Layer</b>. It separates the <i>"What"</i> (the available operations)
 * from the <i>"How"</i> (the specific validation rules in {@link ApplicationService}).
 * </p>
 * <p>
 * <b>Design Principles:</b>
 * <ul>
 * <li><b>Interface Segregation Principle (ISP):</b> This interface focuses solely on the
 * Application/Withdrawal workflow. It is distinct from {@link IApprovalService}, which handles
 * administrative tasks. This ensures that a Student Controller isn't exposed to Admin methods.</li>
 * <li><b>Dependency Inversion:</b> Controllers depend on this interface, allowing us to swap
 * the implementation (e.g., for testing mock objects) without breaking the UI.</li>
 * </ul>
 * </p>
 */
public interface IApplicationService {
    /**
     * Attempts to submit a new application for a student.
     * <p>
     * <b>The Contract:</b> Implementing classes must enforce eligibility rules:
     * <ul>
     * <li>Student Year/Major requirements.</li>
     * <li>Internship availability (Slots > 0).</li>
     * <li>Duplicate application prevention.</li>
     * </ul>
     * </p>
     *
     * @param studentID    The ID of the applicant.
     * @param internshipID The ID of the target internship.
     * @return {@code true} if the application was successfully created.
     */
    boolean applyForInternship(String studentID, String internshipID);
    /**
     * Immediately removes an application.
     * <p>
     * <b>Use Case:</b> Typically used for system cleanup (e.g., when a student accepts Offer A,
     * the system automatically calls this to remove applications B and C).
     * For user-initiated withdrawals from active placements, {@link #requestWithdrawal} is usually preferred.
     * </p>
     *
     * @param studentID    The student ID.
     * @param internshipID The internship ID.
     * @return {@code true} if the application was removed.
     */
    boolean withdrawApplication(String studentID, String internshipID);
    /**
     * Updates an application status to "Approved".
     * <p>
     * <b>Actor:</b> This is used by the <b>Company Representative</b>.
     * </p>
     *
     * @param internshipID The internship ID.
     * @param studentID    The student to approve.
     * @return {@code true} if successful.
     */
    boolean approveApplication(String internshipID, String studentID);
    /**
     * Updates an application status to "Rejected".
     * <p>
     * <b>Actor:</b> This is used by the <b>Company Representative</b>.
     * </p>
     *
     * @param internshipID The internship ID.
     * @param studentID    The student to reject.
     * @return {@code true} if successful.
     */
    boolean rejectApplication(String internshipID, String studentID);
    /**
     * Initiates a withdrawal workflow requiring Staff review.
     * <p>
     * <b>State Transition:</b> Changes status from "Approved/Confirmed" to "Pending Withdrawal".
     * This prevents students from unilaterally quitting confirmed placements without oversight.
     * </p>
     *
     * @param studentID    The student ID.
     * @param internshipID The internship ID.
     * @param reason       The justification for withdrawal.
     * @return {@code true} if the request was successfully queued.
     */
    boolean requestWithdrawal(String studentID, String internshipID, String reason);
    /**
     * Finalizes a withdrawal request.
     * <p>
     * <b>Actor:</b> This is used by <b>Career Center Staff</b>.
     * <br>
     * <b>Effect:</b> Frees up the internship slot and removes the application record.
     * </p>
     *
     * @param internshipID The internship ID.
     * @param studentID    The student ID.
     * @return {@code true} if approved.
     */
    boolean approveWithdrawal(String internshipID, String studentID);
    /**
     * Denies a withdrawal request.
     * <p>
     * <b>Actor:</b> This is used by <b>Career Center Staff</b>.
     * <br>
     * <b>Effect:</b> Reverts the status back to its previous state (e.g., "Confirmed"),
     * forcing the student to remain in the placement.
     * </p>
     *
     * @param internshipID The internship ID.
     * @param studentID    The student ID.
     * @return {@code true} if rejected.
     */
    boolean rejectWithdrawal(String internshipID, String studentID);
}
