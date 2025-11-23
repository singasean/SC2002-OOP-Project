
import java.util.*;

public class LevelFilter implements IInternshipFilter {
    private final String level;

    public LevelFilter(String level) {
        this.level = level;
    }

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