import java.util.*;
/**
 * A concrete filter that selects internships based on the preferred major.
 */
public class MajorFilter implements IInternshipFilter {
    private final String major;
    /**
     * Constructs a new MajorFilter.
     *
     * @param targetMajor The major to filter for (e.g., "Computer Science").
     */
    public MajorFilter(String major) {
        this.major = major;
    }
    /**
     * Filters the list to include only internships matching the target major.
     *
     * @param internships The list of internships to check.
     * @return A filtered list of internships matching the target major.
     */
    @Override
    public List<Internship> filter(List<Internship> internships) {
        if ("all".equals(major)) {
            return internships;
        }

        List<Internship> result = new ArrayList<>();
        for (Internship i : internships) {
            if (i.getPreferredMajor().equalsIgnoreCase(major)) {
                result.add(i);
            }
        }
        return result;
    }
}