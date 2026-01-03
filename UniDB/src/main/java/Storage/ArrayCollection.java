package Storage;

import Models.Student;

import java.util.ArrayList;
import java.util.List;

public class ArrayCollection implements Collection {
    ArrayList<Student> students ;
    public ArrayCollection() {
        students = new ArrayList<>();
    }
    public boolean insertOne(Student student) {
        return students.add(student);
    }

    public boolean deleteOne(Long id) {
        return students.removeIf(student -> student.getId() == id);
    }

    @Override
    public Student findByID(Long id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    public boolean update(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == student.getId()) {
                students.set(i, student);
                return true;
            }
        }
        return false;
    }
    public ArrayList<Student> filterByfield(String fieldName, String start , String end) {
        ArrayList<Student> result = new ArrayList<>();
        for (Student student : students) {
            switch (fieldName) {
                case "name":
                    if (student.getName().compareTo(start) >= 0 && student.getName().compareTo(end) <= 0) {
                        result.add(student);
                    }
                    break;
                case "gpa":
                    if (student.getGpa() >= Double.parseDouble(start) && student.getGpa() <= Double.parseDouble(end)) {
                        result.add(student);
                    }
                    break;
            }
        }
        return result;
    }
    public ArrayList<Student> findAll() {
        return students;
    }
    public int sumOfField(String fieldName) {
        int sum = 0;
        for (Student student : students) {
            switch (fieldName) {
                case "gpa":
                    sum += student.getGpa();
                    break;
            }
        }
        return sum;
    }
    public double averageOfField(String fieldName) {
        if (students.size() == 0) return 0;
        return (double) sumOfField(fieldName) / students.size();
    }
    public int count() {
        return students.size();
    }

    public List<Student> filter(String field, String value) {
        List<Student> result = new ArrayList<>();
        for (Student student : students) {
            if (field.equals("name") && student.getName().equals(value)) {
                result.add(student);
            }
            if (field.equals("gpa") && student.getGpa() == Double.parseDouble(value)) {
                result.add(student);
            }
        }
        return result;

    }

}