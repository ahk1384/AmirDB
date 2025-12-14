package Engine.Commands;

import Storage.FileWriter;
import Storage.StorageManager;

import java.util.Arrays;

public class SaveDataCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static Boolean execute() {
        return FileWriter.saveFile(Arrays.stream(sm.findAll()).toList());
    }

    public static Boolean execute(String filePath) {
        return FileWriter.saveFile(filePath, Arrays.stream(sm.findAll()).toList());
    }
}

