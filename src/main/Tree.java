package src.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Tree is Omni's internal representation of a directory and its contents. A single tree can contain pointers to
 * multiple other trees and blobs, creating a tree or graph-like structure used to define a repository's version
 * control history.
 */
public class Tree extends OmniObject implements Serializable {
    private File dir;
    private List<OmniObject> children;
    private String sha1;

    /**
     * Default constructor.
     *
     * @param dir contains the contents to be serialized in the instance.
     */
    public Tree(File dir) throws FileNotFoundException {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("File argument must be a directory");
        }
        this.dir = dir;
        this.children = new ArrayList<OmniObject>();
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) {
                this.children.add(new Tree(file));
            } else {
                this.children.add(new Blob(file));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append('T');
        for (OmniObject obj: children) {
            sb.append(obj.getSHA1());
        }
        sha1 = sb.toString();
    }

    /**
     * Constructor used during OmniRepo instantiation or committing to represent the overall repository.
     * Similar to {@link Tree#Tree(File)} but allows children to be provided as an argument.
     *
     * @param dir contains the contents to be serialized in the instance.
     * @param objs is a list of children to be associated with the instance.
     *
     * @see OmniRepo#init()
     * @see OmniRepo#commit(String)
     */
    public Tree(File dir, List<OmniObject> objs) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("File argument must be a directory");
        }
        this.dir = dir;
        this.children = objs;
        StringBuilder sb = new StringBuilder();
        sb.append('T');
        for (OmniObject obj: children) {
            sb.append(obj.getSHA1());
        }
        sha1 = sb.toString();
    }

    @Override
    public String getSHA1() {
        return sha1;
    }

    @Override
    public String getPath() {
        return dir.getAbsolutePath();
    }

    @Override
    public String getName() {
        return dir.getName();
    }

    public List<OmniObject> getChildren() {
        return children;
    }
}