package src.main;

import java.io.File;
import java.io.FileInputStream;
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
     * Serialize converts an object into a stream of bytes and stores it in memory as a file.
     *
     * @param parent is the directory that your file is located in.
     * @param fileName is the name of the file you wish to deserialize.
     */
    public void serialize(File parent, String fileName) {
        File outFile = new File(parent, fileName);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outFile));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            throw new Error("Error when output serialized file.");
        }
    }

    /**
     * Works the same as OmniObject.serialize(File, String) but the parent directory defaults to ".omni/objects".
     *
     * @see OmniObject#serialize(File, String)
     */
    public void serialize(String fileName) {
        serialize(new File(".omni/objects"), fileName);
    }

    /**
     * Deserialize takes the contents of a serialized file and converts it into an instance of an OmniObject.
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

    /**
     * Works the same as OmniObject.deserialize(File, String) but the parent directory defaults to ".omni/objects".
     *
     * @see OmniObject#deserialize(String)
     */
    public static OmniObject deserialize(String fileName) {
        return deserialize(new File(".omni/objects"), fileName);
    }

    public abstract String getSHA1(File file);
}
