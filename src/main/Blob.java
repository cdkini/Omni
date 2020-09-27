package src.main;

import java.io.File;
import java.io.Serializable;

/**
 * Blob (or binary large object) is Omni's internal representation of a single file and its contents.
 */
public class Blob extends OmniObject implements Serializable {
    private File file;
    private byte[] contents;

    public Blob(File file) {
        this.file = file;
        this.contents = Utils.readContents(file);
    }

    @Override
    public String getSHA1() {
        return Utils.sha1(Utils.readContents(file));
    }

    @Override
    public String getPath() {
        return file.getAbsolutePath();
    }

    @Override
    public String getName() {
        return file.getName();
    }
}