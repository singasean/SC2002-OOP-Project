import java.util.*;

// Interface Segregation - separate filtering concerns
public interface IInternshipFilter {
    List<Internship> filter(List<Internship> internships);
}