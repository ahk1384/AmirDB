package Engine.Commands;

import Storage.StorageManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoadDataCommand {
    private static final StorageManager sm;

    static {
        try {
            sm = StorageManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getDataFilePath() {
        return Paths.get(System.getProperty("user.dir"), "data.csv");
    }

    public static Boolean execute() {
        return sm.importData(getDataFilePath().toString());
    }

    public static Boolean execute(String filePath) {
        Path path = Paths.get(System.getProperty("user.dir"), filePath);
        return sm.importData(path.toString());
    }
}
