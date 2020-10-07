package src.main;

import java.io.File;
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

    /**
     * TODO: Write docstring!
     * @param dir
     */
    public Tree(File dir) {
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
        StringBuilder sha1 = new StringBuilder();
        sha1.append('T');
        for (OmniObject obj: children) {
            sha1.append(obj.getSHA1());
        }
        return sha1.toString();
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