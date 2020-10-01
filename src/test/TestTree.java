package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TestTree {
    private String mockDirPath;
    private File mockNestedDir;

    @Rule
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        mockDirPath = mockDir.getRoot().getAbsolutePath();
        mockNestedDir = mockDir.newFolder("nestedDir");
    }

    @After
    public void tearDown() {
        mockDirPath = "";
        mockNestedDir = null;
    }

    @Test (expected = Exception.class)
    public void instantiationOfTreeWithFileShouldFail() throws IOException {
        File testFile = mockDir.newFile("test");
        Tree invalidTree = new Tree(testFile);
    }

    @Test (expected = Exception.class)
    public void instantiationOfTreeWithNonexistentDirectoryShouldFail() {
        Tree invalidTree = new Tree(new File("abc"));
    }

    @Test
    public void instantiationfileOfTreeWithNestedFilesShouldAddToChildren() throws IOException {
        // TODO: Open to add test!
        assertTrue(false);
    }

    @Test
    public void serializeTreeShouldCreateHashFile() {
        Tree mockTree = new Tree(mockNestedDir);
        mockTree.serialize(mockDir.getRoot(), "res");
        assertTrue(Files.exists(Paths.get(mockDirPath, "res")));
    }

    @Test
    public void serializeTreeContainingFilesShouldCreateMultipleHashFiles() throws IOException {
        // TODO: Open to add test!
        assertTrue(false);
    }

    @Test
    public void serializeTreeContainingTreeShouldCreateTwoHashFiles() {
        // TODO: Open to add test!
        assertTrue(false);
    }

    @Test
    public void deserializeHashFileShouldCreateTree() {
        // TODO: Open to add test!
        assertTrue(false);
    }

    @Test
    public void getSHA1OfTreeShouldStartWithT() {
        Tree mockTree = new Tree(mockNestedDir);
        String sha1 = mockTree.getSHA1();
        assertTrue(sha1.startsWith("T"));
    }
}