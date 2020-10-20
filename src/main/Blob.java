package src.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Blob (or binary large object) is Omni's internal representation of a single file and its contents.
 */
public class Blob extends OmniObject implements Serializable {
    private final File file;
    private final byte[] contents;
    private final String sha1;

    /**
     * Sole constructor.
     *
     * @param file contains the contents to be serialized in the instance.
     */
    public Blob(File file) throws FileNotFoundException {
        if (!Files.exists(Paths.get(file.getAbsolutePath()))) {
            throw new FileNotFoundException(file.getName() + " did not match any files in current repository");
        }
        this.file = file;
        this.contents = Utils.readContents(file);
        this.sha1 = "B"+Utils.sha1((Object) Utils.readContents(file));
    }

    @Override
    public String getSHA1() {
        return sha1;
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