package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

import java.io.IOException;

public class UpdateCommand {
    private static StorageManager sm;
    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean execute(Student student){
        return sm.update(student);
    }
}
