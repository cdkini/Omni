package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBranch {
    private String mockDirPath;
    private Commit mockCommit;

    @Rule
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        mockDirPath = mockDir.getRoot().getAbsolutePath();
        mockCommit = new Commit(new Tree(mockDir.getRoot()), null, "Init commit",  new ArrayList<>());
    }

    @After
    public void tearDown() {
        mockDirPath = "";
        mockCommit = null;
    }

    @Test
    public void serializeBranchShouldCreateHashFile() throws FileNotFoundException {
        Branch mockBranch = new Branch("testBranch", mockCommit);
        mockBranch.serialize(mockDir.getRoot());
        assertTrue(Files.exists(Paths.get(mockDirPath, "testBranch")));
    }

    @Test
    public void deserializeHashFileShouldCreateBranch() throws FileNotFoundException {
        Branch mockBranch = new Branch("testBranch", mockCommit);
        mockBranch.serialize(mockDir.getRoot());

        Branch resBranch = Branch.deserialize(mockDir.getRoot(), "testBranch");
        assertEquals(mockBranch.getName(), resBranch.getName());

        Commit resCommit = resBranch.getCommit();
        assertEquals(mockCommit.getTracked(), resCommit.getTracked());
        assertEquals(mockCommit.getMessage(), resCommit.getMessage());
        assertEquals(mockCommit.getSHA1(), resCommit.getSHA1());
        assertEquals(mockCommit.getName(), resCommit.getName());
        assertEquals(mockCommit.getPath(), resCommit.getPath());
        assertEquals(mockCommit.getDateTime(), resCommit.getDateTime());
        assertEquals(mockCommit.getParent(), resCommit.getParent());
    }
}
