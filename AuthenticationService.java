import java.util.*;

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