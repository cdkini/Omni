package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.OmniRepo;
import src.main.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCommand {
    private String mockPath;

    @Rule
    public OmniRepo mockRepo = new OmniRepo();
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Before
    public void setUp() {
        mockPath = mockDir.getRoot().getAbsolutePath();
    }

    @After
    public void tearDown() {
        mockPath = "";
    }

    @Test (expected = Exception.class)
    public void InitWithExistingDirectoryShouldFail() throws IOException {
        Files.createDirectory(Paths.get(mockPath+".omni/"));
        mockRepo.init(mockPath);
    }

    @Test
    public void InitShouldCreateDirectoryAndSubdirectories() throws IOException {
        mockRepo.init(mockPath);
        assertTrue(Files.isDirectory(Paths.get(mockPath+".omni/objects/")));
        assertTrue(Files.isDirectory(Paths.get(mockPath+".omni/branches/")));
        assertTrue(Files.isDirectory(Paths.get(mockPath+".omni/refs/heads")));
        assertTrue(Files.exists(Paths.get(mockPath + ".omni/HEAD")));
    }

    @Test
    public void InitShouldSetProperFileContents() throws IOException {
        mockRepo.init(mockPath);
        byte[] headContents = Utils.readContents(new File(mockPath+".omni/HEAD"));
        assertEquals(new String(headContents, StandardCharsets.UTF_8), "ref: refs/heads/master\n");
    }
}