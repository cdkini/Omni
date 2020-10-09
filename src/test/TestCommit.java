package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.Commit;
import src.main.OmniObject;
import src.main.Tree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCommit {
    private String mockDirPath;
    private Tree root;

    @Rule
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        mockDirPath = mockDir.getRoot().getAbsolutePath();
        root = new Tree(mockDir.getRoot());
    }

    @After
    public void tearDown() {
        mockDirPath = "";
        root = null;
    }

    @Test
    public void serializeCommitShouldCreateHashFile() throws FileNotFoundException {
        Commit mockCommit = new Commit(root, null, "This is a random commit message!", new ArrayList<>());
        mockCommit.serialize(mockDir.getRoot(), "res");
        assertTrue(Files.exists(Paths.get(mockDirPath, "res")));
    }

    @Test
    public void deserializeHashFileShouldCreateCommit() throws FileNotFoundException {
        Commit mockCommit = new Commit(root, null, "This is a random commit message!", new ArrayList<>());
        mockCommit.serialize(mockDir.getRoot(), "res");
        Commit resCommit = (Commit) OmniObject.deserialize(mockDir.getRoot(), "res");
        assertEquals(mockCommit.getPath(), resCommit.getPath());
        assertEquals(mockCommit.getName(), resCommit.getName());
        assertEquals(mockCommit.getSHA1(), resCommit.getSHA1());
    }

    @Test
    public void getSHA1OfCommitShouldStartWithC() {
        Commit mockCommit = new Commit(root, null, "", new ArrayList<>());
        String sha1 = mockCommit.getSHA1();
        assertTrue(sha1.startsWith("C"));
    }
}