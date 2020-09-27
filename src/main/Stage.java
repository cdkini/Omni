package src.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage {
    private String path;
    private Commit head;
    private Commit currBranch;
    private Map<String, OmniObject> contents;

    public Stage(String path) {
        this.path = path;
        this.contents = new HashMap<>();
    }

    public void add(String fileName, OmniObject obj) {
        contents.put(fileName, obj);
    }

    public void clear() {
        contents.clear();
    }

    public List<OmniObject> getObjects() {
        return new ArrayList<>(contents.values());
    }

    public Commit getHead() {
        return head;
    }

    public void setHead(Commit head) {
        this.head = head;
    }

    public Commit getCurrBranch() {
        return currBranch;
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }
}