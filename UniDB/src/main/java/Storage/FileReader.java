package Storage;

import Models.Student;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class FileReader {
    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader("src/main/java/"+filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return content.toString();
    }
    private static String readFile(Path filePath) {
        StringBuilder content = new StringBuilder();
        String path = filePath.toString();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return content.toString();
    }
    public static Optional<Path> chooseCsvFile(Component parent) {
        FileDialog fileDialog = new FileDialog((Frame) parent, "Select CSV file", FileDialog.LOAD);
        fileDialog.setFile("*.csv");
        fileDialog.setVisible(true);
        String directory = fileDialog.getDirectory();
        String file = fileDialog.getFile();
        if (directory != null && file != null) {
            return Optional.of(Path.of(directory, file));
        }
        return Optional.empty();
    }

    public static List<Student> loadFile() {
        Optional<Path> chosen= chooseCsvFile(null);
        List<Student> students = new java.util.ArrayList<>();
        String [] lines = readFile(chosen.get()).split(System.lineSeparator());
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                students.add(parseLine(line));
            }
        }
        return students;
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
