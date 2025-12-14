package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

public class InsertOneCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static Boolean execute(Student student) {
        return sm.insertOne(student);
    }
}

