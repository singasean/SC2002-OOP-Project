import java.util.*;

public class CareerCenterStaff extends User {

    public CareerCenterStaff(String userID, String name, String password) {
        super(userID, name, password);
    }

    // Load from CSV (same format as students: ID,Name,Department)
    public static List<CareerCenterStaff> loadFromCSVLines(List<String> csvLines) {
        List<CareerCenterStaff> staffList = new ArrayList<>();
        for (int i = 1; i < csvLines.size(); i++) {
            String[] data = csvLines.get(i).split(",");
            String id = data[0];
            String name = data[1];
            String password = "password";
            staffList.add(new CareerCenterStaff(id, name, password));
        }
        return staffList;
    }

    // Approve or reject a company representative
    public void reviewCompanyRep(CompanyRepresentative rep, boolean approve) {
        if (approve) {
            rep.setStatus("Approved");
            System.out.println("Company Representative " + rep.getName() + " has been APPROVED.");
        } else {
            rep.setStatus("Rejected");
            System.out.println("Company Representative " + rep.getName() + " has been REJECTED.");
        }
    }

    // Approve or reject an internship
    public void reviewInternship(Internship internship, boolean approve) {
        if (approve) {
            internship.setStatus("Approved");
            System.out.println("Internship '" + internship.getTitle() + "' has been APPROVED.");
        } else {
            internship.setStatus("Rejected");
            System.out.println("Internship '" + internship.getTitle() + "' has been REJECTED.");
        }
    }

    // Approve or reject a withdrawal request
    public void reviewWithdrawal(Student student, Internship internship, boolean approve) {
        if (!student.hasRequestedWithdrawal(internship)) {
            System.out.println("No withdrawal request found for this internship.");
            return;
        }

        if (approve) {
            student.removeApplicationInternal(internship);
            internship.setStatusForStudent(student.getUserID(), "Withdrawn");
            System.out.println("Withdrawal approved for " + student.getName() + " from " + internship.getTitle());
        } else {
            System.out.println("Withdrawal rejected for " + student.getName() + " from " + internship.getTitle());
        }
    }

    // Generate report
    public void generateReport(List<Internship> internships, String filter) {
        System.out.println("\n=== Internship Report ===");
        for (Internship i : internships) {
            boolean match = false;
            switch (filter.toLowerCase()) {
                case "approved" -> match = i.getStatus().equalsIgnoreCase("Approved");
                case "pending" -> match = i.getStatus().equalsIgnoreCase("Pending");
                case "rejected" -> match = i.getStatus().equalsIgnoreCase("Rejected");
                case "filled" -> match = i.getStatus().equalsIgnoreCase("Filled");
                default -> match = true; // no filter
            }
            if (match) {
                System.out.printf("%s | %s | %s | Slots: %d/%d | Visible: %s\n",
                        i.getTitle(), i.getCompanyName(), i.getStatus(),
                        i.getConfirmedSlots(), i.getTotalSlots(),
                        i.isVisible() ? "Yes" : "No");
            }
        }
    }

    @Override
    public String toString() {
        return "CareerCenterStaff{" +
                "userID='" + getUserID() + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }
}
