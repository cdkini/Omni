package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.Blob;
import src.main.OmniObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBlob {
    private String mockDirPath;
    private File mockFile;
    private Blob mockBlob;

    @Rule
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        mockDirPath = mockDir.getRoot().getAbsolutePath();
        mockFile = mockDir.newFile("mockFile");
        mockBlob = new Blob(mockFile);
    }

    @After
    public void tearDown() {
        mockDirPath = "";
        mockFile = null;
        mockBlob = null;
    }

    @Test (expected = Exception.class)
    public void instantiationOfBlobWithInvalidFileShouldFail() throws FileNotFoundException {
        Blob invalidBlob = new Blob(new File("abc"));
    }

    @Test
    public void serializeBlobShouldCreateHashFile() throws FileNotFoundException {
        mockBlob.serialize(mockDir.getRoot(), "res");
        assertTrue(Files.exists(Paths.get(mockDirPath, "res")));
    }

    @Test
    public void deserializeHashFileShouldCreateBlob() throws FileNotFoundException {
        mockBlob.serialize(mockDir.getRoot(), "res");
        Blob resBlob = (Blob) OmniObject.deserialize(mockDir.getRoot(), "res");
        assertEquals(mockBlob.getPath(), resBlob.getPath());
        assertEquals(mockBlob.getName(), resBlob.getName());
        assertEquals(mockBlob.getSHA1(), resBlob.getSHA1());
    }

    @Test
    public void getSHA1OfBlobShouldStartWithB() {
        String sha1 = mockBlob.getSHA1();
        assertTrue(sha1.startsWith("B"));
    }
}