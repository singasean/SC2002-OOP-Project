// Interface Segregation Principle - separate authentication concerns
public interface IAuthenticationService {
    void registerUser(String userID, String password);  // ADD THIS
    boolean authenticate(String userID, String password);
    void logout(String userID);
}
