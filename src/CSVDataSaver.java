import java.io.*;
import java.util.*;
/**
 * Concrete implementation of the Data Saver for CSV files.
 * <p>
 * <b>Architectural Role:</b>
 * This class handles the <b>Write Side</b> of the Persistence Layer. It is responsible for
 * "freezing" the current state of the application into text files so that data survives
 * when the program shuts down.
 * </p>
 * <p>
 * <b>Key Challenge - Serialization:</b>
 * While saving simple fields (like Name or ID) is easy, this class solves the difficult problem
 * of saving <b>Relational Data</b> (like which students applied to which internship) into a
 * flat file format that doesn't natively support relationships.
 * </p>
 */
public class CSVDataSaver implements IDataSaver {
    /**
     * Serializes and saves the list of Company Representatives to a CSV file.
     * <p>
     * <b>Why this is critical:</b>
     * The most important piece of data saved here is the <b>Status</b> column (Pending/Approved/Rejected).
     * The {@link ApprovalService} modifies this status in memory during runtime. If we fail to
     * write this back to the file, approved representatives would find themselves locked out
     * again upon the next login.
     * </p>
     * <p>
     * <b>Format:</b> {@code ID, Name, Company, Dept, Position, Email, Status}
     * </p>
     *
     * @param filename The target file path.
     * @param reps     The list of representatives to persist.
     */
    @Override
    public void saveCompanyReps(String filename, List<CompanyRepresentative> reps) {
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // 1. Write the Header (Must match the format expected by CSVDataLoader)
            // Format based on CSVDataLoader: ID,Name,Company,Dept,Position,Email,Status
            bw.write("CompanyRepID,Name,CompanyName,Department,Position,Email,Status");
            bw.newLine();

            // 2. Iterate and Write Data
            for (CompanyRepresentative rep : reps) {
                String line = String.format("%s,%s,%s,%s,%s,%s,%s",
                        rep.getUserID(),
                        rep.getName(),
                        rep.getCompanyName(),
                        rep.getDepartment(),
                        rep.getPosition(),
                        rep.getEmail(),
                        rep.getStatus() // This is the crucial part (Approved/Pending/Rejected)
                );
                bw.write(line);
                bw.newLine();
            }
            
            System.out.println("Company Representatives saved successfully to " + filename);
            
        } catch (IOException e) {
            System.err.println("Error saving company reps: " + e.getMessage());
            e.printStackTrace();
        }
    }
}