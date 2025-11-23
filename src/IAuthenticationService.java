/**
 * Interface defining the contract for User Authentication and Session Management.
 * <p>
 * <b>Architectural Role:</b>
 * This interface belongs to the <b>Service Layer</b>. It acts as the "Gatekeeper" or "Security Guard"
 * for the system. It isolates the security logic from the business logic (ApplicationService)
 * and data logic (UserRepository).
 * </p>
 * <p>
 * <b>Design Principles:</b>
 * <ul>
 * <li><b>Separation of Concerns:</b> By keeping authentication here, we ensure that
 * User Entity objects ({@link Student}, {@link CompanyRepresentative}) do not need to
 * store or manage their own passwords. This improves security design.</li>
 * <li><b>Dependency Inversion:</b> Clients like {@link InternshipManagementSystem} depend
 * on this abstraction, allowing us to potentially switch to a more secure implementation
 * (e.g., OAuth, Hashing) in the future without breaking the login flow.</li>
 * </ul>
 * </p>
 */
public interface IAuthenticationService {
    /**
     * Registers a new user credential pair in the security system.
     * <p>
     * <b>Note:</b> This stores the <i>Secret</i> (Password). The <i>Profile</i> (Name, Major, etc.)
     * is stored separately in the {@link IUserRepository}.
     * </p>
     *
     * @param userID   The unique User ID.
     * @param password The password to associate with this ID.
     */
    void registerUser(String userID, String password);
    /**
     * Associates an email address with a specific User ID.
     * <p>
     * <b>Use Case:</b> This enables the "Login by Email" feature. The system needs a lookup mechanism
     * to translate a user-entered email (e.g., "john@ntu.edu.sg") back into the canonical User ID
     * (e.g., "STU001") used for password verification.
     * </p>
     *
     * @param email  The user's email address.
     * @param userID The canonical User ID.
     */
    void registerEmail(String email, String userID);  // NEW
    /**
     * Verifies the identity of a user.
     * <p>
     * <b>The Contract:</b> Implementing classes must enforce:
     * 1. Credential matching (ID/Email + Password).
     * 2. Access Control (e.g., blocking "Rejected" or "Pending" accounts).
     * </p>
     *
     * @param userIDOrEmail The identifier provided by the user (ID or Email).
     * @param password      The password provided by the user.
     * @return {@code true} if identity is verified and access is granted.
     */
    boolean authenticate(String userIDOrEmail, String password);
    /**
     * Terminates the user's session.
     * <p>
     * Removes the user from the active session tracker, requiring them to re-authenticate
     * for any future restricted actions.
     * </p>
     *
     * @param userID The ID of the user logging out.
     */
    void logout(String userID);
    /**
     * Retrieves the canonical User ID associated with a given email.
     *
     * @param email The email address to look up.
     * @return The corresponding User ID, or {@code null} if not found.
     */
    String getUserIDFromEmail(String email);  // NEW
}

