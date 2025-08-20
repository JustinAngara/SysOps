package com.sysops.commands;

import com.sysops.main.Main;

public class CLICommunication {
    public enum CommandType { ARROW, EXCLAMATION, DOT, NORMAL }

    public static void processCommand(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            Main.g.appendOutput("Empty command");
            return;
        }

        CommandType type = getCommandType(input);
        String commandBody = extractCommand(input, type);
        String[] parts = commandBody.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

        CLICommand command = CommandRegistry.getCommand(commandName, type);
        if (command != null) {
            command.execute(args);
        } else {
            Main.g.appendOutput(getPrefix(type) + " Unknown command: " + commandName);
        }
    }

    private static CommandType getCommandType(String input) {
        if (input.startsWith(">")) return CommandType.ARROW;
        if (input.startsWith("!")) return CommandType.EXCLAMATION;
        if (input.startsWith(".")) return CommandType.DOT;
        return CommandType.NORMAL;
    }

    private static String extractCommand(String input, CommandType type) {
        return (type == CommandType.NORMAL) ? input : input.substring(1).trim();
    }

    private static String getPrefix(CommandType type) {
        switch (type) {
            case ARROW: return "[>]";
            case EXCLAMATION: return "[!]";
            case DOT: return "[.]";
            default: return "";
        }
    }
}