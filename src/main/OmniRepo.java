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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Write docstring!
 */
public class OmniRepo {
    private String path;
    private Stage stage;

    /**
     * TODO: Write docstring!
     *
     * @param path
     * @throws IOException
     */
    public OmniRepo(String path) throws IOException {
        this.path = path;
        if (Files.exists(Paths.get(path, ".omni/index"))) {
            this.stage = Stage.deserialize(path);
        }
    }

    public Commit getHead() {
        return stage.head;
    }

    public List<String> getTrackedFiles() {
        return stage.head.getTracked();
    }

    public List<String> getStagedFiles() {
        return new ArrayList<>(stage.contents.keySet());
    }

    public List<OmniObject> getStagedObjects() {
        return new ArrayList<>(stage.contents.values());
    }

    /**
     * TODO: Write docstring!
     *
     */
    public void saveState() {
        stage.branch.serialize(new File(String.valueOf(Paths.get(path, "/.omni/branches/"))));
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
            throw new FileAlreadyExistsException("Omni directory already initialized in " +
                    Paths.get(".").toAbsolutePath().normalize().toString());
        }

        Files.createDirectory(Paths.get(path, "/.omni/"));
        Files.createDirectory(Paths.get(path, "/.omni/objects/"));
        Files.createDirectory(Paths.get(path, "/.omni/branches/"));
        Files.createDirectory(Paths.get(path, "/.omni/refs/"));
        Files.createDirectory(Paths.get(path, "/.omni/refs/heads"));

        FileWriter fw = new FileWriter(path + "/.omni/HEAD");
        fw.write("ref: refs/heads/master\n");
        fw.close();

        String pwdPath = System.getProperty("user.dir") + path;
        new File(pwdPath).mkdirs();

        Tree root = new Tree(new File(pwdPath), new ArrayList<>());
        Commit head = new Commit(root, null, "Initial commit", null);
        Branch master = new Branch("master", head);

        root.serialize(new File(path, "/.omni/objects"), root.getSHA1());
        head.serialize(new File(path, "/.omni/objects"), head.getSHA1());
        master.serialize(new File(String.valueOf(Paths.get(path, "/.omni/branches"))));

