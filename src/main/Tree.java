package src.main;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Tree extends OmniObject implements Serializable {
    private String dirName;
    private ArrayList<Tree> trees;
    private ArrayList<Blob> blobs;

    public Tree(File dir) {
    }
}