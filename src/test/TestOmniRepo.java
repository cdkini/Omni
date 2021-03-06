package src.test;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import src.main.OmniRepo;
import src.main.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        Assert.assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/objects/")));
        Assert.assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/branches/")));
        Assert.assertTrue(Files.isDirectory(Paths.get(mockDirPath, "/.omni/refs/heads")));
        Assert.assertTrue(Files.exists(Paths.get(mockDirPath, "/.omni/HEAD")));
    }

    @Test
    public void initShouldCreateMasterInBranchesDir() throws IOException {
        mockOmniRepo.init();
        Assert.assertTrue(Files.exists(Paths.get(mockDirPath, "/.omni/branches/master")));
    }

    @Test
    public void initShouldSetProperFileContents() throws IOException {
        mockOmniRepo.init();
        byte[] headContents = Utils.readContents(new File(mockDirPath, "/.omni/HEAD"));
        Assert.assertEquals(new String(headContents, StandardCharsets.UTF_8), "ref: refs/heads/master\n");
    }

    @Test
    public void initShouldCreateAnInitialCommitAndTree() throws IOException {
        mockOmniRepo.init();
        Assert.assertEquals(2, mockObjectsDir.list().length);
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
        saveStateBetweenCommands();

        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        Assert.assertEquals(3, mockObjectsDir.list().length);
    }

    @Test
    public void addOfTwoBlankFilesShouldOnlyAddOnceToObjectsDir() throws IOException {
        mockOmniRepo.init();
        String[] fileNames = {"foo.txt", "bar.txt"};
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            saveStateBetweenCommands();
            mockOmniRepo.add(fileNames[i]);
            Assert.assertEquals(3, mockObjectsDir.list().length);
        }
    }

    @Test
    public void addOfTwoFilesWithDifferentContentsShouldAddToObjectsDirTwice() throws IOException {
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
        Assert.assertEquals(4, mockObjectsDir.list().length);
    }

    @Test
    public void addOfTwoFilesWithIdenticalContentsShouldOnlyAddToObjectsDirOnce() throws IOException {
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
            Assert.assertEquals(3, mockObjectsDir.list().length);
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
        Assert.assertEquals(5, mockObjectsDir.list().length);
    }

    @Test
    public void addOfDirWithSameContentsShouldOnlyAddToObjectsDirOnce() throws IOException {
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
        Assert.assertEquals(5, mockObjectsDir.list().length);

        saveStateBetweenCommands();
        mockOmniRepo.add("TempB");
        Assert.assertEquals(5, mockObjectsDir.list().length);
    }

    @Test
    public void addOfSameFileNameButDifferentContentsShouldOverrideInStage() throws IOException {
        mockOmniRepo.init();

        File mockFile = mockDir.newFile("foo.txt");
        String[] fileMsgs = {"First line", "Second line", "Third line"};
        BufferedWriter bw;
        for (int i = 0; i < fileMsgs.length; i++) {
            bw = new BufferedWriter(new FileWriter(mockFile.getPath(), true));
            bw.write(fileMsgs[i]);
            bw.close();
            saveStateBetweenCommands();
            mockOmniRepo.add(mockFile.getName());
            Assert.assertEquals(1, mockOmniRepo.getStagedFiles().size());
        }
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
        Assert.assertEquals(5, mockObjectsDir.list().length);
    }

    @Test
    public void commitClearsStagedFiles() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();

        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Committed foo!");
        Assert.assertTrue(mockOmniRepo.getStagedFiles().isEmpty());
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

    // OmniRepo.rm______________________________________________________________________________________________________

    @Test
    public void rmStagedFileShouldUnstage() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();

        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.rm("foo.txt");
        Assert.assertTrue(mockOmniRepo.getStagedFiles().isEmpty());
    }

    @Test
    public void rmTrackedFileShouldUntrack() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();

        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Committing foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.rm("foo.txt");
        Assert.assertTrue(mockOmniRepo.getTrackedFiles().isEmpty());
    }

    @Test
    public void rmTrackedFileShouldNotBeIncludedInSubsequentCommit() throws IOException {
        BufferedWriter bw;
        mockOmniRepo.init();
        saveStateBetweenCommands();

        File mockFile1 = mockDir.newFile("foo.txt");
        bw = new BufferedWriter(new FileWriter(mockFile1.getPath(), true));
        bw.write("Writing message in foo!");
        bw.close();
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Committing foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.rm("foo.txt");
        saveStateBetweenCommands();

        File mockFile2 = mockDir.newFile("bar.txt");
        bw = new BufferedWriter(new FileWriter(mockFile2.getPath(), true));
        bw.write("Writing message in bar!");
        bw.close();
        mockOmniRepo.add("bar.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Committing bar.txt");
        Assert.assertFalse(mockOmniRepo.getHead().getTracked().contains(mockFile1.getAbsolutePath()));
        Assert.assertTrue(mockOmniRepo.getHead().getTracked().contains(mockFile2.getAbsolutePath()));
    }

    @Test
    public void rmUntrackedFileShouldRemoveFileFromWorkingDir() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();

        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Committing foo.txt");
        saveStateBetweenCommands();

        Assert.assertTrue(Files.exists(Paths.get(mockDirPath, mockFile.getName())));
        mockOmniRepo.rm("foo.txt");
        Assert.assertFalse(Files.exists(Paths.get(mockDirPath, mockFile.getName())));
    }

    @Test (expected = Exception.class)
    public void rmFileNotTrackedByHeadShouldFail() throws IOException {
        mockOmniRepo.init();
        File mockFile = mockDir.newFile("foo.txt");
        saveStateBetweenCommands();
        mockOmniRepo.rm("foo.txt");
    }

    @Test (expected = Exception.class)
    public void rmFileThatDoesNotExistShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.rm("abc");
    }

    @Test (expected = Exception.class)
    public void rmInUninitializedDirectoryShouldFail() throws IOException {
        mockOmniRepo.rm("abc");
    }

    // OmniRepo.log_____________________________________________________________________________________________________

    @Test (expected = Exception.class)
    public void logInUninitializedDirectoryShouldFail() throws IOException {
        mockOmniRepo.log();
    }

    // OmniRepo.globalLog_______________________________________________________________________________________________

    @Test (expected = Exception.class)
    public void globalLogInUninitializedDirectoryShouldFail() throws IOException {
        mockOmniRepo.globalLog();
    }

    // OmniRepo.find____________________________________________________________________________________________________

    @Test
    public void findInInitializedRepoShouldFindOneCommit() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        int count = mockOmniRepo.find("Initial commit");
        Assert.assertEquals(1, count);
    }

    @Test
    public void findDuplicatedMessageOfDifferentCommitsShouldFindMultipleCommits() throws IOException {
        mockOmniRepo.init();

        String[] fileNames = {"a.txt", "b.txt", "c.txt", "d.txt", "e.txt"};
        String[] fileMsgs = {"a", "b", "c", "d", "e"};
        BufferedWriter bw;
        for (int i = 0; i < fileNames.length; i++) {
            File mockFile = mockDir.newFile(fileNames[i]);
            bw = new BufferedWriter(new FileWriter(mockFile.getPath(), true));
            bw.write(fileMsgs[i]);
            bw.close();

            saveStateBetweenCommands();
            mockOmniRepo.add(fileNames[i]);

            saveStateBetweenCommands();
            mockOmniRepo.commit("Committing a file!");

            saveStateBetweenCommands();
            Assert.assertEquals(i+1, mockOmniRepo.find("Committing a file!"));
        }
    }

    @Test
    public void findDuplicatedMessageOfIdenticalCommitsShouldFindSameCommit() throws IOException {
        mockOmniRepo.init();

        String[] fileNames = {"a.txt", "b.txt", "c.txt", "d.txt", "e.txt"};
        for (int i = 0; i < fileNames.length; i++) {
            saveStateBetweenCommands();
            File mockFile = mockDir.newFile(fileNames[i]);

            mockOmniRepo.add(fileNames[i]);
            saveStateBetweenCommands();

            mockOmniRepo.commit("Committing a file!");
            saveStateBetweenCommands();

            Assert.assertEquals(1, mockOmniRepo.find("Committing a file!"));
        }
    }

    @Test (expected = Exception.class)
    public void findNonexistentMessageShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();

        File mockFile = mockDir.newFile("foo.txt");
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Committing foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.find("Committing bar.txt");
    }

    @Test (expected = Exception.class)
    public void findInUninitializedDirectoryShouldFail() throws IOException {
        mockOmniRepo.globalLog();
    }

    // OmniRepo.status__________________________________________________________________________________________________

    @Test (expected = Exception.class)
    public void statusInUninitializedDirectoryShouldFail() throws IOException {
        mockOmniRepo.status();
    }

    // OmniRepo.checkoutFile____________________________________________________________________________________________

    // OmniRepo.checkoutCommit__________________________________________________________________________________________

    // OmniRepo.checkoutBranch__________________________________________________________________________________________

    @Test
    public void checkoutBranchShouldChangeBranchInStage() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();

        mockOmniRepo.branch("abc");
        saveStateBetweenCommands();
        Assert.assertEquals("master", mockOmniRepo.getCurrBranch().getName());

        mockOmniRepo.checkoutBranch("abc");
        Assert.assertEquals("abc", mockOmniRepo.getCurrBranch().getName());
    }

    @Test public void checkoutBranchShouldChangeHeadInStage() throws IOException {
        mockOmniRepo.init();
        String masterHead = mockOmniRepo.getHead().getSHA1();
        saveStateBetweenCommands();

        mockOmniRepo.branch("abc");
        saveStateBetweenCommands();

        mockOmniRepo.checkoutBranch("abc");
        saveStateBetweenCommands();
        Assert.assertEquals(masterHead, mockOmniRepo.getHead().getSHA1());

        File mockFile1 = mockDir.newFile("foo.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(mockFile1.getPath(), true));
        bw.write("This is foo!");
        bw.close();
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Initial commit in abc branch");
        saveStateBetweenCommands();
        Assert.assertNotEquals(masterHead, mockOmniRepo.getHead().getSHA1());

        mockOmniRepo.checkoutBranch("master");
        saveStateBetweenCommands();
        Assert.assertEquals(masterHead, mockOmniRepo.getHead().getSHA1());
    }

    @Test
    public void checkoutBranchShouldNotTrackSameFilesAsOriginalBranch() throws IOException {
        BufferedWriter bw;
        File mockFile1 = mockDir.newFile("foo.txt");
        bw = new BufferedWriter(new FileWriter(mockFile1.getPath(), true));
        bw.write("This is foo!");
        bw.close();

        File mockFile2 = mockDir.newFile("bar.txt");
        bw = new BufferedWriter(new FileWriter(mockFile2.getPath(), true));
        bw.write("This is bar!");
        bw.close();

        mockOmniRepo.init();
        saveStateBetweenCommands();

        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Initial commit in master branch");
        saveStateBetweenCommands();

        Assert.assertTrue(mockOmniRepo.getCurrBranch().getCommit().getTracked().contains(mockFile1.getName()));
        Assert.assertFalse(mockOmniRepo.getCurrBranch().getCommit().getTracked().contains(mockFile2.getName()));

        mockOmniRepo.branch("testBranch");
        saveStateBetweenCommands();

        mockOmniRepo.checkoutBranch("testBranch");
        saveStateBetweenCommands();

        mockOmniRepo.add("bar.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Second commit in testBranch branch");
        saveStateBetweenCommands();

        Assert.assertTrue(mockOmniRepo.getCurrBranch().getCommit().getTracked().contains(mockFile1.getName()));
        Assert.assertTrue(mockOmniRepo.getCurrBranch().getCommit().getTracked().contains(mockFile2.getName()));

        mockOmniRepo.checkoutBranch("master");
        saveStateBetweenCommands();

        Assert.assertTrue(mockOmniRepo.getCurrBranch().getCommit().getTracked().contains(mockFile1.getName()));
        Assert.assertFalse(mockOmniRepo.getCurrBranch().getCommit().getTracked().contains(mockFile2.getName()));
    }

    @Test
    public void checkoutBranchShouldNotContainSameFilesInRootDirAsOriginalBranch() throws IOException {
        BufferedWriter bw;
        mockOmniRepo.init();
        saveStateBetweenCommands();

        File mockFile1 = mockDir.newFile("foo.txt");
        bw = new BufferedWriter(new FileWriter(mockFile1.getPath(), true));
        bw.write("This is foo!");
        bw.close();
        mockOmniRepo.add("foo.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Initial commit in master branch");
        saveStateBetweenCommands();

        Assert.assertTrue(Files.exists(Paths.get(mockObjectsDir.getAbsolutePath(), "foo.txt")));
        Assert.assertFalse(Files.exists(Paths.get(mockObjectsDir.getAbsolutePath(), "bar.txt")));

        mockOmniRepo.branch("testBranch");
        saveStateBetweenCommands();

        mockOmniRepo.checkoutBranch("testBranch");
        saveStateBetweenCommands();

        File mockFile2 = mockDir.newFile("bar.txt");
        bw = new BufferedWriter(new FileWriter(mockFile2.getPath(), true));
        bw.write("This is bar!");
        bw.close();
        mockOmniRepo.add("bar.txt");
        saveStateBetweenCommands();

        mockOmniRepo.commit("Second commit in testBranch branch");
        saveStateBetweenCommands();

        Assert.assertTrue(Files.exists(Paths.get(mockObjectsDir.getAbsolutePath(), "foo.txt")));
        Assert.assertTrue(Files.exists(Paths.get(mockObjectsDir.getAbsolutePath(), "bar.txt")));

        mockOmniRepo.checkoutBranch("master");
        saveStateBetweenCommands();

        Assert.assertTrue(Files.exists(Paths.get(mockObjectsDir.getAbsolutePath(), "foo.txt")));
        Assert.assertFalse(Files.exists(Paths.get(mockObjectsDir.getAbsolutePath(), "bar.txt")));
    }

    @Test (expected = Exception.class)
    public void checkoutBranchOfCurrentBranchShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.checkoutBranch("master");
    }

    @Test (expected = Exception.class)
    public void checkoutBranchInUninitializedDirectoryShouldFail() throws FileNotFoundException {
        mockOmniRepo.checkoutBranch("master");
    }

    @Test (expected = Exception.class)
    public void checkoutBranchOfNonexistentBranchShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.checkoutBranch("fakeBranch");
    }

    // OmniRepo.branch__________________________________________________________________________________________________

    @Test
    public void branchCreatesHashFileInBranchesDir() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.branch("feature");
        Assert.assertTrue(Files.exists(Paths.get(mockDirPath, "/.omni/branches/feature")));
    }

    @Test (expected = Exception.class)
    public void branchOfSameNameAsExistingBranchShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.branch("master");
    }

    @Test (expected = Exception.class)
    public void branchInUninitializedDirectoryShouldFail() throws IOException {
        mockOmniRepo.branch("abc");
    }

    // OmniRepo.rmBranch________________________________________________________________________________________________

    @Test
    public void rmBranchDeletesHashFileInBranchesDir() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();

        mockOmniRepo.branch("abc");
        saveStateBetweenCommands();

        Assert.assertTrue(Files.exists(Paths.get(mockDirPath, "/.omni/branches/abc")));
        mockOmniRepo.rmBranch("abc");
        Assert.assertFalse(Files.exists(Paths.get(mockDirPath, "/.omni/branches/abc")));
    }

    @Test (expected = Exception.class)
    public void rmBranchOfMasterShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.rmBranch("master");
    }

    @Test (expected = Exception.class)
    public void rmBranchOfCurrentBranchShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();

        mockOmniRepo.branch("abc");
        saveStateBetweenCommands();

        mockOmniRepo.checkoutBranch("abc"); // FIXME: Open to implement to get test to work
        saveStateBetweenCommands();

        mockOmniRepo.rmBranch("abc");
    }

    @Test (expected = Exception.class)
    public void rmBranchOfNonexistentBranchShouldFail() throws IOException {
        mockOmniRepo.init();
        saveStateBetweenCommands();
        mockOmniRepo.rmBranch("fakeBranch");
    }

    @Test (expected = Exception.class)
    public void rmBranchInUninitializedDirectoryShouldFail() throws IOException {
        mockOmniRepo.rmBranch("abc");
    }

    // OmniRepo.reset___________________________________________________________________________________________________

    // OmniRepo.merge___________________________________________________________________________________________________

}
