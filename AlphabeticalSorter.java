import java.util.*;

// Strategy Pattern - different sorting strategies
public class AlphabeticalSorter implements IInternshipSorter {
    @Override
    public void sort(List<Internship> internships) {
        internships.sort(Comparator.comparing(Internship::getTitle));
    }
}