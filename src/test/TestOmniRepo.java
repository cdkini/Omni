package src.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import src.main.OmniRepo;
import src.main.Utils;

import java.io.*;
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
    public void setUp() throws FileNotFoundException {
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

    // Miscellaneous____________________________________________________________________________________________________

    @Test
    public void saveStateShouldCreateIndex() throws IOException {
        mockOmniRepo.init();
        mockOmniRepo.saveState();
        assertTrue(Files.exists(Paths.get(mockDirPath, "/.omni/index")));
    }

    @Test
    public void saveStageShouldOverwriteExistingIndex() throws IOException {
        assertTrue(false);
    }

    // OmniRepo.init____________________________________________________________________________________________________

    @Test
    public void InitShouldCreateDirectoryAndSubdirectories() throws IOException {
        mockOmniRepo.init();
        assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/objects/")));
        assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/branches/")));
        assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/refs/heads")));
        assertTrue(Files.exists(Paths.get(mockDirPath, "/.omni/HEAD")));
        assertTrue(Files.exists(Paths.get(mockDirPath,"/.omni/index")));
    }

    @Test
    public void InitShouldSetProperFileContents() throws IOException {
        mockOmniRepo.init();
        byte[] headContents = Utils.readContents(new File(mockDirPath, "/.omni/HEAD"));
        assertEquals(new String(headContents, StandardCharsets.UTF_8), "ref: refs/heads/master\n");
    }

    @Test(expected = Exception.class)
    public void InitWithExistingDirectoryShouldFail() throws IOException {
        Files.createDirectory(Paths.get(mockDirPath, "/.omni/"));
        mockOmniRepo.init();
    }

    // OmniRepo.add_____________________________________________________________________________________________________

    @Test
    public void AddShouldSerializeBlobInObjectsDir() throws IOException {
        mockOmniRepo.init();
        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        assertEquals(1, mockObjectsDir.list().length);
    }

    @Test
    public void AddOfTwoBlankBlobsShouldOnlyAddOnce() throws IOException {
        mockOmniRepo.init();
        String[] fileNames = {"foo.txt", "bar.txt"};
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            mockOmniRepo.add(fileNames[i]);
            assertEquals(1, mockObjectsDir.list().length);
        }
    }

    @Test
    public void AddOfTwoBlobsWithDifferentContentsShouldAddTwice() throws IOException {
        mockOmniRepo.init();

        String[] fileNames = {"foo.txt", "bar.txt"};
        String[] fileMsgs = {"This is a random piece of text!", "This is another random piece of text!"};
        BufferedWriter bw;
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            bw = new BufferedWriter(new FileWriter(mockFile.getPath(), true));
            bw.write(fileMsgs[i]);
            bw.close();
            mockOmniRepo.add(fileNames[i]);
        }
        assertEquals(2, mockObjectsDir.list().length);
    }

    @Test
    public void AddOfTwoBlobsWithIdenticalContentsShouldOnlyAddOnce() throws IOException {
        mockOmniRepo.init();

        String[] fileNames = {"foo.txt", "bar.txt"};
        BufferedWriter bw;
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            bw = new BufferedWriter(new FileWriter(mockFile.getPath(), true));
            bw.write("This is a random piece of text!");
            bw.close();
            mockOmniRepo.add(fileNames[i]);
            assertEquals(1, mockObjectsDir.list().length);
        }
    }

    @Test
    public void AddShouldSerializeTreeInObjectsDir() throws IOException {
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

        mockOmniRepo.add("Temp");
        assertEquals(3, mockObjectsDir.list().length);
    }

    @Test
    public void AddOfTreeWithSameContentsShouldOnlyAddOnce() throws IOException {
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

        mockOmniRepo.add("TempA");
        assertEquals(3, mockObjectsDir.list().length);
        mockOmniRepo.add("TempB");
        assertEquals(3, mockObjectsDir.list().length);
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

    // OmniRepo.commit__________________________________________________________________________________________________

    @Test
    public void CommitOfStagedFileShouldSerializeInObjectsDir() throws IOException {
        mockOmniRepo.init();
        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        mockOmniRepo.commit("Committed foo!");
        assertEquals(3, mockObjectsDir.list().length);
    }

    @Test (expected = Exception.class)
    public void CommitOfEmptyStageShouldFail() throws IOException {
        mockOmniRepo.init();
        mockOmniRepo.commit("Commit message.");
    }

    @Test (expected = Exception.class)
    public void CommitOfUnitializedDirectoryShouldFail() throws FileNotFoundException {
        mockOmniRepo.commit("Commit message.");
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
