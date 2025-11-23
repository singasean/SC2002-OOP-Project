
import java.util.*;
/**
 * A concrete filter that selects internships based on their difficulty level.
 * (e.g., "Basic", "Intermediate", "Advanced").
 */
public class LevelFilter implements IInternshipFilter {
    private final String level;
    /**
     * Constructs a new LevelFilter.
     *
     * @param targetLevel The specific level to filter for (e.g., "Basic").
     * Case-insensitive comparison is used.
     */
    public LevelFilter(String level) {
        this.level = level;
    }
    /**
     * Filters the list to include only internships matching the target level.
     *
     * @param internships The list of internships to check.
     * @return A filtered list of internships matching the target level.
     */
    @Override
    public List<Internship> filter(List<Internship> internships) {
        if ("all".equals(level)) {
            return internships;
        }

        List<Internship> result = new ArrayList<>();
        for (Internship i : internships) {
            if (i.getLevel().equalsIgnoreCase(level)) {
                result.add(i);
            }
        }
        return result;
    }
}