package src.main;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/*
 * Commit is Omni's internal representation of a snapshot of a repository and its staged files at a particular point in
 * time. As such, it is inclusive of metadata such as the prior commit, author, time of commit, and a commit message or
 * log. The tree that it points to is the tracked repository.
 */
public class Commit extends OmniObject implements Serializable {
    private Tree root;
    private Commit parent;
    private long timeStamp;
    private String message;
    private List<String> tracked;

    /**
     * TODO: Write docstring!
     * @param root
     * @param parent
     * @param message
     */
    public Commit(Tree root, Commit parent, String message, List<String> tracked) {
        this.root = root;
        this.parent = parent;
        this.timeStamp = new Date().getTime();
        this.message = message;
        this.tracked = tracked;
    }

    /**
     * TODO: Write docstring!
     * @return
     */
    @Override
    public String getSHA1()  {
        return "C"+Utils.sha1(root.getSHA1());
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}