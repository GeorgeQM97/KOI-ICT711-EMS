package EMS;

public class Employee_Manager extends Employee {
    public Employee_Manager(String id, String name, String department, double baseSalary, String performanceRating) {
        super(id, name, department, baseSalary, performanceRating);
    }

    @Override
    public double calculateSalary() {
        double ratingValue = getRatingValue(performanceRating);
        return baseSalary + (ratingValue * 2000);
    }

    private double getRatingValue(String rating) {
        return switch (rating.toLowerCase()) {
            case "poor" -> 2.0;
            case "average" -> 5.0;
            case "good" -> 7.0;
            case "excellent" -> 9.0;
            default -> 5.0;
        };
    }
}