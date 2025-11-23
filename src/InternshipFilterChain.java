import java.util.*;
/**
 * A Composite Filter implementation that allows chaining multiple filters together.
 * <p>
 * This class applies the <b>Chain of Responsibility</b> or <b>Decorator</b> concept
 * to filtering. It holds a list of {@link IInternshipFilter} objects and applies
 * them sequentially. An internship must pass <b>all</b> filters in the chain to be included in the result.
 * </p>
 */
// Open-Closed Principle - can add new filters without modifying existing code
public class InternshipFilterChain implements IInternshipFilter {
    private final List<IInternshipFilter> filters;
    /**
     * Initializes an empty filter chain.
     */
    public InternshipFilterChain() {
        this.filters = new ArrayList<>();
    }
    /**
     * Adds a new filter to the chain.
     *
     * @param filter The {@link IInternshipFilter} to add.
     */
    public void addFilter(IInternshipFilter filter) {
        filters.add(filter);
    }
    /**
     * Applies all added filters sequentially to the list of internships.
     * The output of one filter becomes the input for the next.
     *
     * @param internships The initial list of internships.
     * @return The final list of internships that satisfy ALL criteria.
     */
    @Override
    public List<Internship> filter(List<Internship> internships) {
        List<Internship> result = internships;
        for (IInternshipFilter filter : filters) {
            result = filter.filter(result);
        }
        return result;
    }
}