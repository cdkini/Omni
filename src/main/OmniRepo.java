package src.main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class OmniRepo {
    private Stage stage;

    public OmniRepo() {
        this.stage = new Stage();
    }

    public boolean isInitialized() {
        return Files.isDirectory(Paths.get(".omni/"));
    }

    /**
     * Initializes the .omni directory and its subdirectories and folders in order to store metadata about changes
     * within a given directory.
     *
     * @param path is the relative path to initialize the .omni directory in (empty string defaults to pwd).
     * @throws IOException if the .omni directory and its contents already exist.
     */
    public void init(String path) throws IOException {
        if (Files.isDirectory(Paths.get(path+".omni/"))) {
            throw new FileAlreadyExistsException("Omni directory already initialized in "+
                    Paths.get(".").toAbsolutePath().normalize().toString());
        }

        Files.createDirectory(Paths.get(path+".omni/"));
        Files.createDirectory(Paths.get(path+".omni/objects/"));
        Files.createDirectory(Paths.get(path+".omni/branches/"));
        Files.createDirectory(Paths.get(path+".omni/refs/"));
        Files.createDirectory(Paths.get(path+".omni/refs/heads"));

        FileWriter fw = new FileWriter(path+".omni/HEAD");
        fw.write("ref: refs/heads/master\n");
        fw.close();

        System.out.println("Initialized empty Omni repository in "+
                Paths.get(".").toAbsolutePath().normalize().toString());
    }
    
    /**
     * Works the same as init(String) but defaults to the pwd.
     *
     * @see OmniRepo#init(String)
     */
    public void init() throws IOException {
        init("");
    }

    /**
     *
     * @param fileName
     */
    public void add(String fileName) {
        if (stage.isEmpty()) {
            throw new Error();
        }
    }

    public void commit(String msg) {
        // TODO: FILL IN
    }

    public void rm(String filename) {
        // TODO: FILL IN
    }

    public void log() {
        // TODO: FILL IN
    }

    public void globalLog() {
        // TODO: FILL IN
    }

    public void find(String msg) {
        // TODO: FILL IN
    }

    public void status() {
        // TODO: FILL IN
    }

    public void checkOutFile(String fileName) {
        // TODO: FILL IN
    }

    public void checkOutCommit(String commmitID, String filename) {
        // TODO: FILL IN
    }

    public void checkOutBranch(String branchName) {
        // TODO: FILL IN
    }

    public void branch(String branchName) {
        // TODO: FILL IN
    }

    public void rmBranch(String branchName) {
        // TODO: FILL IN
    }

    public void reset(String commitID) {
        // TODO: FILL IN
    }

    public void merge(String branchName) {
        // TODO: FILL IN
    }

    private class Stage {
        private ArrayList<OmniObject> contents;

        public Stage() {
            this.contents = new ArrayList<>();
        }

        public boolean isEmpty() {
            return contents.isEmpty();
        }
    }
}
