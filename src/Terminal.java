import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.DefaultCaret;
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

    private String prompt = System.getProperty("user.name") + "@" + System.getProperty("host.name") + " ~ $ ";

    public Terminal(String vfsPath, String logPath, String scriptPath) {
        this.vfsPath = vfsPath;
        this.logPath = logPath;
        this.scriptPath = scriptPath;

        setTitle("Terminal");
        setSize(800, 500);
        setUndecorated(true);
        this.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(50, 50 ,50), 10));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(30, 30, 30));
        titleBar.setPreferredSize(new Dimension(800, 40));

        JLabel titleLabel = new JLabel(" Terminal ", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setFont(new Font("Brass Mono", Font.BOLD, 20));
        titleBar.add(titleLabel, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Color bg = new Color(15, 15, 15);
        Color fg = new Color(200, 200, 200);
        Color promptColor = new Color(0, 200, 0);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(bg);
        outputArea.setForeground(fg);
        outputArea.setCaretColor(fg);
        outputArea.setFont(new Font("Brass Mono", Font.PLAIN, 24));
        DefaultCaret caret = (DefaultCaret) outputArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        promptLabel = new JLabel("user@this-pc ~ $ ");
        promptLabel.setFont(new Font("Brass Mono", Font.BOLD, 24));
        promptLabel.setBackground(bg);
        promptLabel.setForeground(promptColor);

        inputField = new JTextField();
        inputField.setFont(new Font("Brass Mono", Font.PLAIN, 24));
        inputField.setBackground(bg);
        inputField.setForeground(fg);
        inputField.setCaretColor(fg);
        inputField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(bg);
        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                inputField.setText("");
                handleCommand(input);
            }
        });


        this.setLayout(new BorderLayout());
        this.add(titleBar, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);

        runStartupScript();
    }

    private int handleCommand(String input) {
        if (input.isEmpty()) {
            return 0;
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
                outputArea.append(prompt + input + "\n");
                outputArea.append("Command: ls\nArgs: ");
                for (String arg : arguments) {
                    outputArea.append(arg + ' ');
                }
                outputArea.append("\n");
                logEvent(command, arguments, "Executed successfully", false);
                return 0;
            case "cd":
                outputArea.append(prompt + input + "\n");
                outputArea.append("Command: cd\nArgs: ");
                for (String arg : arguments) {
                    outputArea.append(arg + ' ');
                }
                outputArea.append("\n");
                logEvent(command, arguments, "Executed successfully", false);
                return 0;
            case "clear":
                outputArea.setText("");
                logEvent(command, arguments, "Screen cleared", false);
                return 0;
            case "echo":
                outputArea.append(prompt + input + "\n");
                try {
                    if (arguments.length > 0 && arguments[0].equals("$HOME")) {
                        arguments[0] = System.getProperty("user.home");
                    }
                    outputArea.append(arguments[0] + "\n");
                    logEvent(command, arguments, "Echo output", false);
                    return 0;
                }
                catch (Exception e) {
                    outputArea.append("Error is echo command");
                    logEvent(command, arguments, e.getMessage(), true);
                    return -1;
                }
            case "exit":
                logEvent(command, arguments, "Program exited", false);
                System.exit(0);
                return 0;

            default:
                outputArea.append(prompt + input + "\n");
                outputArea.append("Unknown command\n");
                logEvent(command, arguments, "Unknown command", true);
                return -1;
        }
    }

    private void logEvent(String command, String[] args, String message, boolean isError) {
        try (FileWriter fw = new FileWriter(logPath, true)) {
            String username = System.getProperty("user.name");
            fw.write("<event>\n");
            fw.write("\t<user>" + username + "</user>\n");
            fw.write("\t<command>" + command + "</command>\n");
            fw.write("\t<args>" + args + "</args>\n");
            fw.write("\t<message>" + message + "</message>\n");
            fw.write("\t<error>" + isError + "</error>\n");
            fw.write("\t<time>" + new Date() + "</time>\n");
            fw.write("</event>\n\n");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void runStartupScript() {
        try (BufferedReader br = new BufferedReader(new FileReader(scriptPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    if (handleCommand(line) != 0) {
                        break;
                    }
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