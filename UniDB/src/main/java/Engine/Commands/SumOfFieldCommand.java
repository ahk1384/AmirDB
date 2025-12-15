package Engine.Commands;

import Storage.StorageManager;

import java.io.IOException;

public class SumOfFieldCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Double execute(String fieldName) {
        return sm.sumOfField(fieldName);
    }
}

