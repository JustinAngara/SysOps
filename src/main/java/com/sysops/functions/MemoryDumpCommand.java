package com.sysops.functions;

import com.sysops.commands.CLICommand;
import com.sysops.main.Main;

public class MemoryDumpCommand implements CLICommand {
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            Main.g.appendOutput("[>] Usage: ! important <action>");
            return;
        }
        String action = String.join(" ", args);
        Main.g.appendOutput("[>] Extracted process: " + action);

        // this is the process target
        String target = args[0];

        String processName = target.replaceAll("\"", "");

        // load memory dump here

        Main.g.appendOutput("[>] Performing memory dump on "+processName);


    }
}