package Engine.Commands;

import Storage.StorageManager;

public class CountCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static Integer execute() {
        return sm.count();
    }
}

