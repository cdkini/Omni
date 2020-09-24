package src.main;

import java.io.*;

public abstract class OmniObject implements Serializable {
    private String ID;
    private static File DIR = new File(".omni");

    /**
     * TODO: Update javadoc!
     * @param vals
     */
    public void setID(Object... vals) {
        this.ID = Utils.sha1(vals);
    }

    /**
     * TODO: Update javadoc!
     * @param parent
     * @param fileName
     */
    public void serialize(File parent, String fileName) {
        File outFile = new File(parent, fileName);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            throw new Error("Error when output serialized file.");
        }
    }

    /**
     * TODO: Update javadoc!
     * @param fileName
     */
    public void serialize(String fileName) {
        serialize(DIR, fileName);
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
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
            obj = (OmniObject) inp.readObject();
            inp.close();
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
        return deserialize(fileName, DIR);
    }
}
