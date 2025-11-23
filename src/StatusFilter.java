import java.util.*;
/**
 * A concrete filter that selects internships based on their current status.
 * (e.g., "Approved", "Pending", "Filled").
 */
// Open-Closed - each filter is a separate class
public class StatusFilter implements IInternshipFilter {
    private final String status;

    /**
     * Constructs a new StatusFilter.
     *
     * @param targetStatus The status to filter for.
     */
    public StatusFilter(String status) {
        this.status = status;
    }
    /**
     * Filters the list to include only internships matching the target status.
     *
     * @param internships The list of internships to check.
     * @return A filtered list of internships matching the target status.
     */
    @Override
    public List<Internship> filter(List<Internship> internships) {
        if ("all".equals(status)) {
            return internships;
        }

        List<Internship> result = new ArrayList<>();
        for (Internship i : internships) {
            if (i.getStatus().equalsIgnoreCase(status)) {
                result.add(i);
            }
        }
        return result;
    }
}