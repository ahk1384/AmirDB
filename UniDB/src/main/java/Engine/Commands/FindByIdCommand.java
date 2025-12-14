package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

public class FindByIdCommand {
    private static final StorageManager sm = StorageManager.getInstance();
    public static Student execute(int id) {
        return sm.findByID(id);
    }
}

