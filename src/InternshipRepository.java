import java.util.*;
/**
 * Repository specifically for managing {@link Internship} objects.
 * <p>
 * Extends the basic CRUD functionality with domain-specific queries, such as
 * retrieving all internships created by a specific Company Representative.
 * </p>
 */
public class InternshipRepository implements IInternshipRepository {
    private final Map<String, Internship> internships;
    private int nextID = 1;
    
    public InternshipRepository() {
        this.internships = new HashMap<>();
    }

    @Override
    public void add(Internship internship) {
        internships.put(internship.getInternshipID(), internship);
    }

    @Override
    public Internship getById(String internshipID) {
        return internships.get(internshipID);
    }

    @Override
    public List<Internship> getAll() {
        return new ArrayList<>(internships.values());
    }
    /**
     * Retrieves all internships owned by a specific Company Representative.
     *
     * @param repID The ID of the company representative.
     * @return A list of internships associated with that representative.
     */
    @Override
    public List<Internship> getByRepresentativeID(String repID) {
        List<Internship> result = new ArrayList<>();
        for (Internship internship : internships.values()) {
            if (internship.getRepresentativeID().equals(repID)) {
                result.add(internship);
            }
        }
        return result;
    }
    /**
     * Generates a unique ID for a new internship.
     * <p>
     * Logic: Finds the current maximum numeric ID suffix and increments it.
     * </p>
     *
     * @return A new unique ID string.
     */
    @Override
    public String generateNextID() {
        return "INT" + (nextID++);
    }
}