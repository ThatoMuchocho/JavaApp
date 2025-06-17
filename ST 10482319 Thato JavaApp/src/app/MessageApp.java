package app;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageApp extends JFrame {

    private final String username;
    private static final String MESSAGE_FILE = "messages.txt";
    private final Random random = new Random();

    private JTextArea messageArea;
    private JTextField recipientField;

    public MessageApp(String username, int par, String test_user, String hi_Mike_can_you_join_us_for_dinner_tonigh) {
        this.username = username;
        setTitle("QuickChat - Logged in as: " + username);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        recipientField = new JTextField();

        JButton sendButton = new JButton("Send Message");
        JButton showMessagesButton = new JButton("Show Messages");
        JButton quitButton = new JButton("Quit");
        JButton longestMsgButton = new JButton("Show Longest Message");
        JButton deleteByHashButton = new JButton("Delete Message by Hash");
        JButton reportButton = new JButton("Display Message Report");
        JButton searchRecipientButton = new JButton("Search Messages by Recipient");

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(new JLabel("Recipient (+country code):"), BorderLayout.NORTH);
        topPanel.add(recipientField, BorderLayout.CENTER);
        topPanel.add(sendButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(showMessagesButton);
        bottomPanel.add(longestMsgButton);
        bottomPanel.add(deleteByHashButton);
        bottomPanel.add(reportButton);
        bottomPanel.add(searchRecipientButton);
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
        longestMsgButton.addActionListener(e -> showLongestMessage());
        deleteByHashButton.addActionListener(e -> deleteMessageByHash());
        reportButton.addActionListener(e -> displayMessageReport());
        searchRecipientButton.addActionListener(e -> searchMessagesByRecipient());
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
        scrollPane.setPreferredSize(new Dimension(580, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Saved Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLongestMessage() {
        String longestMessage = null;
        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            String line;
            int maxLength = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String body = parts[3].replace("\\n", "\n");
                    if (body.length() > maxLength) {
                        maxLength = body.length();
                        longestMessage = body;
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading messages file.");
            return;
        }

        if (longestMessage != null) {
            JOptionPane.showMessageDialog(this, longestMessage, "Longest Message (length: " + longestMessage.length() + ")", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No messages found.");
        }
    }

    private void deleteMessageByHash() {
        String hash = JOptionPane.showInputDialog(this, "Enter message hash to delete:");
        if (hash == null || hash.trim().isEmpty()) return;

        File inputFile = new File(MESSAGE_FILE);
        File tempFile = new File("messages_temp.txt");

        boolean deleted = false;
        int lineIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] parts = currentLine.split("\\|");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String body = parts[3].replace("\\n", "\n");
                    String generatedHash = generateMessageHash(id, lineIndex, body);
                    if (generatedHash.equals(hash)) {
                        deleted = true;
                        lineIndex++;
                        continue;
                    }
                }
                writer.write(currentLine);
                writer.newLine();
                lineIndex++;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error processing messages file.");
            return;
        }

        if (!inputFile.delete()) {
            JOptionPane.showMessageDialog(this, "Could not delete original message file.");
            return;
        }
        if (!tempFile.renameTo(inputFile)) {
            JOptionPane.showMessageDialog(this, "Could not rename temp file.");
            return;
        }

        if (deleted) {
            JOptionPane.showMessageDialog(this, "Message with hash " + hash + " deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No message found with the given hash.");
        }
    }

    private void displayMessageReport() {
        int count = 0;
        String earliest = null;
        String latest = null;

        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                count++;
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String timestamp = parts[4];
                    if (earliest == null || timestamp.compareTo(earliest) < 0) earliest = timestamp;
                    if (latest == null || timestamp.compareTo(latest) > 0) latest = timestamp;
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading messages file.");
            return;
        }

        String report = "Total messages: " + count + "\n" +
                "Earliest message: " + (earliest != null ? earliest : "N/A") + "\n" +
                "Latest message: " + (latest != null ? latest : "N/A");

        JOptionPane.showMessageDialog(this, report, "Message Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchMessagesByRecipient() {
        String recipient = JOptionPane.showInputDialog(this, "Enter recipient phone number to search:");
        if (recipient == null || recipient.trim().isEmpty()) return;

        List<String> matchedMessages = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String rec = parts[2];
                    if (rec.equals(recipient)) {
                        matchedMessages.add(line);
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading messages file.");
            return;
        }

        if (matchedMessages.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No messages found for recipient: " + recipient);
        } else {
            JTextArea textArea = new JTextArea(String.join("\n", matchedMessages));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(580, 300));
            JOptionPane.showMessageDialog(this, scrollPane, "Messages to " + recipient, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private long generateMessageID() {
        return 1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L);
    }

    private String getCurrentTimestamp() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(fmt);
    }

    private boolean saveMessage(long id, String recipient, String body, String timestamp) {
        String messageBodyCleaned = body.replace("\n", "\\n");
        int messageNumber = getMessageCount();
        String hash = generateMessageHash(String.valueOf(id), messageNumber, body);

        String line = id + "|" + username + "|" + recipient + "|" + messageBodyCleaned + "|" + timestamp + "|" + hash;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MESSAGE_FILE, true))) {
            bw.write(line);
            bw.newLine();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private int getMessageCount() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            // Ignore if file doesn't exist
        }
        return count;
    }

    private String generateMessageHash(String id, int messageNumber, String messageBody) {
        String firstTwo = id.length() >= 2 ? id.substring(0, 2) : id;
        String[] words = messageBody.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0].toUpperCase() : "";
        String lastWord = words.length > 1 ? words[words.length - 1].toUpperCase() : firstWord;
        return firstTwo + ":" + messageNumber + ":" + firstWord + lastWord;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MessageApp("test_user", 0, "test_user", "Hi Mike, can you join us for dinner tonight").setVisible(true));
    }

    String getMessageHash() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
