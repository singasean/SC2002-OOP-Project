/**
 * Represents a Career Center Staff member (Administrator) in the Internship Management System.
 * <p>
 * This class extends the {@link User} base class. It serves as the primary administrative
 * entity in the system.
 * </p>
 * <p>
 * <b>Architectural Note:</b>
 * While this class does not contain complex business logic methods itself (following the
 * <b>Separation of Concerns</b>), its existence enables the {@link MenuControllerFactory}
 * to inject the powerful {@link ApprovalService} into the user's session. This ensures that
 * administrative capabilities are tied to the <i>type</i> of the user object.
 * </p>
 */
public class CareerCenterStaff extends User {
    /**
     * Constructs a new Career Center Staff user.
     * <p>
     * Note: While the source CSV may contain additional details (Department, Email),
     * the system currently only requires ID and Name for identification and logging purposes.
     * </p>
     *
     * @param userID The unique identifier for the staff member (e.g., "admin1").
     * @param name   The full name of the staff member.
     */
    public CareerCenterStaff(String userID, String name) {
        super(userID, name);
    }
    /**
     * Identifies the specific role of this user in the hierarchy.
     * <p>
     * This implementation fulfills the abstract contract defined in {@link User#getRole()}.
     * It allows the {@link AuthenticationService} and UI controllers to log and display
     * the user's role generically without needing {@code instanceof} checks in every print statement.
     * </p>
     *
     * @return The string "Career Center Staff".
     */
    @Override
    public String getRole() {
        return "Career Center Staff";
    }
}