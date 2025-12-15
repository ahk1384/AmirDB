package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

import java.io.IOException;

public class FindByIdCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Student execute(Long id) {
        return sm.findByID(id);
    }
}

