package EMS;

import java.io.*;
import java.util.*;
import java.util.Date;

public class Main_EMS {
    static ArrayList<Employee> employees = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, Employee> employeeById = new HashMap<>();
    static HashMap<String, Employee> employeeByName = new HashMap<>();
    static TreeMap<Double, List<Employee>> employeeByRating = new TreeMap<>();

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n### Employee Management System ###");
            System.out.println("1. Load Employee Records");
            System.out.println("2. Insert New Employee");
            System.out.println("3. Update Employee Information");
            System.out.println("4. Delete Employee");
            System.out.println("5. View Employee Details");
            System.out.println("6. Generate Performance Letters & Adjustments");
            System.out.println("7. Exit");
            System.out.print("Choose your option: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> loadEmployeesFromFile();
                case 2 -> addEmployee();
                case 3 -> updateEmployee();
                case 4 -> deleteEmployee();
                case 5 -> viewEmployee();
                case 6 -> generatePerformanceLetters();
                case 7 -> System.out.println("Exiting system...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 7);
    }

    static void loadEmployeesFromFile() {
        employees.clear();
        File file = new File("employee_data.csv");
        System.out.println("Loading from: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("Error: File not found!");
            return;
        }
        
        HashMap<String, Employee> employeeById = new HashMap<>();
        HashMap<String, Employee> employeeByName = new HashMap<>();
        TreeMap<Double, List<Employee>> employeeByRating = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            int loadedCount = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                // Use a more robust CSV parsing that handles quoted fields
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (firstLine) {
                    firstLine = false;
                    if (parts.length < 6) {
                        System.out.println("Warning: Header has only " + parts.length + " columns");
                    }
                    continue;
                }

                if (parts.length < 6) {
                    System.out.println("Skipping line " + lineNumber + ": Only " + parts.length + " columns found");
                    continue;
                }

                try {
                    String id = parts[0].trim().replaceAll("\"", "");
                    String name = parts[1].trim().replaceAll("\"", "");
                    String dept = parts[2].trim().replaceAll("\"", "");
                    String role = parts[3].trim().replaceAll("\"", "");
                    double salary = Double.parseDouble(parts[4].trim().replaceAll("\"", ""));
                    String rating = parts[5].trim().replaceAll("\"", "");

                    Employee emp = role.equalsIgnoreCase("Manager") ?
                        new Employee_Manager(id, name, dept, salary, rating) :
                        new our_employee(id, name, dept, salary, rating, role);

                    employees.add(emp);
                    employeeById.put(id, emp);
                    employeeByName.put(name.toLowerCase(), emp);

                    double parsedRating;
                    try {
                        parsedRating = Double.parseDouble(rating);
                        employeeByRating.computeIfAbsent(parsedRating, k -> new ArrayList<>()).add(emp);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid rating for employee " + name + ": " + rating);
                    }

                    loadedCount++;
                } catch (Exception e) {
                    System.out.println("Error processing line " + lineNumber + ": " + e.getMessage());
                    System.out.println("Problematic line: " + line);
                }
            }
            System.out.println("Successfully loaded " + loadedCount + " employees");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    static void addEmployee() {
        System.out.print("Enter ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Department: ");
        String dept = sc.nextLine();
        System.out.print("Enter Role (Intern/Regular/Manager): ");
        String role = sc.nextLine();
        System.out.print("Enter Base Salary: ");
        double baseSalary = Double.parseDouble(sc.nextLine());
        System.out.print("Enter Performance Rating (Poor/Average/Good/Excellent): ");
        String perf = sc.nextLine();

        Employee emp = role.equalsIgnoreCase("Manager") ?
                new Employee_Manager(id, name, dept, baseSalary, perf) :
                new our_employee(id, name, dept, baseSalary, perf, role);

        // Add to main list
        employees.add(emp);

        // add to search structures
        employeeById.put(id, emp);
        employeeByName.put(name.toLowerCase(), emp);
        
        // add to search sort structures by raging 
        double parsedRating;
        try {
            parsedRating = parseRatingToNumber(perf); // convert "Good" → 3.0, etc.
            employeeByRating.computeIfAbsent(parsedRating, k -> new ArrayList<>()).add(emp);
        } catch (Exception e) {
            System.out.println("Invalid rating, not added to rating index: " + perf);
        }

        saveEmployeesToFile();
        System.out.println("Employee added and saved.");
    }
    
    static double parseRatingToNumber(String rating) {
        switch (rating.toLowerCase()) {
            case "poor": return 3.0;
            case "average": return 5.0;
            case "good": return 7.0;
            case "excellent": return 8.0;
            default: throw new IllegalArgumentException("Invalid rating: " + rating);
        }
    }

    static void updateEmployee() {
        System.out.print("Enter Employee ID to update: ");
        String id = sc.nextLine();

        Employee e = employeeById.get(id); // efficient search O(1)
        if (e == null) {
            System.out.println("Employee not found.");
            return;
        }

        // delete index rating actual
        try {
            double oldRating = parseRatingToNumber(e.getPerformanceRating());
            employeeByRating.get(oldRating).remove(e);
            if (employeeByRating.get(oldRating).isEmpty()) {
                employeeByRating.remove(oldRating);
            }
        } catch (Exception ex) {
            // rating invalid previous
        }

        // update and index
        System.out.print("Enter new Performance Rating (Poor/Average/Good/Excellent): ");
        String newRating = sc.nextLine();
        e.setPerformanceRating(newRating);

        try {
            double newParsedRating = parseRatingToNumber(newRating);
            employeeByRating.computeIfAbsent(newParsedRating, k -> new ArrayList<>()).add(e);
        } catch (Exception ex) {
            System.out.println("Invalid new rating, not added to index.");
        }

        saveEmployeesToFile();
        System.out.println("Employee updated.");
    }

    static void deleteEmployee() {
        System.out.print("Enter Employee ID to delete: ");
        String id = sc.nextLine().trim(); // delete spaces extra
        boolean removed = employees.removeIf(e -> e.getId().equalsIgnoreCase(id)); // ignore capital letters
        if (removed) {
            saveEmployeesToFile();
            System.out.println("Employee deleted.");
        } else {
            System.out.println("Employee not found.");
        }
    }
    
    static void viewEmployee() {
        // Prompt the user to enter an employee's name or ID
        System.out.print("Enter employee name or ID: ");
        String query = sc.nextLine().trim(); // Remove extra spaces

        boolean found = false;

        // Loop through the list of employees
        for (Employee e : employees) {
            // Check if the query matches either name or ID (case-insensitive)
            if (e.getName().equalsIgnoreCase(query) || e.getId().equalsIgnoreCase(query)) {
                System.out.println("\n=== Employee Details ===");
                e.displayInfo(); // Display employee info
                System.out.printf("Calculated Salary: $%.2f\n", e.calculateSalary()); // Show salary
                found = true;
                break; // Exit the loop once found
            }
        }

        // Inform the user if no employee was found
        if (!found) {
            System.out.println("Employee not found.");
        }
    }

    static void generatePerformanceLetters() {
        // Iterate over each employee in the list
        for (Employee e : employees) {
            // Convert the performance rating to a numerical value
            double ratingValue = getRatingValue(e.performanceRating);

            // If performance is below threshold, issue a warning and apply a fine
            if (ratingValue < 4) {
                generateWarningLetter(e); // Generate warning letter
                applyFine(e);             // Apply salary deduction
            }
            // If performance is above threshold, issue appreciation and apply bonus
            else if (ratingValue > 8) {
                generateAppreciationLetter(e); // Generate appreciation letter
                applyAwardBonus(e);            // Apply bonus to salary
            }
        }

        // Save updated employee records to file
        saveEmployeesToFile();
        System.out.println("Performance letters and adjustments completed.");
    }

    private static double getRatingValue(String rating) {
        return switch (rating.toLowerCase()) {
            case "poor" -> 2.0;        // Low performance
            case "average" -> 5.0;     // Moderate performance
            case "good" -> 7.0;        // Good performance
            case "excellent" -> 9.0;   // High performance
            default -> 5.0;            // Default to average if input is unrecognized
        };
    }

    static void generateWarningLetter(Employee emp) {
        // Construct the file name using employee ID
        String fileName = "warning_letter_" + emp.getId() + ".txt";
        
        // Try to write the warning letter to a file
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("Warning Letter");
            pw.println("---------------");
            pw.println("Date: " + new Date());  // Current date
            pw.println("To: " + emp.getName());
            pw.println("Employee ID: " + emp.getId());
            pw.println("Department: " + emp.department);  // Employee's department

            // Letter body
            pw.println("\nDear " + emp.getName() + ",");
            pw.println("\nThis is a formal warning regarding your recent performance.");
            pw.println("Your current performance rating is " + emp.performanceRating + ", which is below expectations.");
            pw.println("Please take immediate steps to improve your performance.");
            pw.println("\nSincerely,");
            pw.println("Management");

            // Console confirmation
            System.out.println("Generated warning letter for " + emp.getName());
        } catch (IOException e) {
            // Handle file writing errors
            System.out.println("Error generating warning letter: " + e.getMessage());
        }
    }

