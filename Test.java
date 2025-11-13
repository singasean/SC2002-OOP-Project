import java.io.*;
import java.util.*;

public class Test {
    private static final String STUDENT_CSV = "sample_student_list.csv";
    private static final String COMPANY_CSV = "sample_company_representative_list.csv";
    private static final String CAREER_CSV = "sample_staff_list.csv";
    private static final String DEFAULT_PASSWORD = "password";

    private static List<Student> students = new ArrayList<>();
    private static List<CompanyRepresentative> companyReps = new ArrayList<>();
    private static List<Internship> allInternships = new ArrayList<>();
    private static List<CareerCenterStaff> careerStaff = new ArrayList<>();
    private static int nextCompanyRepID = 1;

    // Filter settings (saved across menu pages)
    private static String filterStatus = "all";
    private static String filterMajor = "all";
    private static String filterLevel = "all";
    private static String filterClosingDate = "all";
    private static String sortBy = "alphabetical";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Internship Placement Management System ===");

        // Load data
        students = Student.loadFromCSVLines(readCSV(STUDENT_CSV));
        companyReps = CompanyRepresentative.loadFromCSVLines(readCSV(COMPANY_CSV));
        careerStaff = CareerCenterStaff.loadFromCSVLines(readCSV(CAREER_CSV));

        // Initialize next Company Rep ID based on existing reps
        updateNextCompanyRepID();

        System.out.println("System initialized with " + students.size() + " students and " + companyReps.size() + " company representatives.");

        while (true) {
            // Step 1: Select Role
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("--- Select Your Role ---");
            System.out.println("1. Student");
            System.out.println("2. Company Representative");
            System.out.println("3. Career Center Staff");
            System.out.println("4. Exit System");
            System.out.print("Enter choice: ");
            String roleChoice = sc.nextLine().trim();

            if (roleChoice.equals("4")) {
                System.out.println("Exiting system... Goodbye!");
                sc.close();
                return;
            }

            // Step 2: Login or Register/Back menu
            if (roleChoice.equals("2")) {
                // Company Rep specific menu
                System.out.println("\n1. Login");
                System.out.println("2. Register");
                System.out.println("3. Back to Main Menu");
                System.out.print("Enter choice: ");
                String actionChoice = sc.nextLine().trim();

                if (actionChoice.equals("3")) {
                    continue;
                } else if (actionChoice.equals("2")) {
                    registerCompanyRep(sc);
                    continue;
                } else if (actionChoice.equals("1")) {
                    User loggedIn = loginAsCompanyRep(sc);
                    if (loggedIn != null && loggedIn instanceof CompanyRepresentative rep) {
                        companyRepMenu(sc, rep);
                    }
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            } else if (roleChoice.equals("1") || roleChoice.equals("3")) {
                // Student and Staff menu
                System.out.println("\n1. Login");
                System.out.println("2. Back to Main Menu");
                System.out.print("Enter choice: ");
                String actionChoice = sc.nextLine().trim();

                if (actionChoice.equals("2")) {
                    continue;
                }

                if (actionChoice.equals("1")) {
                    User loggedIn = null;

                    switch (roleChoice) {
                        case "1" -> loggedIn = loginAsStudent(sc);
                        case "3" -> loggedIn = loginAsStaff(sc);
                        default -> {
                            System.out.println("Invalid role selection. Please try again.");
                            continue;
                        }
                    }

                    if (loggedIn != null) {
                        if (loggedIn instanceof Student s) studentMenu(sc, s);
                        else if (loggedIn instanceof CareerCenterStaff staff) careerStaffMenu(sc, staff);
                    }
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            } else {
                System.out.println("Invalid choice. Please select 1-4.");
            }
        }
    }

    // Update next Company Rep ID based on existing records
    private static void updateNextCompanyRepID() {
        int maxID = 0;
        for (CompanyRepresentative rep : companyReps) {
            try {
                int id = Integer.parseInt(rep.getUserID());
                if (id > maxID) maxID = id;
            } catch (NumberFormatException e) {
                // Skip non-numeric IDs
            }
        }
        nextCompanyRepID = maxID + 1;
    }

    // Register new Company Representative with validation
    private static void registerCompanyRep(Scanner sc) {
        System.out.println("\n--- Company Representative Registration ---");

        // Get name
        String name;
        while (true) {
            System.out.print("Enter your name: ");
            name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                break;
            }
            System.out.println("Error: Name cannot be empty. Please try again.");
        }

        // Get company name
        String companyName;
        while (true) {
            System.out.print("Enter company name: ");
            companyName = sc.nextLine().trim();
            if (!companyName.isEmpty()) {
                break;
            }
            System.out.println("Error: Company name cannot be empty. Please try again.");
        }

        // Get department
        String department;
        while (true) {
            System.out.print("Enter department: ");
            department = sc.nextLine().trim();
            if (!department.isEmpty()) {
                break;
            }
            System.out.println("Error: Department cannot be empty. Please try again.");
        }

        // Get position
        String position;
        while (true) {
            System.out.print("Enter position: ");
            position = sc.nextLine().trim();
            if (!position.isEmpty()) {
                break;
            }
            System.out.println("Error: Position cannot be empty. Please try again.");
        }

        // Get email with validation
        String email;
        while (true) {
            System.out.print("Enter email: ");
            email = sc.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("Error: Email cannot be empty. Please try again.");
                continue;
            }

            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Error: Please enter a valid email address.");
                continue;
            }

