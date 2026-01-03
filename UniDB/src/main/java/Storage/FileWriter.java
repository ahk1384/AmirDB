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
    private static String writeFile(String filePath,String content) {
        Path path = Paths.get("src/main/java/"+filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
        return content;
    }
    public static boolean saveFile(List<Student> students) {
        StringBuilder content = new StringBuilder();

        for(Student st : students){
            content.append(parseLine(st));
        }
        writeFile("Storage/data.csv",content.toString());
        return true;
    }
    public static boolean saveFile(String filePath, List<Student> students) {
        Path path = Paths.get("src/main/java/"+filePath);
        StringBuilder content = new StringBuilder();
        for(Student st : students){
            content.append(parseLine(st));
        }
        writeFile(filePath,content.toString());
        return true;
    }
    private static String parseLine(Student st){
        return st.getId()+" - "+st.getName()+" - "+st.getGpa() + System.lineSeparator();
    }
}
