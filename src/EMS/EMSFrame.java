package EMS;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class EMSFrame extends JFrame {
    public EMSFrame() {
        setTitle("Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());
        
        // Add components like buttons, tables, etc.
        // Example:
        JButton viewEmployeesButton = new JButton("View Employees");
        add(viewEmployeesButton, BorderLayout.NORTH);

        // Add action listeners
        viewEmployeesButton.addActionListener(e -> {
            // Call EMSController method to get employee list
        });

        setVisible(true);
    }
}
