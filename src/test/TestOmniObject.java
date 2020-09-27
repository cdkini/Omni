package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class TestOmniObject {
    private String mockPath;

    @Rule
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Before
    public void setUp() {
        mockPath = mockDir.getRoot().getAbsolutePath();
    }

    @After
    public void tearDown() {
        mockPath = "";
    }

    @Test
    public void BlobTest() throws IOException {
        File mockFile = mockDir.newFile();
    }
}
