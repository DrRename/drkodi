package drrename;

import com.github.ktools1000.io.BackupCreator;
import drrename.util.ArrayUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilTest {

    Path fileToRename = Paths.get("src/test/resources/rename-tests/some-dir/some-file.txt");

    File backupFile;

    Path newFile;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        backupFile = new BackupCreator().makeBackup(fileToRename.toFile());
    }

    @AfterEach
    void tearDown() throws IOException {
        new BackupCreator().restoreBackup(backupFile);
        backupFile.delete();
        if(newFile != null)
            Files.delete(newFile);
    }

    @Test
    void rename() throws IOException {

        var newName = "some2-file2.txt";
        ArrayUtil.rename(fileToRename, newName);
        newFile = Paths.get("src/test/resources/rename-tests/some-dir", newName);
        assertTrue(Files.exists(newFile));
        assertTrue(Files.isReadable(newFile));
    }


    @Test
    void renameConflict() throws IOException {

        var newName = "some2-file2.txt";
        newFile = Paths.get("src/test/resources/rename-tests/some-dir", newName);
        Files.createFile(newFile);

        Throwable throwable =  assertThrows(FileAlreadyExistsException.class, () -> {
            ArrayUtil.rename(fileToRename, newName);
        });
        assertEquals(FileAlreadyExistsException.class, throwable.getClass());
    }


    @Test
    void concatenate() {
    }

    @Test
    void getSubList() {
    }


}