package Engine.Commands;

import Storage.StorageManager;

public class SumOfFieldCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static Double execute(String fieldName) {
        return sm.sumOfField(fieldName);
    }
}

