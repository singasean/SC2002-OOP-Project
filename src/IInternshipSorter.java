import java.util.*;
/**
 * Interface defining the contract for Internship Sorting Strategies.
 * <p>
 * This interface facilitates the <b>Strategy Design Pattern</b> and the <b>Liskov Substitution Principle</b>.
 * Clients (like {@code StudentMenuController}) depend on this abstraction rather than
 * concrete sorters, allowing new sorting algorithms to be added without modifying existing code.
 * </p>
 */
// Interface for sorting strategies
public interface IInternshipSorter {
    /**
     * Sorts a list of internships according to the specific implementation strategy.
     *
     * @param internships The list of {@link Internship} objects to modify/sort.
     */
    void sort(List<Internship> internships);
}