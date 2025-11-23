import java.util.*;
/**
 * Concrete implementation of the Generic User Repository.
 * <p>
 * Acts as an <b>In-Memory Database</b> storing user objects in an {@code ArrayList}.
 * </p>
 *
 * @param <T> The specific type of User (Student, CompanyRepresentative, etc.).
 */
// Single Responsibility - manages user storage only
public class UserRepository<T extends User> implements IUserRepository<T> {
    private final Map<String, T> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    @Override
    public void add(T user) {
        users.put(user.getUserID(), user);
    }

    @Override
    public T getById(String userID) {
        return users.get(userID);
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean exists(String userID) {
        return users.containsKey(userID);
    }

    @Override
    public void remove(String userID) {
        users.remove(userID);
    }
}