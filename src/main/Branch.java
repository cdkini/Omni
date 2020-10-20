package src.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Branch is a wrapper around a commit and represents an individual line of development.
 */
public class Branch implements Serializable {
    private String name;
    private Commit commit;

    /**
     * Sole constructor.
     *
     * @param name is the name assigned to the new branch.
     * @param commit is a pointer to the current commit where the branch begins.
     */
    public Branch(String name, Commit commit) {
        this.name = name;
        this.commit = commit;
    }

    /**
     * Converts a Branch into a stream of bytes and stores it in memory as a file.
     *
     * @param parent is the directory that your file is located in.
     */
    public void serialize(File parent) {
        File outFile = new File(parent, name);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outFile));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            throw new Error("Error when output serialized file.");
        }
    }

    /**
     * Takes the contents of a serialized file and converts it into an instance of a Branch.
     *
     * @param parent is the directory that your file is located in.
     * @param fileName is the name of the file you wish to deserialize.
     * @return a Branch instance serialized in the given path.
     */
    public static Branch deserialize(File parent, String fileName) {
        Branch branch;
        File inFile = new File(parent, fileName);
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inFile));
            branch = (Branch) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new Error("IO Error or Class Not Find");
        }
        return branch;
    }

    public String getName() {
        return name;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }
}
