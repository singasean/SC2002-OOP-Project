import java.util.*;

public class AlphabeticalSorter implements IInternshipSorter, Comparator<Internship> {
    @Override
    public void sort(List<Internship> internships) {
        internships.sort(this);
    }

    @Override
    public int compare(Internship i1, Internship i2) {
        return i1.getTitle().compareToIgnoreCase(i2.getTitle());
    }
}
