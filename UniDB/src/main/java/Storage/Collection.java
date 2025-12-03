package Storage;

import Models.Student;

import java.util.List;

public interface Collection {
    boolean insertOne(Student student);
    boolean deleteOne(int id);
    Student findByID(int id);
    List<Student> findAll();
}