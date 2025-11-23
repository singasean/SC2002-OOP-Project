import java.util.*;

// Dependency Inversion - depend on abstraction, not concrete implementation
public interface IUserRepository<T extends User> {
    void add(T user);
    T getById(String userID);
    List<T> getAll();
    boolean exists(String userID);
    void remove(String userID);
}