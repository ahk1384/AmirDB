package Storage;

import Engine.Command;
import Engine.CommandType;
import Models.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private static StorageManager instance = null;

    private StorageManager() throws IOException {
        instance = this;
    }
    public static StorageManager getInstance() throws IOException {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    private final LinkedListCollection linkedListCollection = new LinkedListCollection();
    private final ArrayCollection arrayCollection = new ArrayCollection();

    public boolean insertOne(Models.Student student) {
        if (linkedListCollection.insertOne(student)) {
            return arrayCollection.insertOne(student);
        }
        return false;
    }

    public boolean deleteOne(Long id) {
        if (linkedListCollection.deleteOne(id)) {
            return arrayCollection.deleteOne(id);
        }
        return false;
    }

    public Models.Student findByID(Long id) {
        return linkedListCollection.findByID(id);
    }

    public List<Student> findAll() {
        List<Student> students = new java.util.ArrayList<>();
        students = arrayCollection.findAll();
        return students;
    }

    public boolean update(Student student){
        arrayCollection.update(student);
        return linkedListCollection.update(student);
    }
    public List<Command> importDataTransaction(String filePath) {
        List<Command> commands = new java.util.ArrayList<>();
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        List<Student> students = FileReader.loadFile(filePath);
        if (students.isEmpty()) {
            return null;
        }
        for (Models.Student student : students) {
            if (!insertOne(student)) {
                return null;
            } else {
                commands.add(new Command("db", "s", CommandType.DELETE_ONE, new String[]{
                        "db",
                        "s",
                        "deleteOne",
                        String.valueOf(student.getId())
                }));
            }
        }
        return commands;
    }

//    public List<Command> deleteAll(){
//        List<Command> commands = new ArrayList<>();
//        List<Student> students = findAll();
//        for (Student student : students) {
//            if (deleteOne(student.getId())) {
//                commands.add(new Command("db", "s", CommandType.INSERT_ONE, new String[]{
//                        "db",
//                        "s",
//                        "insertOne",
//                        student.toString()
//                }));
//            } else {
//                return null;
//            }
//        }
//        return commands;
//    }
//    public boolean importData() {
//        List<Student> students = FileReader.loadFile();
//        if (students.isEmpty()) {
//            return false;
//        }
//        for (Models.Student student : students) {
//            if (!insertOne(student)) {
//                return false;
//            }
//        }
//        return true;
//    }
    public boolean importData(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        List<Student> students = FileReader.loadFile(filePath);

        if (students.isEmpty()) {
            return false;
        }
        for (Models.Student student : students) {
            if (!insertOne(student)) {
                return false;
            }
        }
        return true;
    }

    public long count() {
        return arrayCollection.count();

    }

    public double sumOfField(String fieldName) {
        return arrayCollection.sumOfField(fieldName);
    }

    public double averageOfField(String fieldName) {
        return arrayCollection.averageOfField(fieldName);
    }

    public List<Student> filterByField(String fieldName, String value) {
        return arrayCollection.filter(fieldName, value);
    }

    public List<Student> filterByField(String fieldName,String start,String end){
        return arrayCollection.filterByfield(fieldName, start, end);
    }



}
