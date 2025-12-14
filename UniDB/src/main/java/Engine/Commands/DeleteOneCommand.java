package Engine.Commands;

import Storage.StorageManager;

public class DeleteOneCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static Boolean execute(int id) {
        return sm.deleteOne(id);
    }
}
