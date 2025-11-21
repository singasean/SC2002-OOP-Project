// Single Responsibility - only stores user data
// Open-Closed - can be extended without modification
public abstract class User {
    protected String userID;
    protected String name;

    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    // Getters only - no business logic
    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Template method for role-specific behavior
    public abstract String getRole();
}