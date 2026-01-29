package Storage;

import Models.Student;

import java.util.List;

public interface Collection {
    boolean insertOne(Student student);
    boolean deleteOne(Long id);
    Student findByID(Long id);
    List<Student> findAll();
}