    static void generateAppreciationLetter(Employee emp) {
        // Create the file name using employee ID
        String fileName = "appreciation_letter_" + emp.getId() + ".txt";
        
        // Try writing the appreciation letter to the file
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("Appreciation Letter");
            pw.println("-------------------");
            pw.println("Date: " + new Date());  // Add current date
            pw.println("To: " + emp.getName());  // Employee's name
            pw.println("Employee ID: " + emp.getId());  // Employee's ID
            pw.println("Department: " + emp.department);  // Employee's department

            // Letter content
            pw.println("\nDear " + emp.getName() + ",");
            pw.println("\nWe are pleased to recognize your outstanding performance!");
            pw.println("Your current performance rating is " + emp.performanceRating + ", which exceeds our expectations.");
            pw.println("Keep up the excellent work!");
            pw.println("\nSincerely,");
            pw.println("Management");

            // Console confirmation
            System.out.println("Generated appreciation letter for " + emp.getName());
        } catch (IOException e) {
            // Handle file writing errors
            System.out.println("Error generating appreciation letter: " + e.getMessage());
        }
    }

    static void applyAwardBonus(Employee emp) {
        emp.baseSalary *= 1.10;  // Increase salary by 10%
        System.out.printf("Applied 10%% bonus to %s's salary (New: $%.2f)\n", 
                          emp.getName(), emp.baseSalary);
    }
    
    static void applyFine(Employee emp) {
        emp.baseSalary *= 0.90;  // Reduce salary by 10%
        System.out.printf("Applied 10%% fine to %s's salary (New: $%.2f)\n", 
                          emp.getName(), emp.baseSalary);
    }

    static void saveEmployeesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("employee_data.csv"))) {
            // Write CSV header
            pw.println("ID,Name,Department,Role,Salary,PerformanceRating");

            for (Employee e : employees) {
                String role = (e instanceof Employee_Manager) ? "Manager" : 
                             (e instanceof our_employee) ? ((our_employee)e).getRole() : "Regular";

                // Escape commas by wrapping fields in quotes if necessary
                String name = escapeCsvField(e.getName());
                String department = escapeCsvField(e.department);
                String salaryFormatted = String.format("%.2f", e.baseSalary);

                pw.println(e.getId() + "," + name + "," + department + "," +
                           role + "," + salaryFormatted + "," + e.performanceRating);
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Helper method to escape CSV fields containing commas or quotes
    private static String escapeCsvField(String field) {
        if (field.contains(",") || field.contains("\"")) {
            field = field.replace("\"", "\"\""); // escape quotes by doubling
            return "\"" + field + "\"";
        }
        return field;
    }
}