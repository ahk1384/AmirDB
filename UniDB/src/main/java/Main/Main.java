package Main;

import Engine.ExecutionEngine;
import Models.Student;
import Parser.QueryParser;
import Storage.FileReader;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        ExecutionEngine engine = new ExecutionEngine();
//        QueryParser.parseAndExecute("db.s.load-saved()", engine);
        System.out.println("UniDB Shell (Java)\nType 'exit' to quit.");
        while (true) {
            System.out.print("UniDB> ");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                QueryParser.parseAndExecute("db.s.save()", engine);
                System.out.println("Exiting UniDB Shell. Goodbye!");
                break;
            }
            QueryParser.parseAndExecute(input, engine);
        }
    }
}
