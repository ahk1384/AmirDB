package Engine;

import Models.Student;
import Storage.ArrayCollection;
import Storage.Collection;
import Storage.FileReader;
import Storage.StorageManager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ExecutionEngine {
    private Collection currentCollection;
    TransactionStack transactionStack;
    Queue<Command> batchQueue;
    private ProgramMode currentMode;
    private StorageManager sm = StorageManager.getInstance();
    public ExecutionEngine() {
        this.currentCollection = new ArrayCollection();
        this.currentMode = ProgramMode.NORMAL;
    }
    public boolean executeCommandDirectly(Command command) {
        if (command.getCommandType().equals("insertOne")) {
            if(sm.insertOne(new Student(Integer.parseInt(command.getArgs()[3]),command.getArgs()[4],Double.parseDouble(command.getArgs()[5])))){
                System.out.println("Insert successful.");
            }else{
                System.out.println("Insert failed.");
            }
        }else if (command.getCommandType().equals("deleteOne")) {
            if (sm.deleteOne(Integer.parseInt(command.getArgs()[3]))) {
                System.out.println("Delete successful.");
            } else {
                System.out.println("Delete failed.");
            }
        }
        return true;
    }
    public boolean executeCommand(Command command) {
        if (command.getCommandType().equals("insertOne")) {
            if (currentMode == ProgramMode.TRANSACTION) {
                executeCommandDirectly(command);
                command.setCommandType("deleteOne");
                command.setArgs(new String[]{
                        command.getArgs()[0],
                        command.getArgs()[1],
                        "deleteOne",
                        command.getArgs()[3]
                });
                transactionStack.push(command);
                return true;
            } else if (currentMode == ProgramMode.BATCH) {
                batchQueue.add(command);
                System.out.println("Command added to batch queue.");
                return true;
            }
            executeCommandDirectly(command);
        }else if (command.getCommandType().equals("deleteOne")) {
            if (currentMode == ProgramMode.TRANSACTION) {
                Student s = sm.findByID(Integer.parseInt(command.getArgs()[3]));
                if (s != null) {
                    executeCommandDirectly(command);
                    command.setCommandType("insertOne");
                    command.setArgs(new String[]{
                            command.getArgs()[0],
                            command.getArgs()[1],
                            "insertOne",
                            String.valueOf(s.getId()),
                            s.getName(),
                            String.valueOf(s.getGpa())
                    });
                    transactionStack.push(command);
                }
                return true;
            } else if (currentMode == ProgramMode.BATCH) {
                batchQueue.add(command);
                System.out.println("Command added to batch queue.");
                return true;
            }
            executeCommandDirectly(command);
        }else if (command.getCommandType().equals("findByID")) {
            Student st = sm.findByID(Integer.parseInt(command.getArgs()[3]));
            if (st != null) {
                System.out.println("Found Student: ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
            } else {
                System.out.println("Student not found.");
            }
        }else if (command.getCommandType().equals("findAll")) {
            Student[] students = sm.findAll();
            System.out.println("All Students:");
            for (Student st : students) {
                System.out.println("ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
            }
            return true;
        } else if (command.getCommandType().equals("import")) {
            sm.importData(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')')-1).trim());
            System.out.println("Data imported.");
        }else if (command.getCommandType().equals("filter")){
            String[] conditions = command.getArgs()[2].split(",");
            String field = conditions[0].substring(conditions[0].indexOf('(') +2, conditions[0].lastIndexOf('"')).trim();
            String value = conditions[1].substring(1, conditions[1].lastIndexOf(')')-1).trim();
            System.out.println("Filtering by " + field + " = " + value);
            for (Student st : sm.filterByField(field, value)) {
                System.out.println("ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
            }
            return true;
        } else if (command.getCommandType().equals("count")) {
            System.out.println("Total count: " + sm.count());
        } else if (command.getCommandType().equals("sum")) {
            System.out.println("Calculating sum...");
            double sum = sm.sumOfField(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')')-1).trim());
            System.out.println("Sum: " + sum);
        } else if (command.getCommandType().equals("average")) {
            System.out.println("Calculating average...");
            double avg = sm.averageOfField(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')')-1).trim());
            System.out.println("Average: " + avg);
        }else if (command.getCommandType().equals("start")) {
            if (currentMode == ProgramMode.BATCH) {
                System.out.println("Already in batch mode.");
            } else {
                currentMode = ProgramMode.BATCH;
                batchQueue = new LinkedList<>();
                System.out.println("Switched to batch mode.");
            }
        } else if (command.getCommandType().equals("execute")) {
            if (currentMode != ProgramMode.BATCH) {
                System.out.println("Not in batch mode.");
            } else {
                while (!batchQueue.isEmpty()) {
                    Command cmd = batchQueue.poll();
                    executeCommandDirectly(cmd);
                }
                currentMode = ProgramMode.NORMAL;
                batchQueue = null;
                System.out.println("Batch execution completed.");
            }
        } else if (command.getCommandType().equals("beginTransaction")) {
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
                while (!transactionStack.isEmpty()) {
                    Command cmd = transactionStack.pop();
                    executeCommandDirectly(cmd);
                }
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