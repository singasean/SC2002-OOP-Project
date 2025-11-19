import java.util.*;

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

    @Override
    public String generateNextID() {
        return "INT" + (nextID++);
    }
}