package EMS;

public class our_employee extends Employee {
    private String role;

    public our_employee(String id, String name, String department, double baseSalary, String performanceRating, String role) {
        super(id, name, department, baseSalary, performanceRating);
        this.role = role;
    }

    public String getRole() {  // Add this getter method
        return role;
    }

    @Override
    public double calculateSalary() {
        double ratingValue = getRatingValue(performanceRating);
        double bonus = ratingValue * 1000;
        return "Intern".equalsIgnoreCase(role) ? baseSalary + (bonus * 0.5) : baseSalary + bonus;
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

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Role: " + role);
    }
}