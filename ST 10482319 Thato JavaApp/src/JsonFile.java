
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class JsonFile {
    private void readMessagesFromJson() {
    StringBuilder output = new StringBuilder("[\n");
        String JSON_FILE = "Message.Json";
    try (BufferedReader br = new BufferedReader(new FileReader(JSON_FILE))) {
        String line;
        while ((line = br.readLine()) != null) {
            output.append("  ").append(line).append("\n");
        }
    } catch (IOException e) {
        output.append("  No JSON messages found.\n");
    }
    output.append("]");

    JTextArea textArea = new JTextArea(output.toString());
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(580, 300));
    JOptionPane.showMessageDialog(this, scrollPane, "JSON Messages", JOptionPane.INFORMATION_MESSAGE);
}

    
}
