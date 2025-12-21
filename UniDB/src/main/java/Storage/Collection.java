package Storage;

import Models.Student;

import java.util.List;

public interface Collection {
    boolean insertOne(Student student);
    boolean deleteOne(Long id);
    Student findByID(Long id);
    boolean update(Student student);

    List<Student> findAll();
}