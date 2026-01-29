package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

import java.io.IOException;

public class InsertOneCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean execute(Student student) {
        return sm.insertOne(student);
    }
}

