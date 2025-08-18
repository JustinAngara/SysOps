public class CLICommunication {

    public static void processCommand(String input) {
        // Remove leading > if present
        if (input.startsWith(">")) {
            input = input.substring(1).trim();
        }

        String[] parts = input.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();

        switch (command) {
            case "stealth":
                handleStealthCommand(parts);
                break;
            default:
                Main.g.appendOutput("Unknown command: " + command);
                break;
        }
    }

    private static void handleStealthCommand(String[] parts) {
        if (parts.length < 2) {
            Main.g.appendOutput("Usage: stealth \"process.exe\"");
            return;
        }

        String processName = parts[1].replaceAll("\"", "");
        Main.lib.applyStealth(processName);
        Main.g.appendOutput("Stealth mode activated for: " + processName);

        // TODO: Implement actual stealth functionality
    }
}