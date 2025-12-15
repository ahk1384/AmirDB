package Storage;

import Engine.Command;
import Engine.CommandType;
import Models.Student;

import java.io.IOException;
import java.util.List;

public class StorageManager {
    private static StorageManager instance = null;
    private RandomAccessManager ram = new RandomAccessManager();

    private StorageManager() throws IOException {
        instance = this;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private void close() throws IOException {
        if (ram != null) {
            ram.close();
        }
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

        if (ram.writeRecord(student.toStudentRecord())) {
            return true;
        }
        return false;


//        if (linkedListCollection.insertOne(student)) {
//            return arrayCollection.insertOne(student);
//        }
//        return false;
    }

    public boolean deleteOne(Long id) {
        if (ram.deleteRecordById(id)) {
            return true;
        }
        return false;
//        if (linkedListCollection.deleteOne(id)) {
//            return arrayCollection.deleteOne(id);
//        }
//        return false;
    }

    public Models.Student findByID(Long id) {
        Student record = ram.readRecordByID(id).toStudent();
        if (record != null) {
            return record;
        }
        return null;
//        return linkedListCollection.findByID(id);
    }

    public List<Student> findAll() {
        List<Student> students = new java.util.ArrayList<>();
        for (StudentRecord record : ram.readAllRecord()) {
            students.add(record.toStudent());
        }
        return students;
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
//        return arrayCollection.count();
        return ram.getRecordCount();
    }

    public double sumOfField(String fieldName) {
        return ram.sumOfFiled(fieldName);
//        return arrayCollection.sumOfField(fieldName);
    }

    public double averageOfField(String fieldName) {
        return ram.averageOfFiled(fieldName);
//        return arrayCollection.averageOfField(fieldName);
    }

    public List<Student> filterByField(String fieldName, String value) {
        List <StudentRecord> records = ram.filterByFiled(fieldName, value);
        List<Student> students = new java.util.ArrayList<>();
        for (StudentRecord record : records) {
            students.add(record.toStudent());
        }
        return students;
//        return arrayCollection.filter(fieldName, value);
    }



}
