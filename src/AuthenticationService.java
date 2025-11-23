import java.util.*;
/**
 * Concrete implementation of the Authentication Service.
 * <p>
 * <b>Security Note:</b> This class separates security credentials (passwords) from the
 * User Profile objects stored in {@link UserRepository}. This ensures that a compromise
 * of the user list does not automatically compromise user passwords.
 * </p>
 * <p>
 * It also enforces <b>Role-Based Access Control</b> at the login stage, preventing
 * "Pending" or "Rejected" Company Representatives from accessing the system.
 * </p>
 */
public class AuthenticationService implements IAuthenticationService {
    private final Map<String, String> credentials;
    private final Map<String, String> emailToUserID;
    private final Set<String> loggedInUsers;
    private final IOutputService outputService;
    private IUserRepository<CompanyRepresentative> companyRepo;

    public AuthenticationService(IOutputService outputService) {
        this.credentials = new HashMap<>();
        this.emailToUserID = new HashMap<>();
        this.loggedInUsers = new HashSet<>();
        this.outputService = outputService;
    }

    public void setCompanyRepository(IUserRepository<CompanyRepresentative> companyRepo) {
        this.companyRepo = companyRepo;
    }

    @Override
    public void registerUser(String userID, String password) {
        credentials.put(userID, password);
    }

    public void registerEmail(String email, String userID) {
        emailToUserID.put(email.toLowerCase(), userID);
    }
    /**
     * Authenticates a user against the stored credentials.
     *
     * @param userIDOrEmail The user's ID or registered email address.
     * @param password      The provided password.
     * @return {@code true} if credentials are valid AND the account is active/approved.
     */
    @Override
    public boolean authenticate(String userIDOrEmail, String password) {
        String userID = userIDOrEmail;

        // Handle email login
        if (userIDOrEmail.contains("@")) {
            userID = emailToUserID.get(userIDOrEmail.toLowerCase());
            if (userID == null) {
                outputService.displayMessage("Login failed! Invalid credentials.");
                return false;
            }
        }

        // Check password
        if (credentials.containsKey(userID) && credentials.get(userID).equals(password)) {

            // === FIX: Specific checks for Company Rep status ===
            if (userID.startsWith("CR") && companyRepo != null) {
                CompanyRepresentative rep = companyRepo.getById(userID);
                if (rep != null) {
                    // 1. Check for Rejection
                    if ("Rejected".equalsIgnoreCase(rep.getStatus())) {
                        outputService.displayError("Login Denied: Your account application was REJECTED.");
                        return false;
                    }
                    // 2. Check for Pending (Non-Approved)
                    if (!rep.isApproved()) {
                        outputService.displayError("Login Denied: Your account is currently PENDING approval.");
                        return false;
                    }
                }
            }
            // ===================================================

            loggedInUsers.add(userID);
            return true;
        }

        outputService.displayMessage("Login failed! Invalid credentials.");
        return false;
    }

    @Override
    public void logout(String userID) {
        if (loggedInUsers.contains(userID)) {
            loggedInUsers.remove(userID);
        } else {
            outputService.displayMessage("No user is currently logged in.");
        }
    }

    public boolean isLoggedIn(String userID) {
        return loggedInUsers.contains(userID);
    }
    /**
     * Updates the user's password.
     *
     * @param userID      The ID of the user.
     * @param oldPassword The current password (for verification).
     * @param newPassword The new desired password.
     * @return {@code true} if the old password matched and update was successful.
     */
    public boolean changePassword(String userID, String oldPassword, String newPassword) {
        if (credentials.containsKey(userID) && credentials.get(userID).equals(oldPassword)) {
            credentials.put(userID, newPassword);
            outputService.displayMessage("Password changed successfully!");
            return true;
        }
        outputService.displayMessage("Password change failed!");
        return false;
    }

    public String getUserIDFromEmail(String email) {
        return emailToUserID.get(email.toLowerCase());
    }
}