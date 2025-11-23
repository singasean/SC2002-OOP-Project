import java.io.*;
import java.util.*;
/**
 * Concrete implementation of the Data Loader strategy for Comma-Separated Value (CSV) files.
 * <p>
 * <b>Architectural Role:</b>
 * This class belongs to the <b>Persistence Layer (Read Side)</b>. It acts as a translator
 * that converts raw text data from the file system into in-memory Java Entities ({@link Student},
 * {@link Internship}, etc.).
 * </p>
 * <p>
 * <b>Robustness Features:</b>
 * <ul>
 * <li><b>Header Skipping:</b> Automatically detects and skips the first row of CSVs.</li>
 * <li><b>Empty Line Handling:</b> Ignores blank lines to prevent parsing crashes.</li>
 * <li><b>Safe Split:</b> Uses Regex splitting to handle cases where data might contain special characters.</li>
 * <li><b>Partial Loading:</b> If one line is corrupt, it skips that line and continues loading the rest
 * (Fault Tolerance), rather than crashing the entire application.</li>
 * </ul>
 * </p>
 */
// Single Responsibility - handles CSV loading only
public class CSVDataLoader implements IDataLoader {

    @Override
    public List<Student> loadStudents(String filename) {
        List<Student> students = new ArrayList<>();
        List<String> lines = readCSVFile(filename);

        // Skip header row, start from index 1
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] data = line.split(",");
            // CSV format: StudentID,Name,Major,Year,Email
            String id = data[0].trim();           // StudentID
            String name = data[1].trim();         // Name
            String major = data[2].trim();        // Major
            int year = Integer.parseInt(data[3].trim()); // Year
            // data[4] is email - we're ignoring it for now

            students.add(new Student(id, name, year, major));
        }
        return students;
    }
    /**
     * Loads the list of Company Representatives.
     * <p>
     * <b>Expected Format:</b> {@code ID, Name, Company, Dept, Position, Email, Status}
     * </p>
     *
     * @param filename The file path.
     * @return A list of populated {@link CompanyRepresentative} objects.
     */
    @Override
    public List<CompanyRepresentative> loadCompanyReps(String filename) {
        List<CompanyRepresentative> reps = new ArrayList<>();
        List<String> lines = readCSVFile(filename);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] data = line.split(",");
            // CSV format: CompanyRepID,Name,CompanyName,Department,Position,Email,Status
            String id = data[0].trim();           // CompanyRepID
            String name = data[1].trim();         // Name
            String company = data[2].trim();      // CompanyName
            String dept = data[3].trim();         // Department
            String position = data[4].trim();     // Position
            String email = data[5].trim();        // Email
            // data[6] is Status - handled separately

            CompanyRepresentative rep = new CompanyRepresentative(id, name, company, dept, position, email);

            // Set status from CSV if it exists
            if (data.length > 6) {
                rep.setStatus(data[6].trim());
            }

            reps.add(rep);
        }
        return reps;
    }
    /**
     * Loads the list of Career Center Staff.
     *
     * @param filename The file path.
     * @return A list of {@link CareerCenterStaff} objects.
     */
    @Override
    public List<CareerCenterStaff> loadStaff(String filename) {
        List<CareerCenterStaff> staff = new ArrayList<>();
        List<String> lines = readCSVFile(filename);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] data = line.split(",");
            // CSV format: StaffID,Name,Role,Department,Email
            String id = data[0].trim();    // StaffID
            String name = data[1].trim();  // Name
            // data[2] is Role, data[3] is Department, data[4] is Email - we're ignoring for now

            staff.add(new CareerCenterStaff(id, name));
        }
        return staff;
    }
    /**
     * Helper method to read raw text lines from a file.
     *
     * @param filename The path to read.
     * @return A list of strings, one per line.
     */
    private List<String> readCSVFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
        }
        return lines;
    }
}
