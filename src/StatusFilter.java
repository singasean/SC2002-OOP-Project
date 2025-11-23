import java.util.*;

// Open-Closed - each filter is a separate class
public class StatusFilter implements IInternshipFilter {
    private final String status;

    public StatusFilter(String status) {
        this.status = status;
    }

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