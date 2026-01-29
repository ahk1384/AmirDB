package Engine.Commands;

import Storage.StorageManager;

import java.io.IOException;

public class DeleteAllCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean execute() {
        return sm.deleteAll();
    }
}
