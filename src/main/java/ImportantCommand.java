public class ImportantCommand implements CLICommand {
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            Main.g.appendOutput("[!] Usage: ! important <action>");
            return;
        }
        String action = String.join(" ", args);
        Main.g.appendOutput("[!] Important action: " + action);
        // TODO: Implement functionality
    }
}