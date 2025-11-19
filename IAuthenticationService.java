public interface IAuthenticationService {
    void registerUser(String userID, String password);
    void registerEmail(String email, String userID);  // NEW
    boolean authenticate(String userIDOrEmail, String password);
    void logout(String userID);
    String getUserIDFromEmail(String email);  // NEW
}

