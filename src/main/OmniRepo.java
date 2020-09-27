package src.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class OmniRepo {
    private Stage stage;
    private String path;

    public OmniRepo(String path) {
        this.stage = new Stage();
        this.path = path;
    }

    /**
     * Initializes the .omni directory and its subdirectories and folders in order to store metadata about changes
     * within a given directory.
     *
     * @throws IOException if the .omni directory and its contents already exist.
     */
    public void init() throws IOException {
        if (Files.isDirectory(Paths.get(path, "/.omni/"))) {
            throw new FileAlreadyExistsException("Omni directory already initialized in "+
                    Paths.get(".").toAbsolutePath().normalize().toString());
        }

        Files.createDirectory(Paths.get(path, "/.omni/"));
        Files.createDirectory(Paths.get(path, "/.omni/objects/"));
        Files.createDirectory(Paths.get(path, "/.omni/branches/"));
        Files.createDirectory(Paths.get(path, "/.omni/refs/"));
        Files.createDirectory(Paths.get(path, "/.omni/refs/heads"));

        FileWriter fw = new FileWriter(path+"/.omni/HEAD");
        fw.write("ref: refs/heads/master\n");
        fw.close();

        System.out.println("Initialized empty Omni repository in "+System.getProperty("user.dir")+path);
    }

    /**
     * Adds a file to the staging area and serializes it the .omni/objects directory as a 40-char encrypted hash.
     *
     * @param fileName is the name of the file that's added to the staging area and serialized into a file.
     * @throws FileNotFoundException if the added file does not exist.
     */
    public void add(String fileName) throws FileNotFoundException {
        File file = new File(path, fileName);
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName() + " did not match any files in current repository");
        }

        File objectsDir = new File(path, "/.omni/objects");
        if (file.isDirectory()) {
            Tree tree = new Tree(file);
            for (OmniObject child: tree.getChildren()) {
                add(tree.getName()+"/"+child.getName());
            }
            tree.serialize(objectsDir, tree.getSHA1());
            stage.add(tree.getPath(), tree);
        } else {
            Blob blob = new Blob(file);
            blob.serialize(objectsDir, blob.getSHA1());
            stage.add(blob.getPath(), blob);
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

    private boolean isInitialized() {
        return Files.isDirectory(Paths.get(path, "/.omni/"));
    }

    private class Stage {
        private Map<String, OmniObject> contents;

        public Stage() {
            this.contents = new HashMap<>();
        }

        public void add(String fileName, OmniObject obj) {
            contents.put(fileName, obj);
        }

        public boolean isEmpty() {
            return contents.isEmpty();
        }
    }
}
