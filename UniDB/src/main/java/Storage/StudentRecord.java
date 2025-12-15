package Storage;

import Models.Student;

public class StudentRecord {
    public static final int ID_SIZE = 4;
    public static final int NAME_SIZE = 50;
    public static final int GPA_SIZE = 8;
    public static final int RECORD_SIZE = ID_SIZE + NAME_SIZE * 2 + GPA_SIZE;
    private final Long id;
    private final String name;
    private final double gpa;

    public StudentRecord(Long id, String name, double gpa) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public double getGpa() { return gpa; }

    public Student ToStudent() {
        return new Student(id, name, gpa);
    }
    public static StudentRecord FromStudent(Student student) {
        return new StudentRecord(student.getId(), student.getName(), student.getGpa());
    }
    public Student toStudent() {
        return new Student(id, name, gpa);
    }
}
