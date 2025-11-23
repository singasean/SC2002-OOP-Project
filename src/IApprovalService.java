// Interface for approval operations
/**
 * Interface defining the contract for administrative approval workflows.
 * <p>
 * <b>Architectural Role:</b>
 * This interface sits in the <b>Service Layer</b>. It encapsulates the "Gatekeeping" logic
 * required to moderate the system.
 * </p>
 * <p>
 * <b>Design Principles:</b>
 * <ul>
 * <li><b>Interface Segregation Principle (ISP):</b> This is the prime example of ISP in the system.
 * By moving these high-privilege operations into their own interface, we ensure that
 * standard user controllers (like {@link StudentMenuController}) have no dependency on,
 * and thus no access to, administrative functions.</li>
 * <li><b>Role-Based Responsibility:</b> This interface is primarily injected into the
 * {@link StaffMenuController}, effectively binding administrative capabilities to the Staff role.</li>
 * </ul>
 * </p>
 */
public interface IApprovalService {
    /**
     * Approves a pending Company Representative account.
     * <p>
     * <b>System Effect:</b>
     * 1. Changes the User's status from "Pending" to "Approved".
     * 2. Unlocks the account in the {@link AuthenticationService}, allowing the user to log in.
     * </p>
     *
     * @param repID The ID of the representative to approve.
     * @return {@code true} if the operation was successful.
     */
    boolean approveCompanyRep(String repID);
    /**
     * Rejects a pending Company Representative account.
     * <p>
     * <b>System Effect:</b>
     * 1. Changes the User's status to "Rejected".
     * 2. Prevents the user from logging into the system.
     * </p>
     *
     * @param repID The ID of the representative to reject.
     * @return {@code true} if the operation was successful.
     */
    boolean rejectCompanyRep(String repID);
    /**
     * Approves a new Internship posting.
     * <p>
     * <b>System Effect:</b>
     * 1. Changes the Internship status from "Pending" to "Approved".
     * 2. Makes the internship visible to Students in the "Available Internships" list
     * (provided the visibility toggle is also on).
     * </p>
     *
     * @param internshipID The ID of the internship.
     * @return {@code true} if the operation was successful.
     */
    boolean approveInternship(String internshipID);
    /**
     * Rejects a new Internship posting.
     * <p>
     * <b>System Effect:</b>
     * 1. Changes the Internship status to "Rejected".
     * 2. Ensures the internship remains hidden from Student views.
     * </p>
     *
     * @param internshipID The ID of the internship.
     * @return {@code true} if the operation was successful.
     */
    boolean rejectInternship(String internshipID);
}