package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.OmniRepo;
import src.main.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestOmniRepo {
    private String mockDirPath;

    @Rule
    public OmniRepo mockOmniRepo = new OmniRepo();
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Before
    public void setUp() {
        mockDirPath = mockDir.getRoot().getAbsolutePath();
    }

    @After
    public void tearDown() {
        mockDirPath = "";
    }

    // OmniRepo.init
    @Test
    public void InitShouldCreateDirectoryAndSubdirectories() throws IOException {
        mockOmniRepo.init(mockDirPath);
        assertTrue(Files.isDirectory(Paths.get(mockDirPath+".omni/objects/")));
        assertTrue(Files.isDirectory(Paths.get(mockDirPath+".omni/branches/")));
        assertTrue(Files.isDirectory(Paths.get(mockDirPath+".omni/refs/heads")));
        assertTrue(Files.exists(Paths.get(mockDirPath + ".omni/HEAD")));
    }

    @Test
    public void InitShouldSetProperFileContents() throws IOException {
        mockOmniRepo.init(mockDirPath);
        byte[] headContents = Utils.readContents(new File(mockDirPath+".omni/HEAD"));
        assertEquals(new String(headContents, StandardCharsets.UTF_8), "ref: refs/heads/master\n");
    }

    @Test (expected = Exception.class)
    public void InitWithExistingDirectoryShouldFail() throws IOException {
        Files.createDirectory(Paths.get(mockDirPath+".omni/"));
        mockOmniRepo.init(mockDirPath);
    }

    // OmniRepo.add
    @Test
    public void AddShouldSerializeBlobInObjectsDir() throws IOException {}

    @Test
    public void AddShouldSerializeTreeInObjectsDir() {}

    @Test (expected = Exception.class)
    public void AddToUninitializedDirectoryShouldFail() throws FileNotFoundException {}

    @Test (expected = Exception.class)
    public void AddNonExistingFileShouldFail() {}
}