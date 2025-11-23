import java.util.*;

// Interface for loading data from external sources
public interface IDataLoader {
    List<Student> loadStudents(String filename);
    List<CompanyRepresentative> loadCompanyReps(String filename);
    List<CareerCenterStaff> loadStaff(String filename);
}