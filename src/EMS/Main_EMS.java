package EMS;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// This is the java collections which make use of array, hashmap etc as n
// well as add() and get().
public class Main_EMS {
    static ArrayList<Employee> employees = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("### Employee Management System Module: ###");
            System.out.println("1. Load Employee Records");
            System.out.println("2. Insert New Employee");
            System.out.println("3. Update Employee Information");
            System.out.println("4. Delete Employee");
            System.out.println("5. Employee Queries");
            System.out.println("6. Exit");
            System.out.print("Menu--->> Choose your option: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> loadEmployeesFromFile();
                case 2 -> addEmployee();
                case 3 -> updateEmployee();
                case 4 -> deleteEmployee();
                case 5 -> {
                	System.out.println("1. View Employee Details");
                	System.out.println("2. Issue Warning letters to employees with a performance below or equal than 5");
                	System.out.println("3. Issue Appreciation letters to employees with a performance above or equal than 8");
                	System.out.println("4. Award a bonus +10% Salary to employees with a performance above or equal than 8");
                	System.out.println("5. Apply a fine -10% Salary to employees with a performance below or equal than 4");
                	System.out.println("6. Go Back");
                	System.out.print("Menu--->> Choose your option: ");
                	choice = Integer.parseInt(sc.nextLine());

                    switch (choice) {
                        case 1 -> viewEmployee(); 
                        case 2 -> issueWarningLetters();
                        case 3 -> issueAppreciationLetters();
                        case 4 -> awardBonuses();
                        case 5 -> applyFines();
                        case 6 -> System.out.println("Going back...");
                        default -> System.out.println("Invalid choice!");
                    }
                }
                case 6 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 6);
    }
// This is file-handeling, reading into file and writing onto file***
    static void loadEmployeesFromFile() {
        employees.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("employee_data.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                String name = parts[1];
                String dept = parts[2];
                String type = parts[3];
                double baseSalary = Double.parseDouble(parts[4]);
                double perfRating = Double.parseDouble(parts[5]);

                if (type.equals("Employee_Manager")) {
                    employees.add(new Employee_Manager(id, name, dept, baseSalary, perfRating));
                } else {
                    employees.add(new Our_Employees(id, name, dept, baseSalary, perfRating));
                }
            }
            System.out.println("Employees loaded successfully.");
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
        System.out.print("Enter Type (Manager/Regular): ");
        String type = sc.nextLine();
        System.out.print("Enter Base Salary: ");
        double baseSalary = Double.parseDouble(sc.nextLine());
        System.out.print("Enter Performance Rating: ");
        double perf = Double.parseDouble(sc.nextLine());

        Employee emp = (type.equals("Manager")) ?
                new Employee_Manager(id, name, dept, baseSalary, perf) :
                new Our_Employees(id, name, dept, baseSalary, perf);
        employees.add(emp);
        saveEmployeesToFile();
        System.out.println("Employee added and saved.");
    }

    static void updateEmployee() {
        System.out.print("Enter Employee ID to update: ");
        String id = sc.nextLine();
        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                System.out.print("Enter new Performance Rating: ");
                double newRating = Double.parseDouble(sc.nextLine());
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
        employees.removeIf(e -> e.getId().equals(id));
        saveEmployeesToFile();
        System.out.println("Employee deleted.");
    }

    static void viewEmployee() {
        System.out.print("Enter name or ID: ");
        String query = sc.nextLine();
        for (Employee e : employees) {
            if (e.getName().equalsIgnoreCase(query) || e.getId().equalsIgnoreCase(query)) {
                e.displayInfo();
                return;
            }
        }
        System.out.println("Employee not found.");
    }

    static void saveEmployeesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("employee_data.csv"))) {
            for (Employee e : employees) {
                pw.println(e.getId() + "," + e.getName() + "," + e.department + "," +
                        (e instanceof Employee_Manager ? "Manager" : "Regular") + "," +
                        e.baseSalary + "," + e.performanceRating);
            }
// This is Exception Handeling for input and output queries
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
    
    static String getWarningLetter(String employeeName) {
    	return "Dear " + employeeName +",\r\n"
    			+ "\r\n"
    			+ "We have observed a consistent decline in your performance, including missed deadlines and reduced work quality. This impacts team productivity and project outcomes. Please treat this as a formal warning and take immediate steps to improve. Continued low performance may lead to further disciplinary action.\r\n"
    			+ "\r\n"
    			+ "Sincerely,\r\n"
    			+ "Management";
    }
    
    static String getAppreciationLetter(String employeeName) {
    	return "Dear " + employeeName +",\r\n"
    			+ "\r\n"
    			+ "We sincerely appreciate your dedication, consistent hard work, and the positive impact you’ve had on the team. Your commitment to excellence and willingness to go the extra mile do not go unnoticed. Thank you for being an essential part of our success. Keep up the great work!.\r\n"
    			+ "\r\n"
    			+ "Sincerely,\r\n"
    			+ "Management";
    }
    
    static void issueWarningLetters() {
    	try {
            for (Employee e : employees) {
            	if(e.getPerformanceRating() <= (double)5) {
            		String formattedDateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
                    String warningLetterName = "warning_" + e.getName() + "_" + formattedDateNow + ".txt";
                    File file = new File(warningLetterName);

                    if (file.createNewFile()) {
                        System.out.println("File created: " + file.getName());
                    } else {
                        System.out.println("File already exists, will overwrite.");
                    }

                    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                        writer.write(getWarningLetter(e.getName()));
                    }           		
            	}
            }
    	// This is Exception Handeling for input and output queries
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
    
    static void issueAppreciationLetters() {
    	try {
            for (Employee e : employees) {
            	if(e.getPerformanceRating() >= (double)8) {
            		String formattedDateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
                    String appreciationLetterName = "appreciation_" + e.getName() + "_" + formattedDateNow + ".txt";
                    File file = new File(appreciationLetterName);
                    
                    if (file.createNewFile()) {
                        System.out.println("File created: " + file.getName());
                    } else {
                        System.out.println("File already exists, will overwrite.");
                    }

                    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                        writer.write(getAppreciationLetter(e.getName()));
                    }              		
            	}
            }
    	// This is Exception Handeling for input and output queries
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
    
    static void awardBonuses() {
    	for (Employee e : employees) {
        	if(e.getPerformanceRating() >= (double)8) {
        		e.setSalary(e.getBaseSalary()*(double)(1 + 0.1));        		
        	}
        }
    	saveEmployeesToFile();
    }
    
    static void applyFines() {
    	for (Employee e : employees) {
        	if(e.getPerformanceRating() <= (double)4) {
        		e.setSalary(e.getBaseSalary()*(double)(1 - 0.1));        		
        	}
        }
    	saveEmployeesToFile();
    }
}