        stage = new Stage(path, head, master);
        System.out.println("Initialized empty Omni repository in " + System.getProperty("user.dir") + path);
    }

    /**
     * Adds a file to the staging area and serializes it the .omni/objects directory as a 40-char encrypted hash.
     * file.getAbsolutePath()
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
            for (OmniObject child : tree.getChildren()) {
                add(tree.getName() + "/" + child.getName());
            }
            tree.serialize(new File(path, "/.omni/objects"), tree.getSHA1());
            stage.contents.put(tree.getPath(), tree);
        } else {
            Blob blob = new Blob(file);
            blob.serialize(new File(path, "/.omni/objects"), blob.getSHA1());
            stage.contents.put(blob.getPath(), blob);
        }
    }

    /**
     * TODO: Write docstring!
     * @param message
     * @throws FileNotFoundException
     */
    public Commit commit(String message) throws IOException {
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        if (stage.contents.isEmpty()) {
            throw new IllegalStateException("No changes added to commit (use 'omni add')");
        }
        String pwdPath = System.getProperty("user.dir") + path;
        new File(pwdPath).mkdirs();

        Tree root = new Tree(new File(pwdPath), getStagedObjects());
        Commit commit = new Commit(root, stage.head, message, getStagedFiles());

        root.serialize(new File(path, "/.omni/objects"), root.getSHA1());
        commit.serialize(new File(path, "/.omni/objects"), commit.getSHA1());

        if (!commit.getSHA1().equals(stage.head.getSHA1())) {
            stage.head = commit;
            stage.branch.setCommit(commit);
        }
        stage.contents.clear();

        return commit;
    }

    /**
     * TODO: Write docstring!
     *
     * @param fileName
     */
    public void rm(String fileName) throws FileNotFoundException {
        File file = new File(path, fileName);
        String filePath = file.getAbsolutePath();
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName() + " did not match any files in current repository");
        }

        List<String> stagedFiles = getStagedFiles();
        List<String> tracked = stage.head.getTracked();
        if (stagedFiles != null && stagedFiles.contains(filePath)) {
            stage.contents.remove(filePath);
            System.out.println("Unstaged "+filePath);
        } else if (tracked != null && tracked.contains(filePath)) {
            stage.head.removeFromTracked(filePath);
            deleteFile(new File(filePath));
            System.out.println("Rm "+filePath);
        } else {
            throw new IllegalStateException("Cannot rm unstaged and untracked file");
        }
    }

    private boolean deleteFile(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                return deleteFile(new File(file, children[i]));
            }
        }
        return file.delete();
    }

    /**
     * TODO: Write docstring!
     */
    public void log() throws FileNotFoundException {
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        Commit curr = stage.head;
        while (curr != null) {
            logCommitContents(curr);
            curr = curr.getParent();
        }
    }

    /**
     * TODO: Write docstring!
     */
    public void globalLog() throws FileNotFoundException {
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        File branchesDir = new File(path, "/.omni/branches");
        for (File branchFile: branchesDir.listFiles()) {
            Branch branch = Branch.deserialize(branchesDir, branchFile.getName());
            Commit curr = branch.getCommit();
            while (curr != null) {
                logCommitContents(curr);
                curr = curr.getParent();
            }
        }
    }

    private void logCommitContents(Commit commit) {
        System.out.println("===");
        System.out.println("commit "+commit.getSHA1());
        System.out.println("Date: "+commit.getDateTime());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /**
     * TODO: Write docstring!
     * @param msg
     */
    public int find(String msg) throws FileNotFoundException {
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        int count = 0;
        File branchesDir = new File(path, "/.omni/branches");
        for (File branchFile: branchesDir.listFiles()) {
            Branch branch = Branch.deserialize(branchesDir, branchFile.getName());
            Commit curr = branch.getCommit();
            while (curr != null) {
                if (curr.getMessage().equals(msg)) {
                    System.out.println(curr.getSHA1());
                    count++;
                }
                curr = curr.getParent();
            }
        }
        if (count == 0) {
            throw new IllegalArgumentException("Found no commit with that message");
        }
        return count;
    }

    /**
     * TODO: Write docstring!
     */
    public void status() throws FileNotFoundException {
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        printBranches();
        printStagedFiles();
        printRemovedFiles();
        printUnstagedModifications();
        printUntrackedFiles();
    }

    private void printBranches() {
        System.out.println("=== Branches ===");
        System.out.println();
    }

    private void printStagedFiles() {
        System.out.println("=== Staged Files ===");
        for (String fileName: stage.contents.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    private void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        System.out.println("");
    }

    private void printUnstagedModifications() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println("");
    }

    private void printUntrackedFiles() {
        System.out.println("=== Untracked Files ===");
        System.out.println("");
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
    public void branch(String branchName) throws FileNotFoundException, FileAlreadyExistsException {
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        File branchesDir = new File(path, "/.omni/branches");
        for (File branchFile: branchesDir.listFiles()) {
            if (branchName.equals(branchFile.getName())) {
                throw new FileAlreadyExistsException("A branch with that name already exists");
            }
        }
        Branch newBranch = new Branch(branchName, stage.head);
        newBranch.serialize(new File(String.valueOf(Paths.get(path, "/.omni/branches"))));
    }

    /**
     * TODO: Write docstring!
     * @param branchName
     */
    public void rmBranch(String branchName) throws FileNotFoundException {
        if (!isInitialized()) {
            throw new FileNotFoundException("Omni directory not initialized");
        }
        if (branchName.equals("master")) {
            throw new IllegalArgumentException("Cannot rm master branch");
        }
        if (branchName.equals(stage.branch.getName())) {
            throw new IllegalArgumentException("Cannot rm current branch");
        }
        File branchFile = new File(String.valueOf(Paths.get(path, "/.omni/branches/", branchName)));
        if (branchFile.delete()) {
            System.out.println("Rm "+branchName);
        } else {
            throw new FileNotFoundException("A branch with that name does not exist");
        }
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
        return stage != null;
    }

    /**
     * TODO: Write docstring!
     */
    private static class Stage implements Serializable {
        private String path;
        private Commit head;
        private Branch branch;
        private Map<String, OmniObject> contents;

        private Stage(String path, Commit head, Branch branch) {
            this.path = path;
            this.head = head;
            this.branch = branch;
            this.contents = new HashMap<>();
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
    }
}
