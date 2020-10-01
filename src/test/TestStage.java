package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertTrue;

public class TestStage {
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
    public void writeContentsToIndexShouldCreateIndex() {
        // TODO: Open to add test!
        assertTrue(false);
    }

    @Test
    public void writeContentsToIndexShouldOverwriteExistingIndex() {
        // TODO: Open to add test!
        assertTrue(false);
    }

    @Test
    public void instantiationOfStageShouldReadFromIndex() {
        // TODO: Open to add test!
        assertTrue(false);
    }

    @Test (expected = Exception.class)
    public void instantiationWithoutIndexShouldFail() {
        // TODO: Open to add test!
    }
}