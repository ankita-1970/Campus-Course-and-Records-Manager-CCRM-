

import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors; // For Stream API [cite: 23, 92]

// ====================================================================
// SECTION 1: DOMAIN & CONFIG (Should be in domain/ and config/ packages)
// ====================================================================

// 1.1 Singleton Pattern for AppConfig [cite: 80, 111]
final class AppConfig {
    private static volatile AppConfig instance; 
    private final String dataFolderPath;

    private AppConfig() {
        // Loads config (demonstrates initialization logic)
        this.dataFolderPath = System.getProperty("user.dir") + "/ccrm_data";
        System.out.println("Config loaded. Data path: " + dataFolderPath);
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }
    
    // Getter for the path (fixes the "config is not used" warning)
    public String getDataFolderPath() { return dataFolderPath; }
}

// 1.2 Abstract Class (Abstraction, Inheritance) [cite: 60, 61]
abstract class Person {
    private final String id; 
    private String fullName;
    
    public Person(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    // Abstract method (Abstraction)
    public abstract String getRole(); 

    // Getters/Setters (Encapsulation) [cite: 59]
    public String getId() { return id; } 
    public String getFullName() { return fullName; }

    @Override
    public String toString() { // Overriding toString() [cite: 28, 77]
        return this.getRole() + " [ID: " + id + ", Name: " + fullName + "]";
    }
}

// 1.3 Concrete Subclass (Inheritance, Date/Time API) [cite: 60, 94]
class Student extends Person {
    final String regNo;
    private final LocalDate enrollmentDate; // Java Date/Time API [cite: 18]
    // Renamed to PRIVATE to enforce getter usage (fixes visibility error)
    private final List<String> enrolledCourseCodes = new ArrayList<>(); 
    private double currentGpa = 0.0;

    public Student(String id, String fullName, String regNo) {
        super(id, fullName); // Demonstrate super() [cite: 64]
        this.regNo = regNo;
        this.enrollmentDate = LocalDate.now();
    }
    
    // Polymorphism: Implementing abstract method [cite: 62]
    @Override
    public String getRole() { return "Student"; }

    // GETTER for enrolledCourseCodes (Fixes "not visible" error)
    public List<String> getEnrolledCourseCodes() { 
        return enrolledCourseCodes; 
    }
    
    // Getter for EnrollmentDate (Fixes "enrollmentDate is not used" warning)
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    // Method to enroll (with business logic)
    public void enroll(String courseCode) {
        // Use assertion for invariant [cite: 89]
        assert courseCode != null : "Course code cannot be null for enrollment.";
        enrolledCourseCodes.add(courseCode);
    }
    
    public double getCurrentGpa() { return currentGpa; }
    public void setCurrentGpa(double gpa) { this.currentGpa = gpa; }
}

// 1.4 Enum with fields (Grade) [cite: 27, 74]
enum Grade {
    S(10.0), A(9.0), B(8.0), C(7.0), D(6.0), F(0.0);

    private final double gradePoint;

    Grade(double gradePoint) { // Enum constructor
        this.gradePoint = gradePoint;
    }

    public double getGradePoint() {
        return gradePoint;
    }
}

// 1.5 Custom Unchecked Exception [cite: 84, 88]
class MaxCreditLimitExceededException extends RuntimeException {
    public MaxCreditLimitExceededException(String message) {
        super(message);
    }
}

// ====================================================================
// SECTION 2: SERVICE LAYER (Should be in service/ package)
// ====================================================================

class StudentService {
    // Simple HashMap data store
    private final Map<String, Student> studentStore = new HashMap<>();

    public void addStudent(Student student) {
        if (studentStore.containsKey(student.getId())) {
             throw new IllegalArgumentException("Student ID already exists."); 
        }
        studentStore.put(student.getId(), student);
        System.out.println("Student added: " + student.getFullName());
    }

    public void enrollStudentInCourse(String studentId, String courseCode, int credits) {
        Student student = studentStore.get(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }
        
        // Use the public getter for list size (FIXES the visibility error)
        if (student.getEnrolledCourseCodes().size() * 3 + credits > 15) { // Assuming 3 credits/course
             throw new MaxCreditLimitExceededException("Enrollment failed: Max credit limit exceeded (15 credits).");
        }
        
        student.enroll(courseCode); 
        System.out.println(student.getFullName() + " enrolled in " + courseCode);
    }
    
    // Demonstrate Functional Interface/Lambda and Stream API for filtering [cite: 23, 72]
    public List<Student> filterStudents(java.util.function.Predicate<Student> predicate) {
        return studentStore.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    // Placeholder for stream aggregation [cite: 93]
    public void printGpaDistribution() {
        System.out.println("\n--- GPA Distribution Report (Stream Aggregation) ---");
        
        // Example Stream pipeline: calculate average GPA of all students
        studentStore.values().stream()
                .mapToDouble(Student::getCurrentGpa)
                .average()
                .ifPresentOrElse(
                    avg -> System.out.printf("Average CCRM Student GPA: %.2f\n", avg),
                    () -> System.out.println("No student data available to compute average GPA.")
                );
    }
}


// ====================================================================
// SECTION 3: CLI WORKFLOW (Main application logic)
// ====================================================================

public class CCRMApplication {

