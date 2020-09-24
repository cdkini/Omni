package src.main;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No valid command passed.");
        }
        try {
            switch (args[0]) { // Command
                case "init":
                    Command.init();
                case "add":
                    Command.add(args[1]);
                case "commit":
                    Command.commit(args[1]);
                case "rm":
                    Command.rm(args[1]);
                case "log":
                    Command.log();
                case "global-log":
                    Command.globalLog();
                case "find":
                    Command.find(args[1]);
                case "status":
                    Command.status();
                case "checkout":
                    // TODO: Implement!
                case "branch":
                    Command.branch(args[1]);
                case "rm-branch":
                    Command.rmBranch(args[1]);
                case "reset":
                    Command.reset(args[1]);
                case "merge":
                    Command.merge(args[1]);
                default:
                    throw new IllegalArgumentException("No valid command passed.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
