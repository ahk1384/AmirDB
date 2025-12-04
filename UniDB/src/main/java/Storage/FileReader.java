package Storage;

import Models.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class FileReader {
    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    public static List<Student> loadFile(String filePath) {
        List<Student> students = new java.util.ArrayList<>();
        String [] lines = readFile(filePath).split(System.lineSeparator());
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                students.add(parseLine(line));
            }
        }
        return students;
    }
    private static Student parseLine(String line){
        String[] parts = line.split("-");
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        double gpa = Double.parseDouble(parts[2].trim());
        return new Student(id, name, gpa);
    }
}
