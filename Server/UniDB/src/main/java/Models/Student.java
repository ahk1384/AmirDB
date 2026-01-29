package Models;

import Storage.StudentRecord;

import java.io.Serializable;

public class Student implements Serializable {
    public long id;
    public String name;
    public double gpa;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public Student(long id, String name, double gpa) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
    }
    public StudentRecord toStudentRecord() {
        return new StudentRecord(id, name, gpa);
    }
}