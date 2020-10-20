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
    private String sha1;

    /**
     * Sole constructor.
     *
     * @param root is a Tree representing the entire tracked repository.
     * @param parent is the prior Commit in the current Branch (null if initial commit).
     * @param message is the commit message passed by the user.
     * @param tracked is a list of files being tracked in the current snapshot.
     */
    public Commit(Tree root, Commit parent, String message, List<String> tracked) {
        this.root = root;
        this.parent = parent;
        this.timeStamp = new Date().getTime();
        this.message = message;
        this.tracked = tracked;
        this.sha1 = "C"+Utils.sha1(root.getSHA1());
    }

    /**
     * Removes a given file from the tracked file list.
     *
     * @param filePath is the path of the file to be removed from the tracked files list.
     */
    public void removeFromTracked(String filePath) {
        tracked.remove(filePath);
    }

    @Override
    public String getSHA1()  {
        return sha1;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public Commit getParent() {
        return parent;
    }

    public String getMessage() {
        return message;
    }

    public Date getDateTime() {
        return new Date(timeStamp);
    }

    public List<String> getTracked() {
        return tracked;
    }
}