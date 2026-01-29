package Engine.Commands;

import Storage.StorageManager;

import java.io.IOException;

public class DeleteOneCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean execute(Long id) {
        return sm.deleteOne(id);
    }
}
