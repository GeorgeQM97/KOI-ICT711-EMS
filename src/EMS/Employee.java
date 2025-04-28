package EMS;

public abstract class Employee {
    protected String id;
    protected String name;
    protected String department;
    protected double baseSalary;
    protected double performanceRating;

    public Employee(String id, String name, String department, double baseSalary, double performanceRating) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.baseSalary = baseSalary;
        this.performanceRating = performanceRating;
    }

    public abstract double calculateSalary();

    public String getId() { return id; }

    public String getName() { return name; }
    
    public double getBaseSalary() { return baseSalary; }
    
    public double getPerformanceRating() { return performanceRating; }

    public void setPerformanceRating(double rating) {
        this.performanceRating = rating;
    }
    
    public void setSalary(double salary) {
        this.baseSalary = salary;
    }

    public void displayInfo() {
        System.out.println("ID: " + id + ", Name: " + name + ", Department: " + department +
                ", Base Salary: " + baseSalary + ", Performance: " + performanceRating);
    }
}

