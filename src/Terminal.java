import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Terminal extends JFrame {
    private final JTextArea outputArea;
    private final JTextField inputField;
    private final JLabel promptLabel;

    public Terminal() {
        setTitle("Terminal");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 22));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        promptLabel = new JLabel("user@this-pc ~ -> ");
        promptLabel.setFont(new Font("Monospaced", Font.PLAIN, 22));

        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 22));
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                inputField.setText("");
                handleCommand(input);
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    private void handleCommand(String input) {
        if (input.isEmpty()) {
            return;
        }
        String command;
        String[] arguments;

        int spaceIndex = input.indexOf(' ');
        if (spaceIndex == -1) {
            command = input;
            arguments = new String[0];
        }
        else {
            command = input.substring(0, spaceIndex);
            arguments = input.substring(spaceIndex + 1).split("\\s+");
        }

        switch (command) {
            case "ls":
                outputArea.setText("Command: ls\nArgs: ");
                for (String arg : arguments) {
                    outputArea.append(arg + ' ');
                }
                break;
            case "cd":
                outputArea.setText("Command: cd\nArgs: ");
                for (String arg : arguments) {
                    outputArea.append(arg + ' ');
                }
                break;
            case "clear":
                outputArea.setText("");
                break;
            case "echo":
                if (arguments[0].equals("$HOME")) {
                    arguments[0] = System.getProperty("user.home");
                }
                outputArea.setText(arguments[0]);
                break;
            case "exit":
                System.exit(0);
                break;

            default:
                outputArea.setText("Unknown command");
        }
    }

    public void setFocus() {
        this.inputField.requestFocusInWindow();
    }
}