package src.main;

import java.io.File;
import java.io.Serializable;

public abstract class OmniObject implements Serializable {
    private String ID;
    private static File DIR = new File(".omni");

    public void setID(Object... vals) {
        this.ID = Utils.sha1(vals);
    }

    public void serialize() {
        Utils.serialize(this, ID, new File(".omni"));
    }

    public static OmniObject deserialize(String fileName) {
        return (OmniObject) Utils.deserialize(fileName, DIR);
    }
}
