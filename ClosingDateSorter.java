import java.util.*;

public class ClosingDateSorter implements IInternshipSorter {
    @Override
    public void sort(List<Internship> internships) {
        internships.sort(Comparator.comparing(Internship::getClosingDate));
    }
}