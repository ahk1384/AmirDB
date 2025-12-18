package Storage;

import Models.Student;

public class StudentRecord {
    public static final int NAME_SIZE = 20;
    public static final int RECORD_SIZE = Long.BYTES
            + Character.BYTES * NAME_SIZE
            + Double.BYTES;

    private final long id;
    private final String name;
    private final double gpa;

    public StudentRecord(long id, String name, double gpa) {
        this.id = id;
        this.name = name == null ? "" : name;
        this.gpa = gpa;
    }

    public long getId() { return id; }
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
