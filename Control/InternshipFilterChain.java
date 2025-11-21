import java.util.*;

// Open-Closed Principle - can add new filters without modifying existing code
public class InternshipFilterChain implements IInternshipFilter {
    private final List<IInternshipFilter> filters;

    public InternshipFilterChain() {
        this.filters = new ArrayList<>();
    }

    public void addFilter(IInternshipFilter filter) {
        filters.add(filter);
    }

    @Override
    public List<Internship> filter(List<Internship> internships) {
        List<Internship> result = internships;
        for (IInternshipFilter filter : filters) {
            result = filter.filter(result);
        }
        return result;
    }
}