import java.util.*;
/**
 * Generic Interface defining data access operations for User entities.
 * <p>
 * This interface uses <b>Java Generics</b> ({@code <T extends User>}) to allow
 * a single contract to handle Students, Company Reps, and Staff while maintaining
 * strict type safety. It adheres to the <b>Dependency Inversion Principle</b>.
 * </p>
 *
 * @param <T> The specific type of User this repository manages.
 */
// Dependency Inversion - depend on abstraction, not concrete implementation
public interface IUserRepository<T extends User> {
    void add(T user);
    T getById(String userID);
    List<T> getAll();
    boolean exists(String userID);
    void remove(String userID);
}