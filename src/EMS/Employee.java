package EMS;

public abstract class Employee {
    protected String id;
    protected String name;
    protected String department;
    protected double baseSalary;
    protected String performanceRating;  // this is string

    public Employee(String id, String name, String department, double baseSalary, String performanceRating) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.baseSalary = baseSalary;
        this.performanceRating = performanceRating;
    }

    public abstract double calculateSalary();

    public String getId() { return id; }

    public String getName() { return name; }

    public void setPerformanceRating(String rating) {
        this.performanceRating = rating;
    }
    
    public String getPerformanceRating() {
        return performanceRating;
    }

    public void displayInfo() {
        System.out.println("ID: " + id + ", Name: " + name + ", Department: " + department +
                ", Base Salary: " + baseSalary + ", Performance: " + performanceRating);
    }
}

