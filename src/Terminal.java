import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Terminal extends JFrame {
    private final String vfsPath;
    private final String logPath;
    private final String scriptPath;

    private final JTextArea outputArea;
    private final JTextField inputField;
    private final JLabel promptLabel;

    public Terminal(String vfsPath, String logPath, String scriptPath) {
        this.vfsPath = vfsPath;
        this.logPath = logPath;
        this.scriptPath = scriptPath;

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

        runStartupScript();
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
        } else {
            command = input.substring(0, spaceIndex);
            arguments = input.substring(spaceIndex + 1).split("\\s+");
        }

        switch (command) {
            case "ls":
                outputArea.setText("Command: ls\nArgs: ");
                for (String arg : arguments) {
                    outputArea.append(arg + ' ');
                }
                logEvent(command, arguments, "Executed successfully", false);
                break;
            case "cd":
                outputArea.setText("Command: cd\nArgs: ");
                for (String arg : arguments) {
                    outputArea.append(arg + ' ');
                }
                logEvent(command, arguments, "Executed successfully", false);
                break;
            case "clear":
                outputArea.setText("");
                logEvent(command, arguments, "Screen cleared", false);
                break;
            case "echo":
                try {
                    if (arguments.length > 0 && arguments[0].equals("$HOME")) {
                        arguments[0] = System.getProperty("user.home");
                    }
                    outputArea.setText(arguments[0]);
                    logEvent(command, arguments, "Echo output", false);
                }
                catch (Exception e) {
                    outputArea.setText("Error is echo command");
                    logEvent(command, arguments, e.getMessage(), true);
                }
                break;
            case "exit":
                logEvent(command, arguments, "Program exited", false);
                System.exit(0);
                break;

            default:
                outputArea.setText("Unknown command");
                logEvent(command, arguments, "Unknown command", true);
        }
    }

    private void logEvent(String command, String[] args, String message, boolean isError) {
        try (FileWriter fw = new FileWriter(logPath, true)) {
            String username = System.getProperty("user.name");
            fw.write("<event>\n");
            fw.write("<user>" + username + "</user>\n");
            fw.write("<command>" + command + "</command>\n");
            fw.write("<args>" + args + "</args>\n");
            fw.write("<message>" + message + "</message>\n");
            fw.write("<error>" + isError + "</error>\n");
            fw.write("<time>" + new Date() + "</time>\n");
            fw.write("</event>");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void runStartupScript() {
        try (BufferedReader br = new BufferedReader(new FileReader(scriptPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                outputArea.append(promptLabel.getText() + line + "\n");
                try {
                    handleCommand(line);
                }
                catch (Exception e) {
                    outputArea.append("Error executing: " + line + "\n");
                    logEvent("script", new String[]{line}, e.getMessage(), true);
                    break;
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void setFocus() {
        this.inputField.requestFocusInWindow();
    }
}