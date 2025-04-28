package EMS;

public class Our_Employees extends Employee {
    public Our_Employees(String id, String name, String department, double baseSalary, double performanceRating) {
        super(id, name, department, baseSalary, performanceRating);
    }

    @Override
    public double calculateSalary() {
        return baseSalary + (performanceRating * 1000); // example logic
    }
}
