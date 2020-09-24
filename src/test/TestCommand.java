package src.test;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;
import org.w3c.dom.ls.LSOutput;
import src.main.Command;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class TestCommand {

    @Rule
    public TemporaryFolder mockFolder = new TemporaryFolder();

    @Test
    public void testInit() throws IOException {
        Command.init();
        System.out.println(mockFolder.getRoot());
    }
}