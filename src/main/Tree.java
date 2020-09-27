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
    private String dirName;
    private ArrayList<Tree> trees;
    private ArrayList<Blob> blobs;

    public Tree(File dir) {
        this.trees = new ArrayList<>();
        this.blobs = new ArrayList<>();
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) {
                this.trees.add(new Tree(file));
            } else {
                this.blobs.add(new Blob(file));
            }
        }
        this.dirName = dir.getName();
    }

    @Override
    public String getSHA1(File file) {
        return null;
    }
}