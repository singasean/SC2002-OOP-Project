import java.util.*;
/**
 * Interface defining the contract for Internship Data Access.
 * <p>
 * <b>Architectural Role:</b>
 * This interface belongs to the <b>Repository Layer</b>. It abstracts the storage details
 * of {@link Internship} objects. By coding against this interface, the
 * {@link ApplicationService} and {@link StudentMenuController} remain agnostic to whether
 * data is stored in a List, a SQL database, or a Cloud store.
 * </p>
 * <p>
 * <b>Design Principles:</b>
 * <ul>
 * <li><b>Dependency Inversion:</b> High-level services depend on this abstraction, not
 * on the concrete {@link InternshipRepository} class.</li>
 * <li><b>Separation of Concerns:</b> It segregates data access logic (finding/saving)
 * from business logic (approving/applying).</li>
 * </ul>
 * </p>
 */
public interface IInternshipRepository {
    /**
     * Persists a new Internship object into the storage system.
     *
     * @param internship The internship entity to save.
     */
    void add(Internship internship);
    /**
     * Retrieves a specific Internship by its unique ID.
     *
     * @param internshipID The unique identifier string (e.g., "INT001").
     * @return The {@link Internship} object if found, or {@code null} if not exists.
     */
    Internship getById(String internshipID);
    /**
     * Retrieves all Internships currently in the system.
     * <p>
     * <b>Usage:</b> This is the foundation for the "View Available Internships" feature.
     * Controllers typically fetch this list and then apply {@link IInternshipFilter} strategies
     * to narrow it down based on user criteria.
     * </p>
     *
     * @return A complete list of all internships (Approved, Pending, Filled, etc.).
     */
    List<Internship> getAll();
    /**
     * Domain-specific query to find all internships owned by a specific Company Representative.
     * <p>
     * <b>Why this is needed:</b>
     * A Company Representative needs to see <i>their</i> specific postings to manage them.
     * Iterating through {@code getAll()} and filtering manually in the controller would be inefficient.
     * This method pushes that query logic down to the data layer.
     * </p>
     *
     * @param repID The User ID of the Company Representative.
     * @return A list of internships posted by that user.
     */
    List<Internship> getByRepresentativeID(String repID);
    /**
     * Generates a new, unique identifier for an internship.
     * <p>
     * <b>Importance:</b>
     * To maintain data integrity, every internship must have a unique Primary Key.
     * This method encapsulates the logic for creating that key (e.g., finding the max current ID
     * and incrementing it), ensuring no collisions occur when multiple users post jobs.
     * </p>
     *
     * @return A unique ID string (e.g., "INT055").
     */
    String generateNextID();
}