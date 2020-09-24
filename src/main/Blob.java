package src.main;

import java.io.File;
import java.io.Serializable;

public class Blob extends OmniObject implements Serializable {
    private String fileName;
    private byte[] contents;

    public Blob(File file) {
        this.contents = Utils.readContents(file);
        this.fileName = file.getName();
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContents() {
        return contents;
    }
}