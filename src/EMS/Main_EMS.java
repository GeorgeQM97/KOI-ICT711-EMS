package EMS;

import java.io.*;
import java.util.*;
import java.util.Date;

public class Main_EMS {
    static ArrayList<Employee> employees = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

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
        employees.add(emp);
        saveEmployeesToFile();
        System.out.println("Employee added and saved.");
    }

    static void updateEmployee() {
        System.out.print("Enter Employee ID to update: ");
        String id = sc.nextLine();
        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                System.out.print("Enter new Performance Rating (Poor/Average/Good/Excellent): ");
                String newRating = sc.nextLine();
                e.setPerformanceRating(newRating);
                saveEmployeesToFile();
                System.out.println("Employee updated.");
                return;
            }
        }
        System.out.println("Employee not found.");
    }

    static void deleteEmployee() {
        System.out.print("Enter Employee ID to delete: ");
        String id = sc.nextLine();
        if (employees.removeIf(e -> e.getId().equals(id))) {
            saveEmployeesToFile();
            System.out.println("Employee deleted.");
        } else {
            System.out.println("Employee not found.");
        }
    }

    static void viewEmployee() {
        System.out.print("Enter employee name or ID: ");
        String query = sc.nextLine();
        for (Employee e : employees) {
            if (e.getName().equalsIgnoreCase(query) || e.getId().equalsIgnoreCase(query)) {
                e.displayInfo();
                System.out.printf("Calculated Salary: $%.2f\n", e.calculateSalary());
                return;
            }
        }
        System.out.println("Employee not found.");
    }

    static void generatePerformanceLetters() {
        for (Employee e : employees) {
            double ratingValue = getRatingValue(e.performanceRating);
            if (ratingValue < 4) {
                generateWarningLetter(e);
                applyFine(e);
            } else if (ratingValue > 8) {
                generateAppreciationLetter(e);
                applyAwardBonus(e);
            }
        }
        saveEmployeesToFile();
        System.out.println("Performance letters and adjustments completed.");
    }

    private static double getRatingValue(String rating) {
        return switch (rating.toLowerCase()) {
            case "poor" -> 2.0;
            case "average" -> 5.0;
            case "good" -> 7.0;
            case "excellent" -> 9.0;
            default -> 5.0;
        };
    }

    static void generateWarningLetter(Employee emp) {
        String fileName = "warning_letter_" + emp.getId() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("Warning Letter");
            pw.println("---------------");
            pw.println("Date: " + new Date());
            pw.println("To: " + emp.getName());
            pw.println("Employee ID: " + emp.getId());
            pw.println("Department: " + emp.department);
            pw.println("\nDear " + emp.getName() + ",");
            pw.println("\nThis is a formal warning regarding your recent performance.");
            pw.println("Your current performance rating is " + emp.performanceRating + ", which is below expectations.");
            pw.println("Please take immediate steps to improve your performance.");
            pw.println("\nSincerely,");
            pw.println("Management");
            System.out.println("Generated warning letter for " + emp.getName());
        } catch (IOException e) {
            System.out.println("Error generating warning letter: " + e.getMessage());
        }
    }

    static void generateAppreciationLetter(Employee emp) {
        String fileName = "appreciation_letter_" + emp.getId() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("Appreciation Letter");
            pw.println("-------------------");
            pw.println("Date: " + new Date());
            pw.println("To: " + emp.getName());
            pw.println("Employee ID: " + emp.getId());
            pw.println("Department: " + emp.department);
            pw.println("\nDear " + emp.getName() + ",");
            pw.println("\nWe are pleased to recognize your outstanding performance!");
            pw.println("Your current performance rating is " + emp.performanceRating + ", which exceeds our expectations.");
            pw.println("Keep up the excellent work!");
            pw.println("\nSincerely,");
            pw.println("Management");
            System.out.println("Generated appreciation letter for " + emp.getName());
        } catch (IOException e) {
            System.out.println("Error generating appreciation letter: " + e.getMessage());
        }
    }

    static void applyAwardBonus(Employee emp) {
        emp.baseSalary *= 1.10;
        System.out.printf("Applied 10%% bonus to %s's salary (New: $%.2f)\n", 
                         emp.getName(), emp.baseSalary);
    }

    static void applyFine(Employee emp) {
        emp.baseSalary *= 0.90;
        System.out.printf("Applied 10%% fine to %s's salary (New: $%.2f)\n", 
                         emp.getName(), emp.baseSalary);
    }

    static void saveEmployeesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("employee_data.csv"))) {
            pw.println("ID,Name,Department,Role,Salary,PerformanceRating");
            for (Employee e : employees) {
                String role = (e instanceof Employee_Manager) ? "Manager" : 
                             (e instanceof our_employee) ? ((our_employee)e).getRole() : "Regular";
                
                pw.println(e.getId() + "," + e.getName() + "," + e.department + "," +
                          role + "," + e.baseSalary + "," + e.performanceRating);
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}