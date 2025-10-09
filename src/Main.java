public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Provide necessary parameters");
            System.exit(1);
        }

        String vfsPath = args[0];
        String logPath = args[1];
        String scriptPath = args[2];

        System.out.println("=== DEBUG: Application Parameters ===");
        System.out.println("VFS Path: " + vfsPath);
        System.out.println("Log File Path: " + logPath);
        System.out.println("Startup Script Path: " + scriptPath);
        System.out.println("======================================");

        Terminal terminal = new Terminal(vfsPath, logPath, scriptPath);
        terminal.setLocationRelativeTo(null);
        terminal.setVisible(true);
        terminal.setFocus();
    }
}