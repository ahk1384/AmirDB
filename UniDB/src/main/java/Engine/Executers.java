package Engine;

import Engine.Commands.*;
import Models.Student;

import java.util.List;

/**
 * Executers class - Acts as a facade for executing commands.
 * Each operation is delegated to its corresponding command class.
 */
public class Executers {

    public static boolean insertOne(Student student) {
        return InsertOneCommand.execute(student);
    }

    public static boolean deleteOne(int id) {
        return DeleteOneCommand.execute(id);
    }

    public static Student findByID(int id) {
        return FindByIdCommand.execute(id);
    }

    public static Student[] findAll() {
        return FindAllCommand.execute();
    }

    public static double sumOfField(String fieldName) {
        return SumOfFieldCommand.execute(fieldName);
    }

    public static double averageOfField(String fieldName) {
        return AverageOfFieldCommand.execute(fieldName);
    }

    public static List<Student> filterByField(String fieldName, Object value) {
        return FilterByFieldCommand.execute(fieldName, value);
    }

    public static boolean importData(String filePath) {
        return ImportDataCommand.execute(filePath);
    }

    public static int count() {
        return CountCommand.execute();
    }

    public static boolean saveData() {
        return SaveDataCommand.execute();
    }

    public static boolean loadData() {
        return LoadDataCommand.execute();
    }

    public static boolean loadData(String filePath) {
        return LoadDataCommand.execute(filePath);
    }

    public static boolean saveData(String filePath) {
        return SaveDataCommand.execute(filePath);
    }
}
