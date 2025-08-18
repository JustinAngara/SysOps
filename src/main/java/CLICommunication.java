public class CLICommunication {

    public static void processCommand(String input) {
        input = input.trim();

        if (input.isEmpty()) {
            Main.g.appendOutput("Empty command");
            return;
        }

        // Determine command type and extract command
        CommandType type = getCommandType(input);
        String command = extractCommand(input, type);
        String[] parts = command.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();

        // Route to appropriate handler based on type
        switch (type) {
            case ARROW:
                handleArrowCommand(commandName, parts);
                break;
            case EXCLAMATION:
                handleExclamationCommand(commandName, parts);
                break;
            case DOT:
                handleDotCommand(commandName, parts);
                break;
            case NORMAL:
                handleNormalCommand(commandName, parts);
                break;
        }
    }

    private static CommandType getCommandType(String input) {
        if (input.startsWith(">")) return CommandType.ARROW;
        if (input.startsWith("!")) return CommandType.EXCLAMATION;
        if (input.startsWith(".")) return CommandType.DOT;
        return CommandType.NORMAL;
    }

    private static String extractCommand(String input, CommandType type) {
        switch (type) {
            case ARROW:
            case EXCLAMATION:
            case DOT:
                return input.substring(1).trim();
            case NORMAL:
            default:
                return input;
        }
    }

    private static void handleArrowCommand(String commandName, String[] parts) {
        switch (commandName) {
            case "stealth":
                handleStealthCommand(parts);
                break;
            default:
                Main.g.appendOutput("[>] Unknown command: " + commandName);
                break;
        }
    }

    private static void handleExclamationCommand(String commandName, String[] parts) {
        switch (commandName) {
            case "important":
                handleImportantCommand(parts);
                break;
            default:
                Main.g.appendOutput("[!] Unknown command: " + commandName);
                break;
        }
    }

    private static void handleDotCommand(String commandName, String[] parts) {
        switch (commandName) {
            case "help":
                handleHelpCommand(parts);
                break;
            default:
                Main.g.appendOutput("[.] Unknown command: " + commandName);
                break;
        }
    }

    private static void handleNormalCommand(String commandName, String[] parts) {
        switch (commandName) {
            case "hello":
                handleHelloCommand(parts);
                break;
            default:
                Main.g.appendOutput("Unknown command: " + commandName);
                break;
        }
    }

    // Arrow Commands (>)
    private static void handleStealthCommand(String[] parts) {
        if (parts.length < 2) {
            Main.g.appendOutput("[>] Usage: > stealth <name/pid>");
            return;
        }

        String target = parts[1];

        // Try to parse as integer (PID)
        try {
            int pid = Integer.parseInt(target);
            Main.lib.applyStealthByPid(pid);
            Main.g.appendOutput("[>] Stealth mode activated for PID: " + pid);
        } catch (NumberFormatException e) {
            // Treat as process name
            String processName = target.replaceAll("\"", "");
            Main.lib.applyStealth(processName);
            Main.g.appendOutput("[>] Stealth mode activated for: " + processName);
        }

    }

    // Exclamation Commands (!)
    private static void handleImportantCommand(String[] parts) {
        if (parts.length < 2) {
            Main.g.appendOutput("[!] Usage: ! important <action>");
            return;
        }
        String action = parts[1];
        Main.g.appendOutput("[!] Important action: " + action);
        // TODO: Implement functionality
    }

    // Dot Commands (.)
    private static void handleHelpCommand(String[] parts) {
        Main.g.appendOutput("[.] Available commands:");
        Main.g.appendOutputRaw("\n  Arrow (>): stealth");
        Main.g.appendOutputRaw("\n  Exclamation (!): important");
        Main.g.appendOutputRaw("\n  Dot (.): help");
        Main.g.appendOutputRaw("\n  Normal: hello");
    }

    // Normal Commands (no prefix)
    private static void handleHelloCommand(String[] parts) {
        Main.g.appendOutput("Hello! System is ready.");
    }

    // Command type enum
    private enum CommandType {
        ARROW,         // >
        EXCLAMATION,   // !
        DOT,           // .
        NORMAL         // no prefix
    }
}