import java.util.*;

public class MajorFilter implements IInternshipFilter {
    private final String major;

    public MajorFilter(String major) {
        this.major = major;
    }

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