package Engine.Commands;

import Storage.StorageManager;

public class LoadDataCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static Boolean execute() {
        return sm.importData("Storage/data.csv");
    }

    public static Boolean execute(String filePath) {
        return sm.importData(filePath);
    }
}

