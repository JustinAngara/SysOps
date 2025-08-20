package com.sysops.commands;

import java.util.HashMap;
import java.util.Map;


public class CommandRegistry {
    private static final Map<String, CLICommand> arrowCommands = new HashMap<>();
    private static final Map<String, CLICommand> exclamationCommands = new HashMap<>();
    private static final Map<String, CLICommand> dotCommands = new HashMap<>();
    private static final Map<String, CLICommand> normalCommands = new HashMap<>();

    static {
        arrowCommands.put("stealth", new StealthCommand());
        arrowCommands.put("unstealth",new UnStealthCommand());
        arrowCommands.put("memorydump", new MemoryDumpCommand());

    }

    public static CLICommand getCommand(String name, CLICommunication.CommandType type) {
        switch (type) {
            case ARROW: return arrowCommands.get(name);
            case EXCLAMATION: return exclamationCommands.get(name);
            case DOT: return dotCommands.get(name);
            case NORMAL: return normalCommands.get(name);
            default: return null;
        }
    }
}