package src.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write docstring!
 */
public class OmniRepo {
    private String path;
    private Stage stage;
    private final File mainDir;
    private final File objectsDir;

    /**
     * TODO: Write docstring!
     * @param path
     * @throws IOException
     */
    public OmniRepo(String path) throws IOException {
        this.path = path;
        if (Files.exists(Paths.get(path, ".omni/index"))) {
            this.stage = Stage.deserialize(path);
        } else {
            this.stage = new Stage(path);
        }
        this.mainDir = new File(path, "/.omni");
        this.objectsDir = new File(path, "/.omni/objects");
    }

    public List<OmniObject> getStagedFiles() {
        return stage.getContents();
    }

    /**
     * TODO: Write docstring!
     * @throws IOException
     */
    public void saveState() throws IOException {
        stage.serialize();
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
     * @param fileName is the name of the file that's added ArrayListto the staging area and serialized into a file.
     * @throws FileNotFoundException if the added file does not exist.
     */
    public void add(String fileName) throws IOException {
        File file = new File(path, fileName);
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName() + " did not match any files in current repository");
        }

        if (file.isDirectory()) {
            Tree tree = new Tree(file);
            for (OmniObject child: tree.getChildren()) {
                add(tree.getName()+"/"+child.getName());
            }
            tree.serialize(objectsDir, tree.getSHA1());
            stage.add(tree);
        } else {
            Blob blob = new Blob(file);
            blob.serialize(objectsDir, blob.getSHA1());
            stage.add(blob);
        }
    }

    /**
     * TODO: Write docstring!
     * @param message
     * @throws FileNotFoundException
     */
    public void commit(String message) throws IOException {
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        if (stage.isEmpty()) {
            throw new IllegalStateException("No changes added to commit (use 'omni add')");
        }
        String pwdPath = System.getProperty("user.dir") + path;
        new File(pwdPath).mkdirs();
        Tree root = new Tree(new File(pwdPath), stage.getContents());
        Commit commit = new Commit(root, stage.getHead(), message);

        root.serialize(objectsDir, root.getSHA1());
        commit.serialize(objectsDir, commit.getSHA1());
        stage.setHead(commit);
        stage.clear();
    }

    /**
     * TODO: Write docstring!
     * @param filename
     */
    public void rm(String filename) {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     */
    public void log() {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     */
    public void globalLog() {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     * @param msg
     */
    public void find(String msg) {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     */
    public void status() {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     * @param fileName
     */
    public void checkOutFile(String fileName) {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     * @param commmitID
     * @param filename
     */
    public void checkOutCommit(String commmitID, String filename) {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     * @param branchName
     */
    public void checkOutBranch(String branchName) {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     * @param branchName
     */
    public void branch(String branchName) {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     * @param branchName
     */
    public void rmBranch(String branchName) {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     * @param commitID
     */
    public void reset(String commitID) {
        // TODO: FILL IN
    }

    /**
     * TODO: Write docstring!
     * @param branchName
     */
    public void merge(String branchName) {
        // TODO: FILL IN
    }

    private boolean isInitialized() {
        return Files.isDirectory(Paths.get(path, "/.omni/"));
    }

    /**
     * TODO: Write docstring!
     */
    private static class Stage implements Serializable {
        private String path;
        private Commit head;
        private Commit branch;
        private List<OmniObject> contents;

        private Stage(String path) throws IOException {
            this.path = path;
            this.head = null;
            this.branch = null;
            this.contents = new ArrayList<>();
        }

        private void serialize() {
            File outFile = new File(path, "/.omni/index");
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outFile));
                oos.writeObject(this);
                oos.close();
            } catch (IOException e) {
                throw new Error("Error when output serialized file.");
            }
        }

        private static Stage deserialize(String path) {
            Stage stage;
            File inFile = new File(path, "/.omni/index");
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inFile));
                stage = (Stage) ois.readObject();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new Error("IO Error or Class Not Find");
            }
            return stage;
        }

        private void add(OmniObject obj) {
            contents.add(obj);
        }

        private Commit getHead() {
            return head;
        }

        private void setHead(Commit head) {
            this.head = head;
        }

        private boolean isEmpty() {
            return contents.isEmpty();
        }

        private List<OmniObject> getContents() {
            return contents;
        }

        private void clear() {
            contents.clear();
        }
    }
}
