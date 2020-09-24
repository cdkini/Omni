package src.main;

import src.main.OmniObject;

import java.io.File;
import java.io.Serializable;

public class Commit extends OmniObject implements Serializable {
    private Tree root;
    private Commit parent;
    private String author;
    private int timeStamp;
    private String message;

    public Commit(File f) {

    }
}