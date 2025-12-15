package Engine.Commands;

import Storage.StorageManager;

import java.io.IOException;

public class LoadDataCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean execute() {
        return sm.importData("Storage/data.csv");
    }

    public static Boolean execute(String filePath) {
        return sm.importData(filePath);
    }
}

