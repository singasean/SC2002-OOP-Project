import java.io.*;
import java.util.*;

public class Test {
    private static final String STUDENT_CSV = "sample_student_list.csv";
    private static final String COMPANY_CSV = "sample_company_representative_list.csv";
    private static final String CAREER_CSV = "sample_career_center_staff_list.csv";
    private static final String DEFAULT_PASSWORD = "password";

    private static List<Student> students = new ArrayList<>();
    private static List<CompanyRepresentative> companyReps = new ArrayList<>();
    private static List<Internship> allInternships = new ArrayList<>();
    private static List<CareerCenterStaff> careerStaff = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Internship Placement Management System ===");

        // Load data
        students = Student.loadFromCSVLines(readCSV(STUDENT_CSV));
        companyReps = CompanyRepresentative.loadFromCSVLines(readCSV(COMPANY_CSV));
        careerStaff = CareerCenterStaff.loadFromCSVLines(readCSV(CAREER_CSV));

        System.out.println("System initialized with " + students.size() + " students and " + companyReps.size() + " company representatives.");

        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> {
                    User loggedIn = login(sc);
                    if (loggedIn != null) {
                        if (loggedIn instanceof Student s) studentMenu(sc, s);
                        else if (loggedIn instanceof CompanyRepresentative rep) companyRepMenu(sc, rep);
                        else if (loggedIn instanceof CareerCenterStaff staff) careerStaffMenu(sc, staff);
                    }
                }
                case "2" -> {
                    System.out.println("Exiting system... Goodbye!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    //Login
    private static User login(Scanner sc) {
        System.out.print("Enter ID/Email: ");
        String id = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String pw = sc.nextLine().trim();

        for (Student s : students) {
            if (s.getUserID().equals(id) && s.getPassword().equals(pw)) {
                s.login(id, pw);
                return s;
            }
        }
        for (CompanyRepresentative rep : companyReps) {
            if ((rep.getUserID().equals(id) || rep.getEmail().equals(id)) && rep.getPassword().equals(pw)) {
                if (rep.login(id, pw)) return rep;
                return null; // account not approved
            }
        }
        for (CareerCenterStaff staff : careerStaff) {
            if (staff.getUserID().equals(id) && staff.getPassword().equals(pw)) {
                staff.login(id, pw);
                return staff;
            }
}

        System.out.println("Login failed. Invalid ID or password.");
        return null;
    }

    // === STUDENT MENU ===
    private static void studentMenu(Scanner sc, Student s) {
        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. View Profile");
            System.out.println("2. Toggle Profile Visibility");
            System.out.println("3. View All Available Internships");
            System.out.println("4. Apply for Internship");
            System.out.println("5. View My Applications");
            System.out.println("6. Accept Placement");
            System.out.println("7. Change Password");
            System.out.println("8. Logout");
            System.out.print("Choose option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> System.out.println(s);
                case "2" -> s.toggleVisibility();
                case "3" -> viewAllInternships();
                case "4" -> applyInternship(sc, s);
                case "5" -> s.viewApplications();
                case "6" -> acceptPlacement(sc, s);
                case "7" -> changePassword(sc, s);
                case "8" -> {
                    s.logout();
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // === COMPANY REPRESENTATIVE MENU ===
    private static void companyRepMenu(Scanner sc, CompanyRepresentative rep) {
        while (true) {
            System.out.println("\n--- Company Representative Menu ---");
            System.out.println("1. View Profile");
            System.out.println("2. Create Internship");
            System.out.println("3. View My Internships");
            System.out.println("4. Toggle Internship Visibility");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> System.out.println(rep);
                case "2" -> createInternship(sc, rep);
                case "3" -> rep.viewInternships();
                case "4" -> toggleInternshipVisibility(sc, rep);
                case "5" -> changePassword(sc, rep);
                case "6" -> {
                    rep.logout();
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
                }
            }
        }
        // === CAREER CENTER STAFF MENU ===
    private static void careerStaffMenu(Scanner sc, CareerCenterStaff staff) {
        while (true) {
            System.out.println("\n--- Career Center Staff Menu ---");
            System.out.println("1. View Pending Company Reps");
            System.out.println("2. Review Company Rep");
            System.out.println("3. Review Internship");
            System.out.println("4. Review Withdrawal Request");
            System.out.println("5. Generate Internship Report");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> viewPendingReps();
                case "2" -> reviewRep(sc, staff);
                case "3" -> reviewInternship(sc, staff);
                case "4" -> reviewWithdrawal(sc, staff);
                case "5" -> generateReport(sc, staff);
                case "6" -> {
                    staff.logout();
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // === HELPER FUNCTIONS ===
    private static void applyInternship(Scanner sc, Student s) {
        if (allInternships.isEmpty()) {
            System.out.println("No available internships.");
            return;
        }

        System.out.println("\nAvailable Internships:");
        for (int i = 0; i < allInternships.size(); i++) {
            Internship in = allInternships.get(i);
            if (in.isVisible()) {
                System.out.printf("%d. %s (%s - %s)\n", i + 1, in.getTitle(), in.getCompanyName(), in.getLevel());
            }
        }
        System.out.print("Enter internship number to apply: ");
        try {
            int choice = Integer.parseInt(sc.nextLine()) - 1;
            if (choice >= 0 && choice < allInternships.size()) {
                Internship selected = allInternships.get(choice);
                s.applyForInternship(selected);
            } else System.out.println("Invalid selection.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void acceptPlacement(Scanner sc, Student s) {
        if (s.getApplications().isEmpty()) {
            System.out.println("No applications to accept.");
            return;
        }

        s.viewApplications();
        System.out.print("Enter internship title to accept: ");
        String title = sc.nextLine().trim();

        for (Internship i : s.getApplications()) {
            if (i.getTitle().equalsIgnoreCase(title)) {
                s.acceptPlacement(i);
                return;
            }
        }
        System.out.println("No matching internship found.");
    }

    private static void createInternship(Scanner sc, CompanyRepresentative rep) {
        System.out.print("Enter title: ");
        String title = sc.nextLine();
        System.out.print("Enter description: ");
        String desc = sc.nextLine();
        System.out.print("Enter level (Basic/Advanced): ");
        String level = sc.nextLine();
        System.out.print("Enter preferred major: ");
        String major = sc.nextLine();
        System.out.print("Enter opening date: ");
        String open = sc.nextLine();
        System.out.print("Enter closing date: ");
        String close = sc.nextLine();
        System.out.print("Enter number of slots (1-10): ");
        int slots = Integer.parseInt(sc.nextLine());

        Internship newIntern = rep.createInternship(title, desc, level, major, open, close, slots);
        if (newIntern != null) {
            allInternships.add(newIntern);
        }
    }

    private static void toggleInternshipVisibility(Scanner sc, CompanyRepresentative rep) {
        if (rep.getInternships().isEmpty()) {
            System.out.println("You have no internships.");
            return;
        }

        rep.viewInternships();
        System.out.print("Enter internship title to toggle visibility: ");
        String title = sc.nextLine();

        for (Internship i : rep.getInternships()) {
            if (i.getTitle().equalsIgnoreCase(title)) {
                rep.toggleInternshipVisibility(i);
                return;
            }
        }
        System.out.println("Internship not found.");
    }

    private static void viewAllInternships() {
        if (allInternships.isEmpty()) {
            System.out.println("No internships currently listed.");
            return;
        }

        System.out.println("\n=== All Available Internships ===");
        for (Internship i : allInternships) {
            if (i.isVisible()) {
                System.out.printf("%s (%s) - Level: %s | Slots: %d/%d\n",
                        i.getTitle(), i.getCompanyName(), i.getLevel(),
                        i.getConfirmedSlots(), i.getTotalSlots());
            }
        }
    }

    private static void changePassword(Scanner sc, User user) {
        System.out.print("Enter old password: ");
        String oldPw = sc.nextLine();
        System.out.print("Enter new password: ");
        String newPw = sc.nextLine();
        user.changePassword(oldPw, newPw);
    }

    private static List<String> readCSV(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename + " (" + e.getMessage() + ")");
        }
        return lines;
    }
        private static void viewPendingReps() {
        for (CompanyRepresentative rep : companyReps) {
            if (rep.getStatus().equalsIgnoreCase("Pending")) {
                System.out.println(rep);
            }
        }
    }

    private static void reviewRep(Scanner sc, CareerCenterStaff staff) {
        System.out.print("Enter Company Rep ID: ");
        String id = sc.nextLine();
        for (CompanyRepresentative rep : companyReps) {
            if (rep.getUserID().equals(id)) {
                System.out.print("Approve? (y/n): ");
                boolean approve = sc.nextLine().equalsIgnoreCase("y");
                staff.reviewCompanyRep(rep, approve);
                return;
            }
        }
        System.out.println("Rep not found.");
    }

    private static void reviewInternship(Scanner sc, CareerCenterStaff staff) {
        viewAllInternships();
        System.out.print("Enter internship title: ");
        String title = sc.nextLine();
        for (Internship i : allInternships) {
            if (i.getTitle().equalsIgnoreCase(title)) {
                System.out.print("Approve? (y/n): ");
                boolean approve = sc.nextLine().equalsIgnoreCase("y");
                staff.reviewInternship(i, approve);
                return;
            }
        }
        System.out.println("Internship not found.");
    }

    private static void reviewWithdrawal(Scanner sc, CareerCenterStaff staff) {
        // Simplified: loop through all students and their withdrawal requests
        for (Student s : students) {
            for (Internship i : s.getApplications()) {
                if (s.hasRequestedWithdrawal(i)) {
                    System.out.println(s.getName() + " requested withdrawal from " + i.getTitle());
                    System.out.print("Approve? (y/n): ");
                    boolean approve = sc.nextLine().equalsIgnoreCase("y");
                    staff.reviewWithdrawal(s, i, approve);
                    return;
                }
            }
        }
        System.out.println("No pending withdrawal requests.");
    }

    private static void generateReport(Scanner sc, CareerCenterStaff staff) {
        System.out.print("Filter by (approved/pending/rejected/filled/all): ");
        String filter = sc.nextLine();
        staff.generateReport(allInternships, filter);
    }
}
