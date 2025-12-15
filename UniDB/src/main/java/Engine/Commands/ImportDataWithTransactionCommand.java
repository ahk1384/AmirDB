package Engine.Commands;

import Engine.Command;
import Storage.StorageManager;

import java.io.IOException;
import java.util.List;

public class ImportDataWithTransactionCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Command> execute(String filePath) {
        return sm.importDataTransaction(filePath);
    }
}

