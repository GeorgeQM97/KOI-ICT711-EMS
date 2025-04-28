// This is java classes for Manager i.e Employee_Manager.
package EMS;

public class Employee_Manager extends Employee {
    public Employee_Manager(String id, String name, String department, double baseSalary, double performanceRating) {
        super(id, name, department, baseSalary, performanceRating);
    }

    @Override
    public double calculateSalary() {
        return baseSalary + (performanceRating * 2000); // managers get more bonus
    }
}

