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
    private File file;
    private byte[] contents;

    /**
     * TODO: Write docstring!
     * @param file
     */
    public Blob(File file) throws FileNotFoundException {
        if (!Files.exists(Paths.get(file.getAbsolutePath()))) {
            throw new FileNotFoundException(file.getName() + " did not match any files in current repository");
        }
        this.file = file;
        this.contents = Utils.readContents(file);
    }

    /**
     * TODO: Write docstring!
     * @return
     */
    @Override
    public String getSHA1() {
        return "B"+Utils.sha1(Utils.readContents(file));
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