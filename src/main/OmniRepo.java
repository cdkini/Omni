package src.main;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.stage = new Stage(path);
        this.mainDir = new File(path, "/.omni");
        this.objectsDir = new File(path, "/.omni/objects");
    }

    /**
     * TODO: Write docstring!
     * @throws IOException
     */
    public void saveState() throws IOException {
        stage.writeContentsToIndex();
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
    public void add(String fileName) throws FileNotFoundException {
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
            stage.add(tree.getPath(), tree);
        } else {
            Blob blob = new Blob(file);
            blob.serialize(objectsDir, blob.getSHA1());
            stage.add(blob.getPath(), blob);
        }
    }

    /**
     * TODO: Write docstring!
     * @param message
     * @throws FileNotFoundException
     */
    public void commit(String message) throws FileNotFoundException {
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
    private class Stage {
        private String path;
        private Commit head;
        private Commit branch;
        private Map<String, OmniObject> contents;

        private Stage(String path) throws IOException {
            this.path = path;
            this.contents = new HashMap<>();
            if (Files.exists(Paths.get(path, ".omni/index"))) {
                JSONObject indexContents = readIndexToJSON();
                if (indexContents.get("head") != JSONObject.NULL) {
                    this.head = (Commit) OmniObject.deserialize(new File(path), indexContents.getString("head"));
                }
                if (indexContents.get("branch") != JSONObject.NULL) {
                    this.branch = (Commit) OmniObject.deserialize(new File(path), indexContents.getString("branch"));
                }
//                this.contents = Utils.toMap(indexContents.getJSONObject("contents"));
            }
        }

        private JSONObject readIndexToJSON() throws IOException {
            String content = new String(Files.readAllBytes(Paths.get(path, ".omni/index")));
            return new JSONObject(content);
        }

        private void writeContentsToIndex() throws IOException {
            JSONObject obj = new JSONObject();
            obj.put("head", head == null ? JSONObject.NULL : head);
            obj.put("branch", branch == null ? JSONObject.NULL : branch);
            obj.put("contents", contents);

            File index = new File(path,".omni/index");
            FileWriter fw = new FileWriter(index);
            fw.write(obj.toString());
            fw.flush();
            fw.close();
        }

        private void add(String fileName, OmniObject obj) {
            contents.put(fileName, obj);
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

        private List getContents() {
            return new ArrayList<>(contents.values());
        }
    }
}
