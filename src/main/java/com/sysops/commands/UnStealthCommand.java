package com.sysops.commands;

import com.sysops.main.Main;

public class UnStealthCommand implements CLICommand {
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            Main.g.appendOutput("[>] Usage: > unstealthstealth <name/pid>");
            return;
        }

        String target = args[0];
        try {
            int pid = Integer.parseInt(target);
//            com.sysops.main.Main.lib.applyStealthByPid(pid);
            Main.g.appendOutput("[>] STILL WORKING ON" + pid);
        } catch (NumberFormatException e) {
            String processName = target.replaceAll("\"", "");
            Main.lib.applyUnStealth(processName);
            Main.g.appendOutput("[>] UnStealth mode activated for: " + processName);
        }
    }
}
