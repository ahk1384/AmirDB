package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

public class FindAllCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static Student[] execute() {
        return sm.findAll();
    }
}

