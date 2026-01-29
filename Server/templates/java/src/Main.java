import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ==========================================
// Domain Models
// ==========================================
class Student {
    public int id;
    public String name;
    public double gpa;

    public Student(int id, String name, double gpa) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
    }
}

// ==========================================
// Layer 3: Storage Engine (Interface)
// ==========================================
interface Collection {
    void insertOne(Student student);
    void deleteOne(int id);
    Student findByID(int id);
    List<Student> findAll();
}

class ArrayCollection implements Collection {
    // TODO: Use ArrayList or raw array
    public void insertOne(Student student) {
        // TODO: Implement
    }
    public void deleteOne(int id) {
        // TODO: Implement
    }
    public Student findByID(int id) {
        // TODO: Implement
        return null;
    }
    public List<Student> findAll() {
        return new ArrayList<>();
    }
}

class LinkedListCollection implements Collection {
    private class Node {
        Student data;
        Node next;
        Node prev;
    }
    // TODO: Manage head/tail

    public void insertOne(Student student) {
        // TODO: Implement
    }
    public void deleteOne(int id) {
        // TODO: Implement
    }
    public Student findByID(int id) {
        // TODO: Implement
        return null;
    }
    public List<Student> findAll() {
        return new ArrayList<>();
    }
}

// ==========================================
// Layer 2: Execution Engine
// ==========================================
class ExecutionEngine {
    private Collection currentCollection;
    // TODO: Add Stack<Command> for Transactions
    // TODO: Add Queue<Command> for Batch

    public ExecutionEngine() {
        this.currentCollection = new ArrayCollection();
    }

    public void executeCommand(String commandType, String[] args) {
        if (commandType.equals("insertOne")) {
            // TODO: Parse JSON-like args
            System.out.println("Executing insertOne...");
        }
        // TODO: Handle others
    }
}

// ==========================================
// Layer 1: Query Parser
// ==========================================
class QueryParser {
    public static void parseAndExecute(String input, ExecutionEngine engine) {
        // Simple tokenizer
        String[] tokens = input.split(" ");
        if (tokens.length == 0) return;

        // TODO: Improve parsing logic to handle 'db.students.insertOne({...})'
        // Current logic is just a placeholder
        String cmd = tokens[0];
        // Pass args...
        engine.executeCommand(cmd, tokens);
    }
}

// ==========================================
// Main Loop (Client Simulation)
// ==========================================
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutionEngine engine = new ExecutionEngine();

        System.out.println("UniDB Shell (Java)\nType 'exit' to quit.");

        while (true) {
            System.out.print("unidb> ");
            String input = scanner.nextLine();
            if (input.equals("exit")) break;

            QueryParser.parseAndExecute(input, engine);
        }
    }
}

