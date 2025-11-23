import java.util.*;
/**
 * Interface defining the contract for Internship Filtering Strategies.
 * <p>
 * This follows the <b>Interface Segregation Principle</b> (specific single-purpose interface)
 * and enables the <b>Open/Closed Principle</b> (new filters can be created without changing existing logic).
 * </p>
 */
// Interface Segregation - separate filtering concerns
public interface IInternshipFilter {
    /**
     * Filters a list of internships based on specific criteria.
     *
     * @param internships The source list of {@link Internship} objects.
     * @return A new list containing only the internships that satisfy the filter criteria.
     */
    List<Internship> filter(List<Internship> internships);
}