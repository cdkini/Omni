package src.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * TODO: Updated javadoc!
 */
public abstract class OmniObject implements Serializable {
    /**
     * TODO: Update javadoc!
     * @param parent
     * @param fileName
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
     * TODO: Update javadoc!
     * @param fileName
     */
    public void serialize(String fileName) {
        serialize(new File(".omni/objects"), fileName);
    }

    /**
     * TODO: Update javadoc!
     * @param fileName
     * @param parent
     * @return
     */
    public static OmniObject deserialize(String fileName, File parent) {
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
     * TODO: Update javadoc!
     * @param fileName
     * @return
     */
    public static OmniObject deserialize(String fileName) {
        return deserialize(fileName, new File(".omni/objects"));
    }
}
