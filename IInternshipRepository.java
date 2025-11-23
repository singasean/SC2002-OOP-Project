import java.util.*;

public interface IInternshipRepository {
    void add(Internship internship);
    Internship getById(String internshipID);
    List<Internship> getAll();
    List<Internship> getByRepresentativeID(String repID);
    String generateNextID();
}