package Engine;

import Storage.ArrayCollection;
import Storage.Collection;

public class ExecutionEngine {
    private Collection currentCollection;
    // TODO: Add Stack<Command> for Transactions
    // TODO: Add Queue<Command> for Batch

    public ExecutionEngine() {
        this.currentCollection = new ArrayCollection();
    }

    public boolean executeCommand(String commandType, String[] args) {
        if (commandType.equals("insertOne")) {
            System.out.println("Executing insertOne...");
        }else if (commandType.equals("deleteOne")) {
            System.out.println("Executing deleteOne...");
        }else if (commandType.equals("findByID")) {
            System.out.println("Executing findByID...");
        }else if (commandType.equals("findAll")) {
            System.out.println("Executing findAll...");
        } else if (commandType.equals("import")) {
            System.out.println("Importing data...");
        }else if (commandType.equals("filter")){
            System.out.println("Filtering data...");
        } else if (commandType.equals("count")) {
            System.out.println("Counting records...");
        } else if (commandType.equals("sum")) {
            System.out.println("Calculating sum...");
        } else if (commandType.equals("average")) {
            System.out.println("Calculating average...");
        } else if (commandType.equals("beginTransaction")) {
            System.out.println("Beginning transaction...");
        } else if (commandType.equals("rollback")) {
            System.out.println("Rolling back transaction...");
        } else if (commandType.equals("commit")) {
            System.out.println("Committing transaction...");
        } else if (commandType.equals("start")) {
            System.out.println("Starting batch processing...");
        } else if (commandType.equals("execute")) {
            System.out.println("Executing batch...");
        } else{
            return false;
        }
        return  true;
    }
}