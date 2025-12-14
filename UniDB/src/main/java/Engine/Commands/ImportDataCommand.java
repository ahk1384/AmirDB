package Engine.Commands;

import Storage.StorageManager;

public class ImportDataCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static Boolean execute(String filePath) {
        return sm.importData(filePath);
    }
}

