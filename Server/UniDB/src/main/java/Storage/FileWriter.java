package Storage;

import Models.Student;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileWriter {
    private static Path getDataFilePath() {
        return Paths.get(System.getProperty("user.dir"), "data.csv");
    }

    private static String writeFile(Path filePath, String content) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
        return content;
    }

    public static boolean saveFile(List<Student> students) {
        Path path = getDataFilePath();
        StringBuilder content = new StringBuilder();

        for (Student st : students) {
            content.append(parseLine(st));
        }
        writeFile(path, content.toString());
        return true;
    }

    public static boolean saveFile(String filePath, List<Student> students) {
        Path path = Paths.get(System.getProperty("usdber.dir"), filePath);
        StringBuilder content = new StringBuilder();
        for (Student st : students) {
            content.append(parseLine(st));
        }
        writeFile(path, content.toString());
        return true;
    }

    private static String parseLine(Student student) {
        return student.getId() + "," + student.getName() + "," + student.getGpa() + "\n";
    }
}
