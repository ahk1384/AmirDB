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
        System.out.println(ui.printBanner("UniDB Shell (Java)"));
        System.out.println(ui.printlnInfo("Type 'exit' to quit."));
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(ui.prompt("UniDB>"));
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                QueryParser.parseAndExecute("db.s.save()", engine);
                System.out.println(ui.printBanner("Exiting UniDB Shell. Goodbye!"));
                ui.close();
                break;
            }
            QueryParser.parseAndExecute(input, engine);
        }
    }
}
