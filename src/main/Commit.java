package src.main;

import src.main.OmniObject;

import java.io.File;
import java.io.Serializable;

/*
 * Commit is Omni's internal representation of a snapshot of a repository and its staged files at a particular point in
 * time. As such, it is inclusive of metadata such as the prior commit, author, time of commit, and a commit message or
 * log. The tree that it points to is the tracked repository.
 */
public class Commit extends OmniObject implements Serializable {
    private Tree root;
    private Commit parent;
    private String author;
    private int timeStamp;
    private String message;

    public Commit(File f) {

    }
}