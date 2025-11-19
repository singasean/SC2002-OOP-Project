import java.util.*;

// Single Responsibility - handles only authentication logic
public class AuthenticationService implements IAuthenticationService {
    private final Map<String, String> credentials; // userID -> password
    private final Set<String> loggedInUsers;
    private final IOutputService outputService;

    public AuthenticationService(IOutputService outputService) {
        this.credentials = new HashMap<>();
        this.loggedInUsers = new HashSet<>();
        this.outputService = outputService;
    }

    @Override
    public void registerUser(String userID, String password) {
        credentials.put(userID, password);
    }


    @Override
    public boolean authenticate(String userID, String password) {
        if (credentials.containsKey(userID) && credentials.get(userID).equals(password)) {
            loggedInUsers.add(userID);
            outputService.displayMessage("Login successful!");
            return true;
        }
        outputService.displayMessage("Login failed! Invalid credentials.");
        return false;
    }

    @Override
    public void logout(String userID) {
        if (loggedInUsers.contains(userID)) {
            loggedInUsers.remove(userID);
            outputService.displayMessage("Logout successful!");
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
}