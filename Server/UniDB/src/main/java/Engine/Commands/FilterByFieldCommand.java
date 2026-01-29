package Engine.Commands;

import Models.Student;
import Storage.StorageManager;

import java.io.IOException;
import java.util.List;

public class FilterByFieldCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Student> execute(String fieldName, Object value) {
        return sm.filterByField(fieldName, value.toString());
    }
    public static List<Student> execute(String fieldName, String start,String end){
        return sm.filterByField(fieldName,start,end);
    }
}

