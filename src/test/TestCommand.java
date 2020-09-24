package src.test;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;
import src.main.Command;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestCommand {

    @Rule
    public TemporaryFolder mockDir = new TemporaryFolder();

    @Test
    public void InitWithExistingDirectoryShouldFail() throws IOException {
        // TODO: Open to implement test!
        String mockPath = mockDir.getRoot().getAbsolutePath();
        Command.init(mockPath);
    }

    @Test
    public void InitShouldCreateDirectoryAndSubdirectories() throws IOException {
        // TODO: Open to implement test!
        String mockPath = mockDir.getRoot().getAbsolutePath();
        Command.init(mockPath);
    }
}