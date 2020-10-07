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
    public void setUp() throws IOException {
        mockDirPath = mockDir.getRoot().getAbsolutePath();
        mockOmniRepo = new OmniRepo(mockDirPath);
        mockObjectsDir = new File(mockDirPath, ".omni/objects");
    }

    @After
    public void tearDown() {
        mockDirPath = "";
        mockOmniRepo = null;
        mockObjectsDir = null;
    }

    private void saveStateBetweenCommands() throws IOException {
        mockOmniRepo.saveState();
        mockOmniRepo = new OmniRepo(mockDirPath);
    }

    // OmniRepo.init____________________________________________________________________________________________________

    @Test
    public void initShouldCreateDirectoryAndSubdirectories() throws IOException {
        mockOmniRepo.init();
        assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/objects/")));
        assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/branches/")));
        assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/refs/heads")));
        assertTrue(Files.exists(Paths.get(mockDirPath, "/.omni/HEAD")));
    }

    @Test
    public void initShouldSetProperFileContents() throws IOException {
        mockOmniRepo.init();
        byte[] headContents = Utils.readContents(new File(mockDirPath, "/.omni/HEAD"));
        assertEquals(new String(headContents, StandardCharsets.UTF_8), "ref: refs/heads/master\n");
    }

    @Test(expected = Exception.class)
    public void initWithExistingDirectoryShouldFail() throws IOException {
        Files.createDirectory(Paths.get(mockDirPath, "/.omni/"));
        mockOmniRepo.init();
    }

    // OmniRepo.add_____________________________________________________________________________________________________

    @Test
    public void addShouldSerializeBlobInObjectsDir() throws IOException {
        mockOmniRepo.init();
        File mockFile = mockDir.newFile("foo.txt");
        saveStateBetweenCommands();
        mockOmniRepo.add("foo.txt");
        assertEquals(1, mockObjectsDir.list().length);
    }

    @Test
    public void addOfTwoBlankBlobsShouldOnlyAddOnce() throws IOException {
        mockOmniRepo.init();
        String[] fileNames = {"foo.txt", "bar.txt"};
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            saveStateBetweenCommands();
            mockOmniRepo.add(fileNames[i]);
            assertEquals(1, mockObjectsDir.list().length);
        }
    }

    @Test
    public void addOfTwoBlobsWithDifferentContentsShouldAddTwice() throws IOException {
        mockOmniRepo.init();

        String[] fileNames = {"foo.txt", "bar.txt"};
        String[] fileMsgs = {"This is a random piece of text!", "This is another random piece of text!"};
        BufferedWriter bw;
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            bw = new BufferedWriter(new FileWriter(mockFile.getPath(), true));
            bw.write(fileMsgs[i]);
            bw.close();
            saveStateBetweenCommands();
            mockOmniRepo.add(fileNames[i]);
        }
        assertEquals(2, mockObjectsDir.list().length);
    }

    @Test
    public void addOfTwoBlobsWithIdenticalContentsShouldOnlyAddOnce() throws IOException {
        mockOmniRepo.init();

        String[] fileNames = {"foo.txt", "bar.txt"};
        BufferedWriter bw;
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            bw = new BufferedWriter(new FileWriter(mockFile.getPath(), true));
            bw.write("This is a random piece of text!");
            bw.close();
            saveStateBetweenCommands();
            mockOmniRepo.add(fileNames[i]);
            assertEquals(1, mockObjectsDir.list().length);
        }
    }

    @Test
    public void addShouldSerializeTreeInObjectsDir() throws IOException {
        mockOmniRepo.init();

        File mockFolder = mockDir.newFolder("Temp");
        String[] fileNames = {"Temp/foo.txt", "Temp/bar.txt"};
        String[] fileMsgs = {"This is foo!", "This is bar!"};
        BufferedWriter bw;
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            bw = new BufferedWriter(new FileWriter(mockFile.getPath(), true));
            bw.write(fileMsgs[i]);
            bw.close();
        }

        saveStateBetweenCommands();
        mockOmniRepo.add("Temp");
        assertEquals(3, mockObjectsDir.list().length);
    }

    @Test
    public void addOfTreeWithSameContentsShouldOnlyAddOnce() throws IOException {
        mockOmniRepo.init();

        String[] dirNames = {"TempA", "TempB"};
        String[] fileNames = {"foo.txt", "bar.txt"};
        String[] fileMsgs = {"This is foo!", "This is bar!"};
        for (int i = 0; i < dirNames.length; i++) {
            File mockFolder = mockDir.newFolder(dirNames[i]);
            BufferedWriter bw;
            for (int j = 0; j < fileNames.length; j++) {
                File mockFile = mockDir.newFile(dirNames[i]+"/"+fileNames[j]);
                bw = new BufferedWriter(new FileWriter(mockFile.getPath(), true));
                bw.write(fileMsgs[j]);
                bw.close();
            }
        }

        saveStateBetweenCommands();
        mockOmniRepo.add("TempA");
        assertEquals(3, mockObjectsDir.list().length);

        saveStateBetweenCommands();
        mockOmniRepo.add("TempB");
        assertEquals(3, mockObjectsDir.list().length);
    }

    @Test (expected = Exception.class)
    public void addToUninitializedDirectoryShouldFail() throws IOException {
        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
    }

    @Test (expected = Exception.class)
    public void addNonExistingFileShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.add("foo.txt");
    }

    // OmniRepo.commit__________________________________________________________________________________________________

    @Test
    public void commitOfStagedFileShouldSerializeInObjectsDir() throws IOException {
        mockOmniRepo.init();
        File mockFile = mockDir.newFile("foo.txt");
        saveStateBetweenCommands();
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();
        mockOmniRepo.commit("Committed foo!");
        assertEquals(3, mockObjectsDir.list().length);
    }

    @Test (expected = Exception.class)
    public void commitOfEmptyStageShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.commit("Commit message.");
    }

    @Test (expected = Exception.class)
    public void commitOfUninitializedDirectoryShouldFail() throws IOException {
        mockOmniRepo.commit("Commit message.");
    }

    @Test
    public void commitClearsStagedFiles() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        File mockFile = mockDir.newFile("foo.txt");
        saveStateBetweenCommands();
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();
        mockOmniRepo.commit("Committed foo!");
        assertEquals(0, mockOmniRepo.getStagedFiles().size());
    }

    // OmniRepo.rm______________________________________________________________________________________________________

    // OmniRepo.log_____________________________________________________________________________________________________

    // OmniRepo.globalLog_______________________________________________________________________________________________

    // OmniRepo.find____________________________________________________________________________________________________

    // OmniRepo.status__________________________________________________________________________________________________

    // OmniRepo.checkout________________________________________________________________________________________________

    // OmniRepo.branch__________________________________________________________________________________________________

    // OmniRepo.rmBranch________________________________________________________________________________________________

    // OmniRepo.reset___________________________________________________________________________________________________

    // OmniRepo.merge___________________________________________________________________________________________________

}
