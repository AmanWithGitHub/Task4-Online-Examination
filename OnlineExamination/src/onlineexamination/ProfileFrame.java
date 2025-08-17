import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileFrame extends JFrame {

    private User currentUser;
    private JTextField usernameField, nameField;
    private JPasswordField newPasswordField, confirmPasswordField;
    private JButton updateButton, changePasswordButton, startExamButton, logoutButton;

    public ProfileFrame(User user) {
        this.currentUser = user;
        setTitle("Profile - " + currentUser.getName());
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Center Panel for profile details
        JPanel profilePanel = new JPanel(new GridLayout(6, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createTitledBorder("User Profile"));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(currentUser.getUsername());
        usernameField.setEditable(false); // Username should not be editable

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(currentUser.getName());
        
        profilePanel.add(usernameLabel);
        profilePanel.add(usernameField);
        profilePanel.add(nameLabel);
        profilePanel.add(nameField);

        // Password change panel
        JPanel passwordPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Change Password"));

        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField();

        passwordPanel.add(newPasswordLabel);
        passwordPanel.add(newPasswordField);
        passwordPanel.add(confirmPasswordLabel);
        passwordPanel.add(confirmPasswordField);

        // Button Panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        updateButton = new JButton("Update Profile");
        changePasswordButton = new JButton("Change Password");
        startExamButton = new JButton("Start Exam");
        logoutButton = new JButton("Logout");

        buttonPanel.add(updateButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(startExamButton);
        buttonPanel.add(logoutButton);

        // Add all panels to the main frame
        mainPanel.add(profilePanel, BorderLayout.NORTH);
        mainPanel.add(passwordPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Action Listeners for buttons

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
        
        startExamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ExamFrame(currentUser).setVisible(true);
                dispose();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(ProfileFrame.this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    dispose();
                }
            }
        });
    }

    private void updateProfile() {
        String newName = nameField.getText();
        
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET name = ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newName);
                pstmt.setInt(2, currentUser.getUserId());
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    currentUser.setName(newName); // Update local object
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Profile update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void changePassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter and confirm your new password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET password = ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newPassword);
                pstmt.setInt(2, currentUser.getUserId());
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    currentUser.setPassword(newPassword); // Update local object
                    JOptionPane.showMessageDialog(this, "Password changed successfully!");
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Password change failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}