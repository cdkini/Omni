package src.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Command {
    public static void init() throws IOException {
        if (Files.isDirectory(Paths.get(".omni/"))) {
            throw new FileAlreadyExistsException("Omni directory already initialized in "+
                    Paths.get(".").toAbsolutePath().normalize().toString());
        }

        // Set up skeleton of .omni directory and subdirectories to store data on pwd
        Files.createDirectory(Paths.get(".omni/"));
        Files.createDirectory(Paths.get(".omni/objects/"));
        Files.createDirectory(Paths.get(".omni/branches/"));
        Files.createDirectory(Paths.get(".omni/refs/"));
        Files.createFile(Paths.get(".omni/HEAD"));

        System.out.println("Initialized empty Omni repository in "+
                Paths.get(".").toAbsolutePath().normalize().toString());
    }

    public static void add(String filename) {
        // TODO: FILL IN
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

