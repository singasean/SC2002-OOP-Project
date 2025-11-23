import java.io.*;
import java.util.*;

public class CSVDataSaver implements IDataSaver {

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