            // Check if email already exists
            boolean emailExists = false;
            for (CompanyRepresentative rep : companyReps) {
                if (rep.getEmail().equalsIgnoreCase(email)) {
                    System.out.println("Error: This email is already registered. Please use a different email.");
                    emailExists = true;
                    break;
                }
            }

            if (!emailExists) {
                break;
            }
        }

        // Generate company rep ID
        String repID = String.valueOf(nextCompanyRepID);
        nextCompanyRepID++;

        // Create new company rep with default password
        CompanyRepresentative newRep = new CompanyRepresentative(
                repID, name, DEFAULT_PASSWORD, companyName, department, position, email
        );

        companyReps.add(newRep);
        saveCompanyRepsToCSV();

        System.out.println("\n=== Registration Successful! ===");
        System.out.println("Your Company Representative ID: " + repID);
        System.out.println("Your Email: " + email);
        System.out.println("Default Password: " + DEFAULT_PASSWORD);
        System.out.println("\nYour account is pending approval from Career Center Staff.");
        System.out.println("You will be able to login once approved.");
    }

    // Save company reps to CSV
    private static void saveCompanyRepsToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMPANY_CSV))) {
            // Write header
            bw.write("CompanyRepID,Name,CompanyName,Department,Position,Email,Status");
            bw.newLine();

            // Write data
            for (CompanyRepresentative rep : companyReps) {
                bw.write(String.format("%s,%s,%s,%s,%s,%s,%s",
                        rep.getUserID(),
                        rep.getName(),
                        rep.getCompanyName(),
                        rep.getDepartment(),
                        rep.getPosition(),
                        rep.getEmail(),
                        rep.getStatus()
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving company representative data: " + e.getMessage());
        }
    }

    // Login as Student
    private static User loginAsStudent(Scanner sc) {
        System.out.println("\n--- Student Login ---");
        System.out.print("Enter Student ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String pw = sc.nextLine().trim();

        for (Student s : students) {
            if (s.getUserID().equals(id) && s.getPassword().equals(pw)) {
                s.login(id, pw);
                return s;
            }
        }
        System.out.println("Login failed. Invalid Student ID or password.");
        return null;
    }

    // Login as Company Representative (using EMAIL)
    private static User loginAsCompanyRep(Scanner sc) {
        System.out.println("\n--- Company Representative Login ---");
        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String pw = sc.nextLine().trim();

        for (CompanyRepresentative rep : companyReps) {
            if (rep.getEmail().equalsIgnoreCase(email) && rep.getPassword().equals(pw)) {
                // Check status first
                if (!rep.getStatus().equalsIgnoreCase("Approved")) {
                    System.out.println("Your account is pending approval. Please contact the Career Center.");
                    return null;
                }

                // Now login using userID (not email)
                if (rep.login(rep.getUserID(), pw)) {
                    return rep;
                }
            }
        }
        System.out.println("Login failed. Invalid email or password.");
        return null;
    }

    // Login as Career Center Staff
    private static User loginAsStaff(Scanner sc) {
        System.out.println("\n--- Career Center Staff Login ---");
        System.out.print("Enter Staff ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String pw = sc.nextLine().trim();

        for (CareerCenterStaff staff : careerStaff) {
            if (staff.getUserID().equals(id) && staff.getPassword().equals(pw)) {
                staff.login(id, pw);
                return staff;
            }
        }
        System.out.println("Login failed. Invalid Staff ID or password.");
        return null;
    }

    // === STUDENT MENU ===
    private static void studentMenu(Scanner sc, Student s) {
        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. View Profile");
            System.out.println("2. Toggle Profile Visibility");
            System.out.println("3. View All Available Internships");
            System.out.println("4. Set Filters & Sort");
            System.out.println("5. Apply for Internship");
            System.out.println("6. View My Applications");
            System.out.println("7. Accept Placement");
            System.out.println("8. Request Withdrawal");         // ADD THIS
            System.out.println("9. Change Password");
            System.out.println("10. Logout");                    // Change from 9
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> System.out.println(s);
                case "2" -> s.toggleVisibility();
                case "3" -> viewAllInternships();
                case "4" -> setFiltersMenu(sc);
                case "5" -> applyInternship(sc, s);
                case "6" -> s.viewApplications();
                case "7" -> acceptPlacement(sc, s);
                case "8" -> requestWithdrawal(sc, s);
                case "9" -> changePassword(sc, s);
                case "10" -> {
                    s.logout();
                    return;
                }
                default -> System.out.println("Invalid choice. Please select 1-10.");
            }
        }
    }

    private static void requestWithdrawal(Scanner sc, Student s) {
        if (s.getApplications().isEmpty()) {
            System.out.println("No applications to withdraw from.");
            return;
        }

        System.out.println("\n=== Your Applications ===");
        s.viewApplications();

        System.out.print("\nEnter internship title to request withdrawal (or 'cancel' to go back): ");
        String title = sc.nextLine().trim();

        if (title.equalsIgnoreCase("cancel")) {
            return;
        }

        for (Internship i : s.getApplications()) {
            if (i.getTitle().equalsIgnoreCase(title)) {
                s.requestWithdrawal(i);
                return;
            }
        }
        System.out.println("Error: No matching internship found.");
    }

    // === COMPANY REPRESENTATIVE MENU ===
    private static void companyRepMenu(Scanner sc, CompanyRepresentative rep) {
        while (true) {
            System.out.println("\n--- Company Representative Menu ---");
            System.out.println("1. View Profile");
            System.out.println("2. Create Internship");
            System.out.println("3. View Internships");
            System.out.println("4. View Student Applications");
            System.out.println("5. Review Student Application");
            System.out.println("6. Set Filters & Sort");
            System.out.println("7. Toggle Internship Visibility");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> System.out.println(rep);
                case "2" -> createInternship(sc, rep);
                case "3" -> viewCompanyRepInternships(rep);
                case "4" -> viewStudentApplications(rep);
                case "5" -> reviewStudentApplication(sc, rep);
                case "6" -> setFiltersMenu(sc);
                case "7" -> toggleInternshipVisibility(sc, rep);
                case "8" -> changePassword(sc, rep);
                case "9" -> {
                    rep.logout();
                    return;
                }
                default -> System.out.println("Invalid choice. Please select 1-9.");
            }
        }
    }

    // === CAREER CENTER STAFF MENU ===
    private static void careerStaffMenu(Scanner sc, CareerCenterStaff staff) {
        while (true) {
            System.out.println("\n--- Career Center Staff Menu ---");
            System.out.println("1. View Pending Company Reps");
            System.out.println("2. Review Company Rep");
            System.out.println("3. View All Internships");
            System.out.println("4. Set Filters & Sort");
            System.out.println("5. Review Internship");
            System.out.println("6. Review Withdrawal Request");
            System.out.println("7. Generate Internship Report");
            System.out.println("8. Change Password");                    // ADDED
            System.out.println("9. Logout");                            // Changed from 8 to 9
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> viewPendingReps();
                case "2" -> reviewRep(sc, staff);
                case "3" -> viewAllInternshipsForStaff();
                case "4" -> setFiltersMenu(sc);
                case "5" -> reviewInternship(sc, staff);
                case "6" -> reviewWithdrawal(sc, staff);
                case "7" -> generateReport(sc, staff);
                case "8" -> changePassword(sc, staff);                   // ADDED
                case "9" -> {                                            // Changed from 8 to 9
                    staff.logout();
                    return;
                }
                default -> System.out.println("Invalid choice. Please select 1-9.");  // Changed from 1-8 to 1-9
            }
        }
    }

    // === FILTER & SORT MENU ===
    private static void setFiltersMenu(Scanner sc) {
        while (true) {
            System.out.println("\n=== Filter & Sort Settings ===");
            System.out.println("Current Settings:");
            System.out.println("  1. Status: " + filterStatus);
            System.out.println("  2. Major: " + filterMajor);
            System.out.println("  3. Level: " + filterLevel);
            System.out.println("  4. Closing Date: " + filterClosingDate);
            System.out.println("  5. Sort By: " + sortBy);
            System.out.println("  6. Reset All Filters");
            System.out.println("  7. Back to Menu");
            System.out.print("Choose option to modify: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter status (all/approved/pending/rejected/filled): ");
                    String status = sc.nextLine().trim().toLowerCase();
                    if (status.equals("all") || status.equals("approved") || status.equals("pending") ||
                            status.equals("rejected") || status.equals("filled")) {
                        filterStatus = status;
                        System.out.println("Status filter updated to: " + filterStatus);
                    } else {
                        System.out.println("Invalid status. Filter not changed.");
                    }
                }
                case "2" -> {
                    System.out.print("Enter major (all/Computer Science/Engineering/etc.): ");
                    filterMajor = sc.nextLine().trim();
                    System.out.println("Major filter updated to: " + filterMajor);
                }
                case "3" -> {
                    System.out.print("Enter level (all/Basic/Intermediate/Advanced): ");
                    String level = sc.nextLine().trim();
                    if (level.equalsIgnoreCase("all") || level.equalsIgnoreCase("Basic") ||
                            level.equalsIgnoreCase("Intermediate") || level.equalsIgnoreCase("Advanced")) {
                        filterLevel = level;
                        System.out.println("Level filter updated to: " + filterLevel);
                    } else {
                        System.out.println("Invalid level. Filter not changed.");
                    }
                }

                case "4" -> {
                    System.out.print("Enter closing date (all/DD/MM/YY): ");
                    filterClosingDate = sc.nextLine().trim();
                    System.out.println("Closing date filter updated to: " + filterClosingDate);
                }
                case "5" -> {
                    System.out.print("Sort by (alphabetical/company/level): ");
                    String sort = sc.nextLine().trim().toLowerCase();
                    if (sort.equals("alphabetical") || sort.equals("company") || sort.equals("level")) {
                        sortBy = sort;
                        System.out.println("Sort option updated to: " + sortBy);
                    } else {
                        System.out.println("Invalid sort option. Sort not changed.");
                    }
                }
                case "6" -> {
                    filterStatus = "all";
                    filterMajor = "all";
                    filterLevel = "all";
                    filterClosingDate = "all";
                    sortBy = "alphabetical";
                    System.out.println("All filters reset to default!");
                }
                case "7" -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Please select 1-7.");
            }
        }
    }

    // === HELPER FUNCTIONS ===

    // View all internships (for Students - only approved)
    private static void viewAllInternships() {
        if (allInternships.isEmpty()) {
            System.out.println("No internships currently listed.");
            return;
        }

        List<Internship> filteredInternships = filterAndSortInternships();

        if (filteredInternships.isEmpty()) {
            System.out.println("No internships match your current filters.");
            return;
        }

        System.out.println("\n=== All Available Internships ===");
        System.out.println("Current Filters: Status=" + filterStatus +
                ", Major=" + filterMajor +
                ", Level=" + filterLevel +
                ", Closing Date=" + filterClosingDate);
        System.out.println("Sorted By: " + sortBy);
        System.out.println("=".repeat(90));

        boolean hasApproved = false;
        for (Internship i : filteredInternships) {
            if (i.isVisible() && i.getStatus().equalsIgnoreCase("Approved")) {
                System.out.printf("%-30s | %-20s | Level: %-8s | Filled: %d/%d\n",
                        i.getTitle(), i.getCompanyName(), i.getLevel(),
                        i.getConfirmedSlots(), i.getTotalSlots());
                hasApproved = true;
            }
        }

        if (!hasApproved) {
            System.out.println("No approved internships available at the moment.");
        }
        System.out.println("=".repeat(90));
    }

    // View all internships (for Staff - all statuses)
    private static void viewAllInternshipsForStaff() {
        if (allInternships.isEmpty()) {
            System.out.println("No internships currently listed.");
            return;
        }

        List<Internship> filteredInternships = filterAndSortInternshipsForStaff();

        if (filteredInternships.isEmpty()) {
            System.out.println("No internships match your current filters.");
            return;
        }

        System.out.println("\n=== All Internships ===");
        System.out.println("Current Filters: Status=" + filterStatus +
                ", Major=" + filterMajor +
                ", Level=" + filterLevel +
                ", Closing Date=" + filterClosingDate);
        System.out.println("Sorted By: " + sortBy);
        System.out.println("=".repeat(90));

        for (Internship i : filteredInternships) {
            System.out.printf("%-30s | %-20s | Level: %-8s | Status: %-10s | Filled: %d/%d\n",
                    i.getTitle(), i.getCompanyName(), i.getLevel(), i.getStatus(),
                    i.getConfirmedSlots(), i.getTotalSlots());
        }
        System.out.println("=".repeat(90));
    }

    // Filter and sort internships for students
    private static List<Internship> filterAndSortInternships() {
        List<Internship> filtered = new ArrayList<>();

        for (Internship i : allInternships) {
            if (!i.isVisible()) continue;

            if (filterStatus.equals("all")) {
                if (!i.getStatus().equalsIgnoreCase("Approved")) {
                    continue;
                }
            } else {
                if (!i.getStatus().equalsIgnoreCase(filterStatus)) {
                    continue;
                }
            }

            if (!filterMajor.equals("all") && !i.getPreferredMajor().equalsIgnoreCase(filterMajor)) {
                continue;
            }

            if (!filterLevel.equals("all") && !i.getLevel().equalsIgnoreCase(filterLevel)) {
                continue;
            }

            if (!filterClosingDate.equals("all") && !i.getClosingDate().equals(filterClosingDate)) {
                continue;
            }

            filtered.add(i);
        }

        applySorting(filtered);
        return filtered;
    }

    // Filter and sort internships for staff
    private static List<Internship> filterAndSortInternshipsForStaff() {
        List<Internship> filtered = new ArrayList<>();

        for (Internship i : allInternships) {
            if (!filterStatus.equals("all") && !i.getStatus().equalsIgnoreCase(filterStatus)) {
                continue;
            }

            if (!filterMajor.equals("all") && !i.getPreferredMajor().equalsIgnoreCase(filterMajor)) {
                continue;
            }

            if (!filterLevel.equals("all") && !i.getLevel().equalsIgnoreCase(filterLevel)) {
                continue;
            }

            if (!filterClosingDate.equals("all") && !i.getClosingDate().equals(filterClosingDate)) {
                continue;
            }

            filtered.add(i);
        }

        applySorting(filtered);
        return filtered;
    }

    // Apply sorting
    private static void applySorting(List<Internship> internships) {
        if (sortBy.equals("alphabetical")) {
            internships.sort(Comparator.comparing(Internship::getTitle));
        } else if (sortBy.equals("company")) {
            internships.sort(Comparator.comparing(Internship::getCompanyName));
        } else if (sortBy.equals("level")) {
            internships.sort(Comparator.comparing(Internship::getLevel));
        }
    }

    // View Company Rep's own internships with filters
    private static void viewCompanyRepInternships(CompanyRepresentative rep) {
        if (rep.getInternships().isEmpty()) {
            System.out.println("No internship opportunities created yet.");
            return;
        }

        List<Internship> filteredInternships = filterCompanyRepInternships(rep);

        if (filteredInternships.isEmpty()) {
            System.out.println("No internships match your current filters.");
            return;
        }

        System.out.println("\n=== Your Internship Opportunities ===");
        System.out.println("Current Filters: Status=" + filterStatus +
                ", Major=" + filterMajor +
                ", Level=" + filterLevel +
                ", Closing Date=" + filterClosingDate);
        System.out.println("Sorted By: " + sortBy);
        System.out.println("=".repeat(90));

        for (int i = 0; i < filteredInternships.size(); i++) {
            Internship internship = filteredInternships.get(i);
            System.out.printf("[%d] %-30s | Level: %-10s | Visibility: %-3s | Filled: %d/%d\n",
                    (i + 1),
                    internship.getTitle(),
                    internship.getLevel(),
                    internship.isVisible() ? "ON" : "OFF",
                    internship.getConfirmedSlots(),
                    internship.getTotalSlots());
            System.out.println("    Status: " + internship.getStatus() +
                    " | Major: " + internship.getPreferredMajor() +
                    " | Closing: " + internship.getClosingDate());
            System.out.println("-".repeat(90));
        }
    }

    // Filter company rep's internships
    private static List<Internship> filterCompanyRepInternships(CompanyRepresentative rep) {
        List<Internship> filtered = new ArrayList<>();

        for (Internship i : rep.getInternships()) {
            if (!filterStatus.equals("all") && !i.getStatus().equalsIgnoreCase(filterStatus)) {
                continue;
            }

            if (!filterMajor.equals("all") && !i.getPreferredMajor().equalsIgnoreCase(filterMajor)) {
                continue;
            }

            if (!filterLevel.equals("all") && !i.getLevel().equalsIgnoreCase(filterLevel)) {
                continue;
            }

            if (!filterClosingDate.equals("all") && !i.getClosingDate().equals(filterClosingDate)) {
                continue;
            }

            filtered.add(i);
        }

        applySorting(filtered);
        return filtered;
    }

    // View student applications
    private static void viewStudentApplications(CompanyRepresentative rep) {
        if (rep.getInternships().isEmpty()) {
            System.out.println("You have no internships yet.");
            return;
        }

        System.out.println("\n=== Student Applications for Your Internships ===");
        System.out.println("=".repeat(90));

        boolean hasApplications = false;

        for (Internship internship : rep.getInternships()) {
            List<Student> applicants = getApplicantsForInternship(internship);

            if (!applicants.isEmpty()) {
                System.out.println("\nInternship: " + internship.getTitle() +
                        " | Level: " + internship.getLevel() +
                        " | Status: " + internship.getStatus() +
                        " | Filled: " + internship.getConfirmedSlots() + "/" + internship.getTotalSlots());
                System.out.println("-".repeat(90));

                for (Student student : applicants) {
                    String appStatus = internship.getStatusForStudent(student.getUserID());
                    System.out.printf("  Student ID: %-15s | Name: %-20s | Year: %d | Major: %-20s | Status: %s\n",
                            student.getUserID(),
                            student.getName(),
                            student.getYearOfStudy(),
                            student.getMajor(),
                            appStatus);
                }
                hasApplications = true;
            }
        }

        if (!hasApplications) {
            System.out.println("No student applications yet.");
        }
        System.out.println("=".repeat(90));
    }

    // Get applicants for internship
    private static List<Student> getApplicantsForInternship(Internship internship) {
        List<Student> applicants = new ArrayList<>();
        for (Student student : students) {
            if (student.getApplications().contains(internship)) {
                applicants.add(student);
            }
        }
        return applicants;
    }

    // Review student application with validation
    private static void reviewStudentApplication(Scanner sc, CompanyRepresentative rep) {
        if (rep.getInternships().isEmpty()) {
            System.out.println("You have no internships yet.");
            return;
        }

        viewStudentApplications(rep);

        if (getApplicantsForInternship(rep.getInternships().get(0)).isEmpty()) {
            return; // No applications to review
        }

        System.out.print("\nEnter internship title (or 'cancel' to go back): ");
        String internshipTitle = sc.nextLine().trim();

        if (internshipTitle.equalsIgnoreCase("cancel")) {
            return;
        }

        Internship selectedInternship = null;
        for (Internship i : rep.getInternships()) {
            if (i.getTitle().equalsIgnoreCase(internshipTitle)) {
                selectedInternship = i;
                break;
            }
        }

        if (selectedInternship == null) {
            System.out.println("Error: Internship not found. Please check the title and try again.");
            return;
        }

        List<Student> applicants = getApplicantsForInternship(selectedInternship);

        if (applicants.isEmpty()) {
            System.out.println("No applications for this internship.");
            return;
        }

        System.out.print("Enter Student ID to review (or 'cancel' to go back): ");
        String studentID = sc.nextLine().trim();

        if (studentID.equalsIgnoreCase("cancel")) {
            return;
        }

        Student selectedStudent = null;
        for (Student s : applicants) {
            if (s.getUserID().equals(studentID)) {
                selectedStudent = s;
                break;
            }
        }

        if (selectedStudent == null) {
            System.out.println("Error: Student application not found. Please check the Student ID and try again.");
            return;
        }

        String currentStatus = selectedInternship.getStatusForStudent(studentID);
        if (!currentStatus.equalsIgnoreCase("Pending")) {
            System.out.println("This application has already been processed. Current status: " + currentStatus);
            return;
        }

        System.out.println("\n=== Student Details ===");
        System.out.println("ID: " + selectedStudent.getUserID());
        System.out.println("Name: " + selectedStudent.getName());
        System.out.println("Year: " + selectedStudent.getYearOfStudy());
        System.out.println("Major: " + selectedStudent.getMajor());
        System.out.println("Current Application Status: " + currentStatus);

        System.out.print("\nApprove this application? (y/n): ");
        String decision = sc.nextLine().trim().toLowerCase();

        if (decision.equals("y")) {
            if (rep.approveApplication(selectedInternship, studentID)) {
                System.out.println("✓ Application APPROVED for " + selectedStudent.getName());
                System.out.println("Student can now accept the placement.");
            }
        } else if (decision.equals("n")) {
            if (rep.rejectApplication(selectedInternship, studentID)) {
                System.out.println("✗ Application REJECTED for " + selectedStudent.getName());
            }
        } else {
            System.out.println("Invalid choice. No action taken.");
        }
    }

    // Apply for internship with improved error handling
    private static void applyInternship(Scanner sc, Student s) {
        if (allInternships.isEmpty()) {
            System.out.println("No available internships.");
            return;
        }

        List<Internship> availableInternships = new ArrayList<>();
        for (Internship in : allInternships) {
            if (in.isVisible() && in.getStatus().equalsIgnoreCase("Approved")) {
                availableInternships.add(in);
            }
        }

        if (availableInternships.isEmpty()) {
            System.out.println("No approved internships available.");
            return;
        }

        System.out.println("\n=== Available Internships ===");
        for (int i = 0; i < availableInternships.size(); i++) {
            Internship in = availableInternships.get(i);
            System.out.printf("%d. %s (%s - %s)\n", i + 1, in.getTitle(), in.getCompanyName(), in.getLevel());
        }

        System.out.print("Enter internship number to apply (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim()) - 1;

            if (choice == -1) {
                return; // User cancelled
            }

            if (choice >= 0 && choice < availableInternships.size()) {
                Internship selected = availableInternships.get(choice);
                s.applyForInternship(selected);
            } else {
                System.out.println("Error: Invalid selection. Please choose a number from the list.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number.");
        }
    }

    // Accept placement with validation
    private static void acceptPlacement(Scanner sc, Student s) {
        if (s.getApplications().isEmpty()) {
            System.out.println("No applications to accept.");
            return;
        }

        System.out.println("\n=== Applications You Can Accept ===");
        List<Internship> successfulApps = new ArrayList<>();

        for (Internship i : s.getApplications()) {
            String status = i.getStatusForStudent(s.getUserID());
            if (status.equalsIgnoreCase("Successful")) {
                successfulApps.add(i);
                System.out.printf("%s | Status: %s | Filled: %d/%d\n",
                        i.getTitle(), status, i.getConfirmedSlots(), i.getTotalSlots());
            }
        }

        if (successfulApps.isEmpty()) {
            System.out.println("No approved applications to accept. Applications must be approved by the company first.");
            return;
        }

        System.out.print("\nEnter internship title to accept (or 'cancel' to go back): ");
        String title = sc.nextLine().trim();

        if (title.equalsIgnoreCase("cancel")) {
            return;
        }

        for (Internship i : successfulApps) {
            if (i.getTitle().equalsIgnoreCase(title)) {
                s.acceptPlacement(i);
                return;
            }
        }
        System.out.println("Error: No matching internship found. Please check the title and try again.");
    }

    // Create internship with comprehensive validation
    private static void createInternship(Scanner sc, CompanyRepresentative rep) {
        System.out.println("\n--- Create New Internship ---");

        // Get title
        String title;
        while (true) {
            System.out.print("Enter title: ");
            title = sc.nextLine().trim();
            if (!title.isEmpty()) {
                break;
            }
            System.out.println("Error: Title cannot be empty. Please try again.");
        }

        // Get description
        String desc;
        while (true) {
            System.out.print("Enter description: ");
            desc = sc.nextLine().trim();
            if (!desc.isEmpty()) {
                break;
            }
            System.out.println("Error: Description cannot be empty. Please try again.");
        }

// Get level with validation
        String level;
        while (true) {
            System.out.print("Enter level (Basic/Intermediate/Advanced): ");
            level = sc.nextLine().trim();
            if (level.equalsIgnoreCase("Basic") || level.equalsIgnoreCase("Intermediate") ||
                    level.equalsIgnoreCase("Advanced")) {
                break;
            }
            System.out.println("Error: Level must be 'Basic', 'Intermediate', or 'Advanced'. Please try again.");
        }


        // Get preferred major
        String major;
        while (true) {
            System.out.print("Enter preferred major: ");
            major = sc.nextLine().trim();
            if (!major.isEmpty()) {
                break;
            }
            System.out.println("Error: Preferred major cannot be empty. Please try again.");
        }

        // Get opening date with validation
        String openingDate;
        while (true) {
            System.out.print("Enter opening date (DD/MM/YY): ");
            openingDate = sc.nextLine().trim();
            if (isValidDate(openingDate)) {
                break;
            }
            System.out.println("Error: Invalid date format. Please use DD/MM/YY format (e.g., 14/11/25).");
        }

        // Get closing date with validation
        String closingDate;
        while (true) {
            System.out.print("Enter closing date (DD/MM/YY): ");
            closingDate = sc.nextLine().trim();
            if (!isValidDate(closingDate)) {
                System.out.println("Error: Invalid date format. Please use DD/MM/YY format (e.g., 15/11/25).");
                continue;
            }
            if (!isClosingAfterOpening(openingDate, closingDate)) {
                System.out.println("Error: Closing date must be after opening date. Please try again.");
                continue;
            }
            break;
        }

        // Get number of slots with validation
        int slots;
        while (true) {
            System.out.print("Enter number of slots (1-10): ");
            try {
                slots = Integer.parseInt(sc.nextLine().trim());
                if (slots >= 1 && slots <= 10) {
                    break;
                }
                System.out.println("Error: Number of slots must be between 1 and 10. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }

        // Create the internship
        Internship newIntern = rep.createInternship(title, desc, level, major, openingDate, closingDate, slots);
        if (newIntern != null) {
            allInternships.add(newIntern);
        }
    }

    // Validate date format DD/MM/YY
    private static boolean isValidDate(String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }

        String[] parts = date.split("/");
        if (parts.length != 3) {
            return false;
        }

        try {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            if (day < 1 || day > 31) return false;
            if (month < 1 || month > 12) return false;
            if (year < 0 || year > 99) return false;

            if (month == 2 && day > 29) return false;
            if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) return false;

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check if closing date is after opening date
    private static boolean isClosingAfterOpening(String opening, String closing) {
        try {
            String[] openParts = opening.split("/");
            String[] closeParts = closing.split("/");

            int openDay = Integer.parseInt(openParts[0]);
            int openMonth = Integer.parseInt(openParts[1]);
            int openYear = Integer.parseInt(openParts[2]);

            int closeDay = Integer.parseInt(closeParts[0]);
            int closeMonth = Integer.parseInt(closeParts[1]);
            int closeYear = Integer.parseInt(closeParts[2]);

            if (closeYear > openYear) return true;
            if (closeYear < openYear) return false;

            if (closeMonth > openMonth) return true;
            if (closeMonth < openMonth) return false;

            return closeDay > openDay;

        } catch (Exception e) {
            return false;
        }
    }

    // Toggle internship visibility with validation
    private static void toggleInternshipVisibility(Scanner sc, CompanyRepresentative rep) {
        if (rep.getInternships().isEmpty()) {
            System.out.println("You have no internships.");
            return;
        }

        rep.viewInternships();
        System.out.print("\nEnter internship title to toggle visibility (or 'cancel' to go back): ");
        String title = sc.nextLine().trim();

        if (title.equalsIgnoreCase("cancel")) {
            return;
        }

        for (Internship i : rep.getInternships()) {
            if (i.getTitle().equalsIgnoreCase(title)) {
                rep.toggleInternshipVisibility(i);
                return;
            }
        }
        System.out.println("Error: Internship not found. Please check the title and try again.");
    }

    // Change password with validation
    private static void changePassword(Scanner sc, User user) {
        System.out.print("Enter old password: ");
        String oldPw = sc.nextLine().trim();
        System.out.print("Enter new password: ");
        String newPw = sc.nextLine().trim();

        if (newPw.isEmpty()) {
            System.out.println("Error: New password cannot be empty.");
            return;
        }

        if (newPw.length() < 4) {
            System.out.println("Error: Password must be at least 4 characters long.");
            return;
        }

        user.changePassword(oldPw, newPw);
    }

    // Read CSV file
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

    // View pending company reps
    private static void viewPendingReps() {
        System.out.println("\n=== Pending Company Representatives ===");
        System.out.println("=".repeat(90));

        boolean hasPending = false;
        int count = 1;

        for (CompanyRepresentative rep : companyReps) {
            if (rep.getStatus().equalsIgnoreCase("Pending")) {
                System.out.println("\n[" + count + "] ID: " + rep.getUserID());
                System.out.println("    Name:         " + rep.getName());
                System.out.println("    Company:      " + rep.getCompanyName());
                System.out.println("    Department:   " + rep.getDepartment());
                System.out.println("    Position:     " + rep.getPosition());
                System.out.println("    Email:        " + rep.getEmail());
                System.out.println("    Status:       " + rep.getStatus());
                System.out.println("    Internships:  " + rep.getInternships().size());
                System.out.println("-".repeat(90));
                hasPending = true;
                count++;
            }
        }

        if (!hasPending) {
            System.out.println("No pending company representatives.");
            System.out.println("=".repeat(90));
        }
    }

    // Review company rep with validation
    private static void reviewRep(Scanner sc, CareerCenterStaff staff) {
        System.out.print("Enter Company Rep ID (or 'cancel' to go back): ");
        String id = sc.nextLine().trim();

        if (id.equalsIgnoreCase("cancel")) {
            return;
        }

        for (CompanyRepresentative rep : companyReps) {
            if (rep.getUserID().equals(id)) {
                System.out.print("Approve? (y/n): ");
                boolean approve = sc.nextLine().equalsIgnoreCase("y");
                staff.reviewCompanyRep(rep, approve);
                saveCompanyRepsToCSV();
                return;
            }
        }
        System.out.println("Error: Company Rep not found. Please check the ID and try again.");
    }

    // Review internship with validation
    private static void reviewInternship(Scanner sc, CareerCenterStaff staff) {
        viewAllInternshipsForStaff();

        if (allInternships.isEmpty()) {
            return;
        }

        System.out.print("\nEnter internship title (or 'cancel' to go back): ");
        String title = sc.nextLine().trim();

        if (title.equalsIgnoreCase("cancel")) {
            return;
        }

        for (Internship i : allInternships) {
            if (i.getTitle().equalsIgnoreCase(title)) {
                System.out.print("Approve? (y/n): ");
                boolean approve = sc.nextLine().equalsIgnoreCase("y");
                staff.reviewInternship(i, approve);
                return;
            }
        }
        System.out.println("Error: Internship not found. Please check the title and try again.");
    }

    // Review withdrawal
    private static void reviewWithdrawal(Scanner sc, CareerCenterStaff staff) {
        boolean foundWithdrawal = false;

        for (Student s : students) {
            for (Internship i : s.getApplications()) {
                if (s.hasRequestedWithdrawal(i)) {
                    System.out.println("\n" + s.getName() + " requested withdrawal from " + i.getTitle());
                    System.out.print("Approve? (y/n): ");
                    boolean approve = sc.nextLine().equalsIgnoreCase("y");
                    staff.reviewWithdrawal(s, i, approve);
                    foundWithdrawal = true;
                    return;
                }
            }
        }

        if (!foundWithdrawal) {
            System.out.println("No pending withdrawal requests.");
        }
    }

    // Generate report
    private static void generateReport(Scanner sc, CareerCenterStaff staff) {
        System.out.print("Filter by (approved/pending/rejected/filled/all): ");
        String filter = sc.nextLine().trim().toLowerCase();

        if (!filter.equals("approved") && !filter.equals("pending") &&
                !filter.equals("rejected") && !filter.equals("filled") && !filter.equals("all")) {
            System.out.println("Invalid filter. Using 'all' instead.");
            filter = "all";
        }

        staff.generateReport(allInternships, filter);
    }
}
