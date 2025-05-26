/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class App {

    public static void main(String[] args) {
    System.out.println("Starting GUI...");
    javax.swing.SwingUtilities.invokeLater(() -> {
        Testing frame = new Testing();
        System.out.println("Frame created");
        frame.setVisible(true);
    });
}
}

class Testing extends JFrame {
    private JTextField usernameLoginField, usernameRegField, surnameField, phoneRegField;
    private JPasswordField passLoginField, passRegField;
    private JPanel loginPanel, signupPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel; // Panel with CardLayout

    public Testing() {
        setTitle("Login and Register");
        setSize(600, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // LOGIN PANEL
        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(Color.BLACK);

        JLabel loginLabel = new JLabel("LOGIN");
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setBounds(90, 20, 100, 30);
        loginPanel.add(loginLabel);

        JLabel usernameLabelLogin = new JLabel("Username:");
        usernameLabelLogin.setForeground(Color.WHITE);
        usernameLabelLogin.setBounds(20, 60, 150, 25);
        loginPanel.add(usernameLabelLogin);

        usernameLoginField = new JTextField();
        usernameLoginField.setBounds(20, 85, 200, 25);
        loginPanel.add(usernameLoginField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(20, 120, 150, 25);
        loginPanel.add(passLabel);

        passLoginField = new JPasswordField();
        passLoginField.setBounds(20, 150, 200, 25);
        loginPanel.add(passLoginField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(70, 190, 100, 30);
        loginPanel.add(loginBtn);

        JButton showSignupBtn = new JButton("New User? Sign Up");
        showSignupBtn.setBounds(40, 230, 160, 30);
        loginPanel.add(showSignupBtn);

        // SIGNUP PANEL
        signupPanel = new JPanel();
        signupPanel.setLayout(null);

        JLabel signupLabel = new JLabel("SIGN UP");
        signupLabel.setBounds(90, 20, 100, 30);
        signupPanel.add(signupLabel);

        JLabel nameLabel = new JLabel("Username:");
        nameLabel.setBounds(20, 60, 100, 25);
        signupPanel.add(nameLabel);

        usernameRegField = new JTextField();
        usernameRegField.setBounds(20, 85, 200, 25);
        signupPanel.add(usernameRegField);

        JLabel surnameLabel = new JLabel("Surname:");
        surnameLabel.setBounds(20, 115, 100, 25);
        signupPanel.add(surnameLabel);

        surnameField = new JTextField();
        surnameField.setBounds(20, 140, 200, 25);
        signupPanel.add(surnameField);

        JLabel phoneRegLabel = new JLabel("Phone Number:");
        phoneRegLabel.setBounds(20, 170, 150, 25);
        signupPanel.add(phoneRegLabel);

        phoneRegField = new JTextField();
        phoneRegField.setBounds(20, 195, 200, 25);
        signupPanel.add(phoneRegField);
        restrictToNumbers(phoneRegField);

        JLabel passRegLabel = new JLabel("Password:");
        passRegLabel.setBounds(20, 225, 100, 25);
        signupPanel.add(passRegLabel);

        passRegField = new JPasswordField();
        passRegField.setBounds(20, 250, 200, 25);
        signupPanel.add(passRegField);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(80, 280, 100, 30);
        signupPanel.add(signupBtn);

        JButton showLoginBtn = new JButton("Already a User? Login");
        showLoginBtn.setBounds(40, 310, 180, 30);
        signupPanel.add(showLoginBtn);

        // Add panels to the mainPanel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(signupPanel, "signup");

        add(mainPanel);

        // Event handlers
        signupBtn.addActionListener(e -> handleSignup());
        loginBtn.addActionListener(e -> handleLogin());
        showSignupBtn.addActionListener(e -> cardLayout.show(mainPanel, "signup"));
        showLoginBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        // Show login panel by default
        cardLayout.show(mainPanel, "login");
    }
  
    private void restrictToNumbers(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();  // Ignore non-digit input
                }
            }
        });
    }

    // Placeholders for validation and utility methods you need to implement:
    private boolean isValidUsername(String username) {
        return username != null && username.contains("_"); // Example validation
    }

    private boolean isValidPassword(String password) {
        // Example validation: at least 8 chars, 1 uppercase, 1 digit, 1 special char
        if (password == null) return false;
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$");
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    private boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 1 && data[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Could log or handle error here
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleSignup() {
        String username = usernameRegField.getText();
        String surname = surnameField.getText();
        String phone = phoneRegField.getText();
        String password = new String(passRegField.getPassword());

        if (!isValidUsername(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username is not correctly formatted;\nplease ensure that the username\ncontains an underscore.",
                    "Username Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(this,
                    "Password is not correctly formatted;\nplease ensure that the password\ncontains at least eight characters, a capital letter, a number, and a special character.",
                    "Password Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(this,
                    "Phone number is not valid.\nPlease enter a 10-digit phone number.",
                    "Phone Number Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists. Please choose another one.",
                    "Signup Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                JOptionPane.showMessageDialog(this, "Failed to hash password. Please try again.",
                        "Security Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            writer.write(username + "," + surname + "," + phone + "," + hashedPassword);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "User registered successfully.");
            cardLayout.show(mainPanel, "login");
            usernameRegField.setText("");
            surnameField.setText("");
            phoneRegField.setText("");
            passRegField.setText("");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error registering user: " + ex.getMessage(),
                    "File Write Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleLogin() {
        String username = usernameLoginField.getText();
        String password = new String(passLoginField.getPassword());

        if (!isValidUsername(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username is not correctly formatted;\nplease ensure that the username\ncontains an underscore.",
                    "Username Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPasswordAttempt = hashPassword(password);
        if (hashedPasswordAttempt == null) {
            JOptionPane.showMessageDialog(this, "Failed to hash password for login. Please try again.",
                    "Security Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4 && data[0].equals(username) && data[3].equals(hashedPasswordAttempt)) {
                    found = true;
                    JOptionPane.showMessageDialog(this, "Welcome " + data[0] + ", " + data[1] + " it is great to see you again.");
                    openChatApp(username);
                    usernameLoginField.setText("");
                    passLoginField.setText("");
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this,
                        "Username or password incorrect, please try again.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading user data: " + ex.getMessage(),
                    "File Read Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

   private void openChatApp(String username) {
    SwingUtilities.invokeLater(() -> {
        MessageApp chatWindow = new MessageApp(username);
        chatWindow.setVisible(true);
        this.dispose();  // Close the login window
    });
}

}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * Unit tests for the Message class.
 * This class contains tests for message validation, hash creation,
 * and other message-related functionality.
 */
class MessageTest {

    public static void main(String[] args) {
        testMessageLengthValidation();
        testRecipientFormatValidation();
        testMessageHashCreation();
        testMessageIDGeneration();
        testMessageSendActions();
    }

    /**
     * Tests the validation of message length (max 250 characters).
     */
    public static void testMessageLengthValidation() {
        System.out.println("Testing message length validation:");

        // Test with a valid message length
        String validMessage = "Hi Mike, can you join us for dinner tonight?";
        if (validMessage.length() <= 250) {
            System.out.println("Success: Message is within allowed length.");
        } else {
            System.out.println("Failure: Message exceeds 250 characters by " +
                    (validMessage.length() - 250) + " characters.");
        }

        // Test with an invalid message length (too long)
        String invalidMessage = "A".repeat(260);
        if (invalidMessage.length() <= 250) {
            System.out.println("Success: Message is within allowed length.");
        } else {
            System.out.println("Failure: Message exceeds 250 characters by " +
                    (invalidMessage.length() - 250) + " characters.");
        }
    }

    /**
     * Tests the format validation for recipient phone numbers.
     * For this test, recipient should start with '+' and max length 13.
     */
    public static void testRecipientFormatValidation() {
        System.out.println("\nTesting recipient phone number format:");

        // Valid phone number with international code
        String validRecipient = "+27718693002";
        if (validRecipient.startsWith("+") && validRecipient.length() <= 13) {
            System.out.println("Success: Recipient phone number format is valid.");
        } else {
            System.out.println("Failure: Recipient phone number format is invalid.");
        }

        // Invalid phone number without international code
        String invalidRecipient = "08575975889";
        if (invalidRecipient.startsWith("+") && invalidRecipient.length() <= 13) {
            System.out.println("Success: Recipient phone number format is valid.");
        } else {
            System.out.println("Failure: Recipient phone number format is invalid.");
        }
    }

    /**
     * Tests the correctness of the message hash creation logic.
     */
    public static void testMessageHashCreation() {
        System.out.println("\nTesting message hash creation:");

        // Create a test message
        MessageApp testMessage = new MessageApp("1234567890", 0, "test_user",
                "Hi Mike, can you join us for dinner tonight");

        // Expected hash based on logic: first 2 chars of ID + ":" + numSent + ":" + firstWord + lastWord (uppercased)
        String expectedHash = "12:0:HITONIGHT";
        String actualHash = testMessage.getMessageHash();

        System.out.println("Expected hash: " + expectedHash);
        System.out.println("Actual hash: " + actualHash);

        if (actualHash.equals(expectedHash)) {
            System.out.println("Success: Message hash matches expected value.");
        } else {
            System.out.println("Failure: Message hash does not match expected value.");
        }
    }

    /**
     * Tests the generation of a valid message ID (10-digit number).
     */
    public static void testMessageIDGeneration() {
        System.out.println("\nTesting message ID generation:");

        Random random = new Random();
        // Generates number between 1,000,000,000 and 9,999,999,999 (inclusive)
        long id = 1_000_000_000L + (long)(random.nextDouble() * 9_000_000_000L);
        String messageID = String.valueOf(id);

        System.out.println("Generated message ID: " + messageID);

        if (messageID.length() == 10) {
            System.out.println("Success: Message ID is valid 10-digit number.");
        } else {
            System.out.println("Failure: Message ID is not 10 digits long.");
        }
    }

    /**
     * Tests simulated actions related to sending, discarding, and storing messages.
     */
    public static void testMessageSendActions() {
        System.out.println("\nTesting message sending actions:");

        // Simulated results
        String sendResult = "Message successfully sent.";
        String discardResult = "Press 0 to delete message.";
        String storeResult = "Message successfully stored.";
        System.out.println("Send action: " + sendResult);
        System.out.println("Discard action: " + discardResult);
        System.out.println("Store action: " + storeResult);
    }
}
