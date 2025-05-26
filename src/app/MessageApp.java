package app;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class MessageApp extends JFrame {

    private final String username;
    private static final String MESSAGE_FILE = "messages.txt";
    private final Random random = new Random();

    public MessageApp(String username) {
        this.username = username;
        setTitle("QuickChat - Logged in as: " + username);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTextArea messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        JTextField recipientField = new JTextField();
        JButton sendButton = new JButton("Send Message");
        JButton showMessagesButton = new JButton("Show Messages");
        JButton quitButton = new JButton("Quit");

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(new JLabel("Recipient (+country code):"), BorderLayout.NORTH);
        topPanel.add(recipientField, BorderLayout.CENTER);
        topPanel.add(sendButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(showMessagesButton);
        bottomPanel.add(quitButton);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            String recipient = recipientField.getText().trim();
            String message = messageArea.getText().trim();
            if (recipient.isEmpty() || message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Recipient and message cannot be empty.");
                return;
            }

            long id = generateMessageID();
            String timestamp = getCurrentTimestamp();
            boolean saved = saveMessage(id, recipient, message, timestamp);
            if (saved) {
                JOptionPane.showMessageDialog(this, "Message sent! ID: " + id + "\nSent at: " + timestamp);
                messageArea.setText("");
                recipientField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save message. See console for details.");
            }
        });

        showMessagesButton.addActionListener(e -> showSavedMessages());

        quitButton.addActionListener(e -> System.exit(0));
    }

    private void showSavedMessages() {
        StringBuilder messages = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                messages.append(line).append("\n");
            }
        } catch (IOException ex) {
            messages.append("No messages found or error reading the file.");
        }
        JTextArea textArea = new JTextArea(messages.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(480, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Saved Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Generates a random 10-digit identifier for each message.
     */
    private long generateMessageID() {
        return 1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L);
    }

    /**
     * Gets the current timestamp formatted as yyyy-MM-dd HH:mm:ss
     */
    private String getCurrentTimestamp() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(fmt);
    }

    /**
     * Appends the message record to a plain-text file in pipe-separated format.
     * <pre>
     * ID|username|recipient|body|yyyy-MM-dd HH:mm:ss
     * </pre>
     */
    private boolean saveMessage(long id, String recipient, String body, String timestamp) {
        String line = id + "|" + username + "|" + recipient + "|" + body.replace("\n", "\\n") + "|" + timestamp;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MESSAGE_FILE, true))) {
            bw.write(line);
            bw.newLine();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MessageApp("test_user").setVisible(true));
    }
}
