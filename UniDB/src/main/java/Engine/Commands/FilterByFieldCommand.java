package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

import java.util.List;

public class FilterByFieldCommand {
    private static final StorageManager sm = StorageManager.getInstance();

    public static List<Student> execute(String fieldName, Object value) {
        return sm.filterByField(fieldName, value.toString());
    }
}

