package src.main;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            throw new IllegalArgumentException("No valid command passed.");
        }
        OmniRepo repo = new OmniRepo();
        if (!repo.isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        try {
            switch (args[0]) {
                case "init":
                    repo.init();
                case "add":
                    repo.add(args[1]);
                case "commit":
                    repo.commit(args[1]);
                case "rm":
                    repo.rm(args[1]);
                case "log":
                    repo.log();
                case "global-log":
                    repo.globalLog();
                case "find":
                    repo.find(args[1]);
                case "status":
                    repo.status();
                case "checkout":
                    // TODO: Implement!
                case "branch":
                    repo.branch(args[1]);
                case "rm-branch":
                    repo.rmBranch(args[1]);
                case "reset":
                    repo.reset(args[1]);
                case "merge":
                    repo.merge(args[1]);
                default:
                    throw new IllegalArgumentException("No valid command passed.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
