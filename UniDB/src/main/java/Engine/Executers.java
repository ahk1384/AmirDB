package Engine;

import Models.Student;
import Storage.FileWriter;
import Storage.StorageManager;

import java.security.spec.ECField;
import java.util.Arrays;
import java.util.List;

public class Executers {
    private static Executers instance = null;
    public static Executers getInstance() {
        if (instance == null) {
            instance = new Executers();
        }
        return instance;
    }
    private Executers() {
    }
    private static StorageManager sm = StorageManager.getInstance();
    public static boolean insertOne(Student student) {
        return sm.insertOne(student);
    }
    public static boolean deleteOne(int id) {
        return sm.deleteOne(id);
    }
    public static Student findByID(int id) {
        return sm.findByID(id);
    }
    public static Student[] findAll() {
        return sm.findAll();
    }
    public static double sumOfField(String fieldName) {
        return sm.sumOfField(fieldName);
    }
    public static double averageOfField(String fieldName) {
        return sm.averageOfField(fieldName);
    }
    public static List<Student> filterByField(String fieldName, Object value) {
        return sm.filterByField(fieldName, value.toString());
    }
    public static boolean importData(String filePath) {
        return sm.importData(filePath);
    }
    public static int count(){
        return sm.count();
    }
    public static boolean saveData(){
        return FileWriter.saveFile(Arrays.stream(sm.findAll()).toList());
    }
    public static boolean loadData(){
        return sm.importData("Storage/data.csv");
    }
    public static boolean loadData(String filePath){
        return sm.importData(filePath);
    }

    public static boolean saveData(String filePath){
        return FileWriter.saveFile(filePath,Arrays.stream(sm.findAll()).toList());
    }

}
