package com.sysops.commands;

import com.sysops.main.Main;

public class StealthCommand implements CLICommand {
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            Main.g.appendOutput("[>] Usage: > stealth <name/pid>");
            return;
        }

        String target = args[0];
        try {
            int pid = Integer.parseInt(target);
            Main.lib.applyStealthByPid(pid);
            Main.g.appendOutput("[>] Stealth mode activated for PID: " + pid);
        } catch (NumberFormatException e) {
            String processName = target.replaceAll("\"", "");
            Main.lib.applyStealth(processName);
            Main.g.appendOutput("[>] Stealth mode activated for: " + processName);
        }
    }
}