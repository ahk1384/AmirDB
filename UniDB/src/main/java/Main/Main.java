package Main;

import Engine.ExecutionEngine;
import Parser.QueryParser;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        ConsoleUI.init();
        ConsoleUI ui = new ConsoleUI();
        ExecutionEngine engine = new ExecutionEngine();
        ui.printBanner("UniDB Shell (Java)");
        ui.printlnInfo("Type 'exit' to quit.");
        Scanner sc = new Scanner(System.in);
        while (true) {
            ui.prompt("UniDB>");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                QueryParser.parseAndExecute("db.s.save()", engine);
                ui.printBanner("Exiting UniDB Shell. Goodbye!");
                ui.close();
                break;
            }
            QueryParser.parseAndExecute(input, engine);
        }
    }
}
