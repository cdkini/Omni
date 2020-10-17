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
     * TODO: Write docstring!
     * @param dir
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
     * TODO: Write docstring!
     * @param dir
     * @param objs
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

    public List<OmniObject> getChildren() {
        return children;
    }

    /**
     * FIXME: Update SHA1 for tree (issues created by nested directories)
     * TODO: Write docstring!
     * @return
     */
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
}