package src.main;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage {
    private String path;
    private Commit head;
    private Commit branch;
    private Map<String, OmniObject> contents;

    public Stage(String path) {
        this.path = path;
        this.head = null;
        this.branch = null;
        this.contents = new HashMap<>();
        File index = new File(path, ".omni/index");
//        Scanner scanner = new Scanner(index);
        // TODO: Fix reading and creation of attribute objects
//        while (scanner.hasNextLine()) {
//            String line = scanner.nextLine();
//            if (line.startsWith("head") {
//                this.head = line.split(": ")[1];
//            } else if (line.startsWith("branch") {
//                this.currBranch = line.split(": ");
//            } else {
//
//            }
//        }
    }

    public void writeContentsToIndex() throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("head", head);
        obj.put("branch", branch);
        obj.put("contents", contents);

        File index = new File(path,".omni/index");
        FileWriter fw = new FileWriter(index);
        fw.write(obj.toString());
        fw.flush();
        fw.close();
    }

    public void add(String fileName, OmniObject obj) {
        contents.put(fileName, obj);
    }

    public ArrayList<OmniObject> getObjects() {
        return new ArrayList<>(contents.values());
    }

    public Commit getHead() {
        return head;
    }

    public void setHead(Commit head) {
        this.head = head;
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }
}