package Engine;

import Storage.ArrayCollection;
import Storage.Collection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ExecutionEngine {
    private Collection currentCollection;
    TransactionStack transactionStack;
    Queue<Command> batchQueue;
    private ProgramMode currentMode;

    public ExecutionEngine() {
        this.currentCollection = new ArrayCollection();
        this.currentMode = ProgramMode.NORMAL;
    }
    public boolean executeCommandDirectly(Command command) {
        if (command.getCommandType().equals("insertOne")) {
            System.out.println("Executing insertOne...");
        }else if (command.getCommandType().equals("deleteOne")) {
            System.out.println("Executing deleteOne...");
        }
        return true;
    }
    public boolean executeCommand(Command command) {
        if (command.getCommandType().equals("insertOne")) {
            if (currentMode == ProgramMode.TRANSACTION) {
                transactionStack.push(command);
                System.out.println("Command queued in transaction.");
                return true;
            } else if (currentMode == ProgramMode.BATCH) {
                batchQueue.add(command);
                System.out.println("Command added to batch queue.");
                return true;
            }
            System.out.println("Executing insertOne...");
        }else if (command.getCommandType().equals("deleteOne")) {
            if (currentMode == ProgramMode.TRANSACTION) {
                transactionStack.push(command);
                System.out.println("Command queued in transaction.");
                return true;
            } else if (currentMode == ProgramMode.BATCH) {
                batchQueue.add(command);
                System.out.println("Command added to batch queue.");
                return true;
            }
            System.out.println("Executing deleteOne...");
        }else if (command.getCommandType().equals("findByID")) {
            System.out.println("Executing findByID...");
        }else if (command.getCommandType().equals("findAll")) {
            System.out.println("Executing findAll...");
        } else if (command.getCommandType().equals("import")) {
            System.out.println("Importing data...");
        }else if (command.getCommandType().equals("filter")){
            System.out.println("Filtering data...");
        } else if (command.getCommandType().equals("count")) {
            System.out.println("Counting records...");
        } else if (command.getCommandType().equals("sum")) {
            System.out.println("Calculating sum...");
        } else if (command.getCommandType().equals("average")) {
            System.out.println("Calculating average...");
        } else if (command.getCommandType().equals("beginTransaction")) {
            System.out.println("Beginning transaction...");
        } else if (command.getCommandType().equals("rollback")) {
            System.out.println("Rolling back transaction...");
        } else if (command.getCommandType().equals("commit")) {
            System.out.println("Committing transaction...");
        }else if (command.getCommandType().equals("start")) {
            if (currentMode == ProgramMode.BATCH) {
                System.out.println("Already in batch mode.");
            } else {
                currentMode = ProgramMode.BATCH;
                batchQueue = new LinkedList<>();
                System.out.println("Switched to batch mode.");
            }
        } else if (command.getCommandType().equals("execute")) {
            System.out.println("Executing batch...");
        } else if (command.getCommandType().equals("beingTransaction")) {
            if (currentMode == ProgramMode.TRANSACTION) {
                System.out.println("Already in transaction mode.");
            } else {
                currentMode = ProgramMode.TRANSACTION;
                transactionStack = new TransactionStack();
                System.out.println("Switched to transaction mode.");
            }
        } else if (command.getCommandType().equals("commit")) {
            if (currentMode != ProgramMode.TRANSACTION) {
                System.out.println("Not in transaction mode.");
            } else {
                currentMode = ProgramMode.NORMAL;
                transactionStack = null;
                System.out.println("Transaction committed.");
            }
        } else if (command.getCommandType().equals("rollback")) {
            if (currentMode != ProgramMode.TRANSACTION) {
                System.out.println("Not in transaction mode.");
            } else {
                executeCommandDirectly(transactionStack.pop());
                currentMode = ProgramMode.NORMAL;
                System.out.println("Transaction rolled back.");
            }
        }
        else {
            return false;
        }
        return true;
    }
}