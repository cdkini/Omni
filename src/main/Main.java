package src.main;

import java.io.IOException;

/**
 * Main brings together the classes and objects from the rest of project, allowing user inputs to be read and evaluated.
 */
public class Main {
    /**
     * Takes in user input and determines the appropriate command to run. An OmniObject is instantiated during program
     * execution and is serialized to the .omni directory at program completion.
     *
     * @param args are the arguments passed by the user in the CLI.
     * @throws IOException if the args are invalid or if there is any other issue during program execution.
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("No valid command passed.");
        }
        OmniRepo omniRepo = new OmniRepo(System.getProperty("user.dir"));
        try {
            switch (args[0]) {
                case "init":
                    omniRepo.init();
                    break;
                case "add":
                    omniRepo.add(args[1]);
                    break;
                case "commit":
                    omniRepo.commit(args[1]);
                    break;
                case "rm":
                    omniRepo.rm(args[1]);
                    break;
                case "log":
                    omniRepo.log();
                    break;
                case "global-log":
                    omniRepo.globalLog();
                    break;
                case "find":
                    omniRepo.find(args[1]);
                    break;
                case "status":
                    omniRepo.status();
                    break;
                case "checkout":
                    // TODO: Implement!
                    break;
                case "branch":
                    omniRepo.branch(args[1]);
                    break;
                case "rm-branch":
                    omniRepo.rmBranch(args[1]);
                    break;
                case "reset":
                    omniRepo.reset(args[1]);
                    break;
                case "merge":
                    omniRepo.merge(args[1]);
                    break;
                default:
                    throw new IllegalArgumentException("No valid command passed.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        omniRepo.saveState();
    }
}
