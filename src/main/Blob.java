package src.main;

import java.io.File;
import java.io.Serializable;

/**
 * Blob (or binary large object) is Omni's internal representation of a single file and its contents.
 */
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

    @Override
    public String getSHA1(File file) {
        return Utils.sha1(Utils.readContents(file));
    }
}