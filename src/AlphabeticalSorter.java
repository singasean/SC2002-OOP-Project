import java.util.*;
/**
 * A concrete strategy for sorting Internships alphabetically by Title.
 * <p>
 * This class implements the <b>Strategy Design Pattern</b> (via {@link IInternshipSorter})
 * and the standard Java {@link Comparator} interface. It enables the system to
 * sort lists of internships in ascending alphabetical order based on the Case-Insensitive Title.
 * </p>
 */
public class AlphabeticalSorter implements IInternshipSorter, Comparator<Internship> {
    /**
     * Sorts the provided list of internships in-place using this class as the comparator.
     *
     * @param internships The list of {@link Internship} objects to be sorted.
     * The list is modified directly.
     */
    @Override
    public void sort(List<Internship> internships) {
        internships.sort(this);
    }
    /**
     * Compares two Internship objects lexicographically based on their titles.
     * <p>
     * This comparison is <b>case-insensitive</b> to ensure consistent user-friendly ordering
     * (e.g., "apple" and "Apple" are treated as adjacent).
     * </p>
     *
     * @param i1 The first {@link Internship} to compare.
     * @param i2 The second {@link Internship} to compare.
     * @return A negative integer, zero, or a positive integer as the first argument's title
     * is less than, equal to, or greater than the second.
     */

    @Override
    public int compare(Internship i1, Internship i2) {
        return i1.getTitle().compareToIgnoreCase(i2.getTitle());
    }
}
