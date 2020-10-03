package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.Blob;
import src.main.OmniObject;
import src.main.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
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

    private void createMockFiles(String... fileNames) throws IOException {
        for (String fileName: fileNames) {
            mockDir.newFile(mockNestedDir.getName()+"/"+fileName);
        }
    }

    @Test (expected = Exception.class)
    public void instantiationOfTreeWithFileShouldFail() throws IOException {
        File testFile = mockDir.newFile("test");
        Tree invalidTree = new Tree(testFile);
    }

    @Test (expected = Exception.class)
    public void instantiationOfTreeWithNonexistentDirectoryShouldFail() {
        Tree invalidTree = new Tree(new File("temp"));
    }

    @Test
    public void instantiationOfEmptyTreeShouldCreateEmptyChildren() throws IOException {
        Tree mockTree = new Tree(mockNestedDir);
    }

    @Test
    public void instantiationOfTreeShouldAddProperAmountOfChildren() throws IOException {
        createMockFiles("a", "b", "c", "d", "e");
        Tree mockTree = new Tree(mockNestedDir);
        assertEquals(5, mockTree.getChildren().size());
    }

    @Test
    public void instantiationOfTreeShouldAddCorrectChildren() throws IOException {
        createMockFiles("a", "b", "c", "d", "e");
        Tree mockTree = new Tree(mockNestedDir);
        ArrayList<String> hashIDs = new ArrayList<>();
        for (OmniObject child: mockTree.getChildren()) {
            hashIDs.add(child.getSHA1());
        }
        for (File f: mockNestedDir.listFiles()) {
            Blob mockBlob = new Blob(f);
            assertTrue(hashIDs.contains(mockBlob.getSHA1()));
        }
    }

    @Test
    public void serializeTreeShouldCreateHashFile() {
        Tree mockTree = new Tree(mockNestedDir);
        mockTree.serialize(mockDir.getRoot(), "res");
        assertTrue(Files.exists(Paths.get(mockDirPath, "res")));
    }

    @Test
    public void deserializeHashFileShouldCreateTree() throws IOException {
        createMockFiles("a", "b", "c", "d", "e");
        Tree mockTree = new Tree(mockNestedDir);
        mockTree.serialize(mockDir.getRoot(), "res");
        Tree resTree = (Tree) OmniObject.deserialize(mockDir.getRoot(), "res");
        assertEquals(mockTree.getChildren(), mockTree.getChildren());
        assertEquals(mockTree.getPath(), resTree.getPath());
        assertEquals(mockTree.getName(), resTree.getName());
        assertEquals(mockTree.getSHA1(), resTree.getSHA1());
    }

    @Test
    public void getSHA1OfTreeShouldStartWithT() {
        Tree mockTree = new Tree(mockNestedDir);
        String sha1 = mockTree.getSHA1();
        assertTrue(sha1.startsWith("T"));
    }
}