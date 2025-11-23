// Single Responsibility - only stores user data
// Open-Closed - can be extended without modification

/**
 * Abstract base class representing a generic user in the Internship Management System.
 * <p>
 * This class serves as the foundation for the <b>User Hierarchy</b>. It enforces the
 * <b>Liskov Substitution Principle</b> by defining common behaviors (like {@code getRole()})
 * that all specific user types must implement.
 * </p>
 */
public abstract class User {
    protected String userID;
    protected String name;

    /**
     * Constructs a new User with the specified ID and name.
     *
     * @param userID The unique identifier for the user.
     * @param name   The full name of the user.
     */
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
    /**
     * Abstract method that forces subclasses to identify their specific role.
     * <p>
     * This enables polymorphic behavior in the UI and logging systems, allowing
     * generic handling of {@code User} objects while still accessing specific role names.
     * </p>
     *
     * @return A string representation of the user's role (e.g., "Student").
     */
    public abstract String getRole();
}