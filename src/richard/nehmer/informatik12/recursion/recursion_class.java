package richard.nehmer.informatik12.recursion;

import richard.nehmer.informatik12.ProjectModule;

import javax.swing.*;
import java.awt.*;

public class recursion_class implements ProjectModule {
    public static void main(String[] args) {
    }
    public long calculateRecursion(int x) {
        checkForValidNumber(x);
        if (x < 2) {
            return 1;
        }
        return calculateRecursion(x-1) +  calculateRecursion(x-2);
    }
    public long calculateFacultative(int x) {
        checkForValidNumber(x);
        if (x < 2) {
            return 1;
        }
        return x * calculateFacultative(x-1);
    }
    private void checkForValidNumber(int x) {
        if (x<0) {
            throw new IllegalArgumentException(x + " is not a Valid number as it is negative. Please enter a positive number.");
        }
    }

    @Override
    public String getName() {
        return "Recursion";
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Recursion", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }
}
