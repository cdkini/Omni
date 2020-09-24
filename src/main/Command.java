package src.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Command {
    /**
     * Initializes the .omni directory and its subdirectories and folders in order to store metadata about changes
     * within a given directory.
     *
     * @param path is the relative path to initialize the .omni directory in (empty string defaults to pwd).
     * @throws IOException if the .omni directory and its contents already exists.
     */
    public static void init(String path) throws IOException {
        if (Files.isDirectory(Paths.get(path+".omni/"))) {
            throw new FileAlreadyExistsException("Omni directory already initialized in "+
                    Paths.get(".").toAbsolutePath().normalize().toString());
        }

        Files.createDirectory(Paths.get(path+".omni/"));
        Files.createDirectory(Paths.get(path+".omni/objects/"));
        Files.createDirectory(Paths.get(path+".omni/branches/"));
        Files.createDirectory(Paths.get(path+".omni/refs/"));
        Files.createDirectory(Paths.get(path+".omni/refs/heads"));
        Files.createFile(Paths.get(path+".omni/HEAD"));
        // TODO: Write "ref: refs/heads/master" to HEAD

        System.out.println("Initialized empty Omni repository in "+
                Paths.get(".").toAbsolutePath().normalize().toString());
    }

    public static void add(String filename) {

    }

    public static void commit(String msg) {
        // TODO: FILL IN
    }

    public static void rm(String filename) {
        // TODO: FILL IN
    }

    public static void log() {
        // TODO: FILL IN
    }

    public static void globalLog() {
        // TODO: FILL IN
    }

    public static void find(String msg) {
        // TODO: FILL IN
    }

    public static void status() {
        // TODO: FILL IN
    }

    public static void checkOutFile(String fileName) {
        // TODO: FILL IN
    }

    public static void checkOutCommit(String commmitID, String filename) {
        // TODO: FILL IN
    }

    public static void checkOutBranch(String branchName) {
        // TODO: FILL IN
    }

    public static void branch(String branchName) {
        // TODO: FILL IN
    }

    public static void rmBranch(String branchName) {
        // TODO: FILL IN
    }

    public static void reset(String commitID) {
        // TODO: FILL IN
    }

    public static void merge(String branchName) {
        // TODO: FILL IN
    }
}

