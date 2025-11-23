import java.util.*;
/**
 * A concrete strategy for sorting Internships by their Closing Date.
 * <p>
 * This class implements the <b>Strategy Design Pattern</b>. It relies on the String
 * comparison of dates formatted as "DD-MM-YYYY".
 * </p>
 * <p>
 * <b>Note:</b> For robust date sorting in a production environment, this should ideally
 * parse Strings into {@code LocalDate} objects. Currently, it performs lexicographical sorting.
 * </p>
 */
public class ClosingDateSorter implements IInternshipSorter, Comparator<Internship> {
    /**
     * Sorts the provided list of internships in-place based on closing date.
     *
     * @param internships The list of {@link Internship} objects to be sorted.
     */
    @Override
    public void sort(List<Internship> internships) {
        internships.sort(this);
    }
    /**
     * Compares two Internship objects based on their closing date string.
     *
     * @param i1 The first {@link Internship} to compare.
     * @param i2 The second {@link Internship} to compare.
     * @return A negative integer, zero, or a positive integer as the first date
     * is lexicographically less than, equal to, or greater than the second.
     */

    @Override
    public int compare(Internship i1, Internship i2) {
        return i1.getClosingDate().compareTo(i2.getClosingDate());
    }
}
