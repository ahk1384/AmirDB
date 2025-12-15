package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

import java.io.IOException;
import java.util.List;

public class FindAllCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Student> execute() {
        return sm.findAll();
    }
}

