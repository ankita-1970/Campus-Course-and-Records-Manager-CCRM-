# Campus-Course-and-Records-Manager-CCRM-

# Campus-Course-Records-Manager-CCRM-

🏛️ CCRM Project Overview & ArchitectureName: 

Campus Course & Records Manager (CCRM)Platform 
Console-based Java SE (Standard Edition) Application.Goal 
To manage core academic data, including student enrollment, course details, and basic grade calculation.Structure 
Designed with clear separation, utilizing domain models (e.g.,Student, Course) and service classes StudentService to adhere to Object-Oriented Design (OOD) principles.✨ 

✨ Core OOP and Design Patterns:
The project rigidly enforces core OOP principles and incorporates industry-standard design patterns:

Encapsulation: Achieved by declaring all data fields as private (e.g., Person.id) and providing controlled access via public getter/setter methods.
Inheritance: Demonstrated by having Student and Instructor (conceptual) extend the abstract base class Person.
Abstraction: The abstract class Person defines an abstract method getRole(), requiring concrete subclasses to provide implementation details.
Polymorphism: The Person reference can point to a Student object, and the appropriate getRole() method is called at runtime.
Singleton Pattern: The AppConfig class ensures that only one instance of the configuration object can exist, providing a global access point for application settings like the data path.
Builder Pattern: The CourseBuilder class provides a fluent, step-by-step mechanism for constructing complex Course objects, separating the construction logic from the object's representation.

💻 Modern Java Features and APIs:
The CCRM leverages Java SE 8+ features to demonstrate competence in modern language usage:

Streams API (Aggregation): Used in StudentService to perform aggregation operations, such as calculating the average GPA of all students using .stream(), .mapToDouble(), and.average().
Functional Interfaces/Lambdas: Demonstrated in StudentService.filterStudents(), which accepts a Predicate<Student> (a functional interface) implemented using a lambda expression for dynamic filtering logic (e.g., filtering high achievers).
Date/Time API (JSR-310): Used for precise and readable date management, such as storing the enrollmentDate LocalDate) in the Student class and generating timestamped backup folders {LocalDateTime) in the BackupService.
NIO.2 (Modern I/O): The BackupService uses java.nio.file.Path and Files utility methods (e.g., Files.copy(), Files.createDirectories()) for robust and efficient file system operations.
Enums with Fields: The Grade enumeration stores both the grade letter and its corresponding gradePoint value, enabling easy GPA calculation.
Immutability: The CourseCode class is designed as an immutable value object, with all fields declared as final.

🛑 Robust Error and Data Handling:

Custom Exception Handling:A custom unchecked exception, MaxCreditLimitExceededException, is thrown by the StudentService when enrollment rules are violated, separating business logic failures from system errors.
Multi-catch: The application's CLI logic uses the multi-catch block (e.g., catch (Exception1 | Exception2 e)) to handle different exception types in a single, clean block.
Assertions: Assertions are used (e.g., assert courseCode != null) to check conditions that should always be true (invariants), aiding in debugging and code integrity.
Recursion Utility: A separate utility class demonstrates recursive logic using Files.walk() to traverse the file system (e.g., calculate the total size of the backup directory).
