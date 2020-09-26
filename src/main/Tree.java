package src.main;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

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
}