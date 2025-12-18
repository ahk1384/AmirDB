package Engine.Commands;

import Engine.Command;
import Storage.StorageManager;

import java.io.IOException;
import java.util.List;

public class ImportDataCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean execute(String filePath) {
        return sm.importData(filePath);
    }
    public static boolean execute() {
        return sm.importData();
    }
}