package Storage;

import Engine.Command;
import Models.Student;

import java.util.List;

public class StorageManager {
    private static StorageManager instance = null;
    private StorageManager() {
        instance = this;
    }
    public static StorageManager getInstance() {
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
    public boolean deleteOne(int id) {
        if (linkedListCollection.deleteOne(id)) {
            return arrayCollection.deleteOne(id);
        }
        return false;
    }
    public Models.Student findByID(int id) {
        return linkedListCollection.findByID(id);
    }
    public Models.Student[] findAll() {
        return arrayCollection.findAll().toArray(new Models.Student[0]);
    }
    public boolean importData(String filePath) {
        for (Models.Student student : FileReader.loadFile(filePath)) {
            if (!insertOne(student)) {
                return false;
            }
        }
        return true;
    }
    public int count(){
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

}