    private final Scanner scanner = new Scanner(System.in);
    private final StudentService studentService = new StudentService();

    public static void main(String[] args) {
        // 1. Load Singleton configuration
        AppConfig config = AppConfig.getInstance(); 
        
        // FIX: Using the config variable addresses the "config is not used" warning.
        String dataPath = config.getDataFolderPath(); 
        System.out.println("Application data will be stored at: " + dataPath);
        
        CCRMApplication app = new CCRMApplication();
        app.run();
        
        // 5. Short platform note [cite: 125, 43]
        System.out.println("\n--- Java Platform Summary ---");
        System.out.println("Java SE (Standard Edition) is the foundation, ideal for desktop/console applications like CCRM. [cite: 43]");
        System.out.println("It contrasts with Java ME (Micro Edition) for embedded/mobile devices and Java EE (Enterprise Edition) for large-scale server-side systems. [cite: 43]");
    }

    public void run() {
        // Main CLI loop [cite: 34]
        mainLoop: while (true) { // Labeled jump used once [cite: 37]
            displayMainMenu();
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                
                // Demonstrate decision structure and switch [cite: 52]
                if (input.matches("\\d+")) { 
                    int choice = Integer.parseInt(input);

                    // Enhanced Switch Menu [cite: 36]
                    switch (choice) { 
                        case 1 -> manageStudents();
                        case 2 -> System.out.println("Placeholder for Course Management.");
                        case 3 -> handleEnrollmentAndGrading();
                        case 4 -> System.out.println("Placeholder for Import/Export Data (NIO.2 required).");
                        case 5 -> handleReportsAndBackup();
                        case 6 -> {
                            System.out.println("Exiting CCRM. Goodbye!");
                            break mainLoop; // Labeled break [cite: 37]
                        }
                        default -> {
                            System.out.println("Invalid choice. Please enter a number from 1 to 6.");
                            continue; // Continue jump control [cite: 37, 54]
                        }
                    }
                } else {
                    System.out.println("Input must be a number.");
                }
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n--- Campus Course & Records Manager (CCRM) ---");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Enrollment & Grading");
        System.out.println("4. Import/Export Data");
        System.out.println("5. Backup & Reports");
        System.out.println("6. Exit");
        System.out.print("Enter choice: ");
    }
    
    private void manageStudents() {
        System.out.println("\n--- Student Management ---");
        try {
            // Demonstrate student creation
            Student s1 = new Student("S001", "Alice Johnson", "R1001");
            s1.setCurrentGpa(3.85); // Set dummy GPA for report
            studentService.addStudent(s1);
            
            // Demonstrating Polymorphism (referencing Student via Person base class) [cite: 62]
            Person p1 = s1;
            System.out.println("Profile: " + p1.toString()); // Uses Person's toString()
            System.out.println("Enrollment Date (Fixes Warning): " + s1.getEnrollmentDate()); 
            
            // Example of filtering using a Lambda/Predicate [cite: 72]
            List<Student> highAchievers = studentService.filterStudents(s -> s.getCurrentGpa() >= 3.5);
            System.out.println("\nHigh Achiever Students:");
            for (Student s : highAchievers) { // Enhanced for loop [cite: 54]
                System.out.println(" - " + s.getFullName() + " (GPA: " + s.getCurrentGpa() + ")");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private void handleEnrollmentAndGrading() {
        System.out.println("\n--- Enrollment & Grading ---");
        
        try {
            Student s2 = new Student("S002", "Bob Smith", "R1002");
            studentService.addStudent(s2);

            // Demonstrate try/multi-catch/finally block for exceptions [cite: 85]
            try {
                studentService.enrollStudentInCourse("S002", "CS101", 3);
                studentService.enrollStudentInCourse("S002", "MA101", 3);
                studentService.enrollStudentInCourse("S002", "PH101", 3);
                studentService.enrollStudentInCourse("S002", "HI101", 3);
                // This next enrollment should trigger the custom exception [cite: 88]
                studentService.enrollStudentInCourse("S002", "AR101", 3); 
                
            } catch (MaxCreditLimitExceededException | IllegalArgumentException e) { // multi-catch [cite: 85]
                System.err.println("\nEnrollment Failed Business Rule Check: " + e.getMessage());
            } finally {
                System.out.println("Enrollment attempts complete for S002.");
            }
        } catch (IllegalArgumentException e) {
             System.err.println("Setup warning: " + e.getMessage());
        }
    }
    
    private void handleReportsAndBackup() {
         System.out.println("\n--- Reports and Backup ---");
         // Backup command that copies exported files to a timestamped folder [cite: 32]
         System.out.println("Backup command executed. (NIO.2 implementation required)");
         
         // Stream report (GPA distribution) [cite: 93]
         studentService.printGpaDistribution(); 
         
         // A recursive utility (e.g., recursively compute and print total size of the backup directory) [cite: 33]
         System.out.println("Recursion utility executed. (Folder size calculation required)");
    }
 {
    
 }
}