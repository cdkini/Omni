package src.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * OmniObject acts as the ABC for the key Omni object types: blobs, trees, and commits.
 * As such, it includes capabilities shared by all such objects, primarily serialization and deserialization.
 */
public abstract class OmniObject implements Serializable {
    /**
     * Converts an object into a stream of bytes and stores it in memory as a file.
     *
     * @param parent is the directory that your file is located in.
     * @param fileName is the name of the file you wish to deserialize.
     */
    public void serialize(File parent, String fileName) throws FileNotFoundException {
        File outFile = new File(parent, fileName);
        if (outFile.isDirectory()) {
            for (File file: outFile.listFiles()) {
                if (file.isDirectory()) {
                    Tree tree = new Tree(file);
                    tree.serialize(parent, tree.getSHA1());
                } else {
                    Blob blob = new Blob(file);
                    blob.serialize(parent, blob.getSHA1());
                }
            }
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outFile));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            throw new Error("Error when output serialized file.");
        }
    }

    /**
     * Takes the contents of a serialized file and converts it into an instance of an OmniObject.
     *
     * @param parent is the directory that your file is located in.
     * @param fileName is the name of the file you wish to deserialize.
     * @return either a blob, tree, or commit based on the retrieved encoding.
     */
    public static OmniObject deserialize(File parent, String fileName) {
        OmniObject obj;
        File inFile = new File(parent, fileName);
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inFile));
            obj = (OmniObject) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new Error("IO Error or Class Not Find");
        }
        return obj;
    }

    public String toString() {
        return getSHA1();
    }

    /**
     * Returns the SHA-1 hashcode associated with the OmniObject and its contents. For purposes of this project, we can
     * assume that collisions are not going to be present. While this is traditionally a 40-character string, we prepend
     * a 'B', 'T', or 'C' to the hashcode to differentiate between blobs, tree, and commits, respectively.
     *
     * @return SHA-1 encoding as a 41-character string.
     */
    public abstract String getSHA1();

    public abstract String getPath();

    public abstract String getName();
}
