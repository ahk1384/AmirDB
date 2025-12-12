package Main;

import Engine.ExecutionEngine;
import Models.Student;
import Parser.QueryParser;
import Storage.FileReader;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutionEngine engine = new ExecutionEngine();

        System.out.println("UniDB Shell (Java)\nType 'exit' to quit.");
        while (true) {
            System.out.print("UniDB> ");
            String input = scanner.nextLine();
            if (input.equals("exit")) break;
            QueryParser.parseAndExecute(input, engine);
        }
    }
}
