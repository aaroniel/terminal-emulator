import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.print("user@this-pc -> ");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                continue;
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
                case "cd":
                    System.out.println("Command: " + command);
                    System.out.print("Args: ");
                    for (String arg : arguments) {
                        System.out.print(arg + ' ');
                    }
                    System.out.println();
                    break;

                case "exit":
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid command");
            }
        }
        scanner.close();
    }
}