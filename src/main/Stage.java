package src.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Stage {
    private String path;
    private Commit head;
    private Commit currBranch;
    private Map<String, OmniObject> contents;

    public Stage(String path) throws FileNotFoundException {
        this.path = path;
        File index = new File(path, ".omni/index");
        Scanner scanner = new Scanner(index);
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

    public void add(String fileName, OmniObject obj) {
        contents.put(fileName, obj);
    }

    public void writeContentsToIndex() throws IOException {
        File index = new File(path,".omni/index");
        FileWriter fw = new FileWriter(index, false);
        fw.write("head: "+head+"\n");
        fw.write("branch: "+currBranch+"\n");
        fw.write("contents: "+contents);
        fw.close();
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

    public boolean isEmpty() {
        return contents.isEmpty();
    }
}