package Engine.Commands;

import Engine.Command;
import Storage.StorageManager;

import java.util.List;

public class ImportDataWithTransactionCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static List<Command> execute(String filePath) {
        return sm.importDataTransaction(filePath);
    }
}

