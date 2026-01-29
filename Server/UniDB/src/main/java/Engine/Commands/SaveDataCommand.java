package Engine.Commands;

import Storage.FileWriter;
import Storage.StorageManager;

import java.io.IOException;
import java.util.Arrays;

public class SaveDataCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean execute() {
        return FileWriter.saveFile(sm.findAll());
    }

    public static Boolean execute(String filePath) {
        return FileWriter.saveFile(filePath, sm.findAll());
    }
}

