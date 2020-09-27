package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.OmniRepo;
import src.main.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestOmniRepo {
    private String mockDirPath;
    private OmniRepo mockOmniRepo;
    private File mockObjectsDir;

    @Rule
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Before
    public void setUp() {
        mockDirPath = mockDir.getRoot().getAbsolutePath();
        mockOmniRepo = new OmniRepo(mockDirPath);
        mockObjectsDir = new File(mockDirPath + ".omni/objects");
    }

    @After
    public void tearDown() {
        mockDirPath = "";
        mockOmniRepo = null;
        mockObjectsDir = null;
    }

    // OmniRepo.init
    @Test
    public void InitShouldCreateDirectoryAndSubdirectories() throws IOException {
        mockOmniRepo.init();
        assertTrue(Files.isDirectory(Paths.get(mockDirPath + ".omni/objects/")));
        assertTrue(Files.isDirectory(Paths.get(mockDirPath + ".omni/branches/")));
        assertTrue(Files.isDirectory(Paths.get(mockDirPath + ".omni/refs/heads")));
        assertTrue(Files.exists(Paths.get(mockDirPath + ".omni/HEAD")));
    }

    @Test
    public void InitShouldSetProperFileContents() throws IOException {
        mockOmniRepo.init();
        byte[] headContents = Utils.readContents(new File(mockDirPath + ".omni/HEAD"));
        assertEquals(new String(headContents, StandardCharsets.UTF_8), "ref: refs/heads/master\n");
    }

    @Test(expected = Exception.class)
    public void InitWithExistingDirectoryShouldFail() throws IOException {
        Files.createDirectory(Paths.get(mockDirPath + ".omni/"));
        mockOmniRepo.init();
    }

    // OmniRepo.add
    @Test
    public void AddShouldSerializeBlobInObjectsDir() throws IOException {
        mockOmniRepo.init();
        File mockFile = mockDir.newFile("foo.txt");
        assertEquals(0, mockObjectsDir.list().length);

        mockOmniRepo.add("foo.txt");
        assertEquals(1, mockObjectsDir.list().length);
    }

    @Test
    public void AddOfTwoBlankBlobsShouldOnlyAddOnce() throws IOException {
        mockOmniRepo.init();
        assertEquals(0, mockObjectsDir.list().length);

        File mockFile1 = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        assertEquals(1, mockObjectsDir.list().length);

        File mockFile2 = mockDir.newFile("bar.txt");
        mockOmniRepo.add("bar.txt");
        assertEquals(1, mockObjectsDir.list().length);
    }

    @Test
    public void AddOfTwoBlobsWithDifferentContentsShouldAddTwicec() throws IOException {
        mockOmniRepo.init();
        assertEquals(0, mockObjectsDir.list().length);

        File mockFile1 = mockDir.newFile("foo.txt");
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(mockFile1.getPath(), true));
        bw1.write("This is a random piece of text!");
        bw1.close();
        mockOmniRepo.add("foo.txt");
        assertEquals(1, mockObjectsDir.list().length);

        File mockFile2 = mockDir.newFile("bar.txt");
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(mockFile2.getPath(), true));
        bw2.write("This is a random piece of text! But slightly different.");
        bw2.close();
        mockOmniRepo.add("bar.txt");
        assertEquals(2, mockObjectsDir.list().length);
    }

    @Test
    public void AddOfTwoBlobsWithIdenticalContentsShouldOnlyAddOnce() throws IOException {
        mockOmniRepo.init();
        assertEquals(0, mockObjectsDir.list().length);

        File mockFile1 = mockDir.newFile("foo.txt");
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(mockFile1.getPath(), true));
        bw1.write("This is a random piece of text!");
        bw1.close();
        mockOmniRepo.add("foo.txt");
        assertEquals(1, mockObjectsDir.list().length);

        File mockFile2 = mockDir.newFile("bar.txt");
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(mockFile2.getPath(), true));
        bw2.write("This is a random piece of text!");
        bw2.close();
        mockOmniRepo.add("bar.txt");
        assertEquals(1, mockObjectsDir.list().length);
    }

    @Test
    public void AddShouldSerializeTreeInObjectsDir() {
        assertTrue(false);
    }

    @Test (expected = Exception.class)
    public void AddToUninitializedDirectoryShouldFail() throws IOException {
        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
    }

    @Test (expected = Exception.class)
    public void AddNonExistingFileShouldFail() throws IOException {
        mockOmniRepo.init();
        mockOmniRepo.add("foo.txt");
    }
}