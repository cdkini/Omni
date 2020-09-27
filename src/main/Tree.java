package src.main;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/*
 * Tree is Omni's internal representation of a directory and its contents. A single tree can contain pointers to
 * multiple other trees and blobs, creating a tree or graph-like structure used to define a repository's version
 * control history.
 */
public class Tree extends OmniObject implements Serializable {
    private File dir;
    private ArrayList<Tree> trees;
    private ArrayList<Blob> blobs;

    public Tree(File dir) {
        this.dir = dir;
        this.trees = new ArrayList<>();
        this.blobs = new ArrayList<>();
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) {
                this.trees.add(new Tree(file));
            } else {
                this.blobs.add(new Blob(file));
            }
        }
    }

    @Override
    public String getSHA1() {
        return null;
    }

    @Override
    public String getName() {
        return dir.getName();
    }
}