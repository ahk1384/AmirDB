package Engine;

import Engine.Commands.*;
import Models.Student;
import Storage.StorageManager;

import java.util.LinkedList;
import java.util.Queue;

public class ExecutionEngine {

    TransactionStack transactionStack;
    Queue<Command> batchQueue;
    private ProgramMode currentMode;
    private StorageManager sm = StorageManager.getInstance();

    public ExecutionEngine() {

        this.currentMode = ProgramMode.NORMAL;
    }
    public boolean executeCommandDirectly(Command command) {

        if (command.getCommandType().equals("insertOne")) {
            if(InsertOneCommand.execute(new Student(Integer.parseInt(command.getArgs()[3]),command.getArgs()[4],Double.parseDouble(command.getArgs()[5])))){
                System.out.println("Insert successful.");
            }else{
                System.out.println("Insert failed.");
            }
        }else if (command.getCommandType().equals("deleteOne")) {
            if (DeleteOneCommand.execute(Integer.parseInt(command.getArgs()[3]))) {
                System.out.println("Delete successful.");
            } else {
                System.out.println("Delete failed.");
            }
        }
        return true;
    }
    public boolean executeCommand(Command command)  {
        try{
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
        }else if (command.getCommandType().equals("save")){
            System.out.println("Saving data...");
            if(SaveDataCommand.execute()){
                System.out.println("Data saved successfully.");
            } else {
                System.out.println("Data saving failed.");
            }
        }else if (command.getCommandType().equals("saveAs")){
            System.out.println("Saving data to " + command.getArgs()[2] + " ...");
            if(SaveDataCommand.execute(command.getArgs()[2])){
                System.out.println("Data saved successfully.");
            } else {
                System.out.println("Data saving failed.");
            }
        }else if (command.getCommandType().equals("load-saved")){
            System.out.println("Loading saved data from ");
            if(LoadDataCommand.execute()){
                System.out.println("Data loaded successfully.");
            } else {
                System.out.println("Data loading failed.");
            }
        }
        else if (command.getCommandType().equals("deleteOne")) {
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
            Student st = FindByIdCommand.execute(Integer.parseInt(command.getArgs()[3]));
            if (st != null) {
                System.out.println("Found Student: ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
            } else {
                System.out.println("Student not found.");
            }
        }else if (command.getCommandType().equals("findAll")) {
            Student[] students = FindAllCommand.execute();
            System.out.println("All Students:");
            for (Student st : students) {
                System.out.println("ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
            }
            return true;
        } else if (command.getCommandType().equals("import")) {
            if(ImportDataCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')')-1).trim())){
                System.out.println("Import successful.");
            }else{
                System.out.println("Import failed.");
            }

        }else if (command.getCommandType().equals("filter")){
            String[] conditions = command.getArgs()[2].split(",");
            String field = conditions[0].substring(conditions[0].indexOf('(') +2, conditions[0].lastIndexOf('"')).trim();
            String value = conditions[1].substring(1, conditions[1].lastIndexOf(')')-1).trim();
            System.out.println("Filtering by " + field + " = " + value);
            for (Student st : FilterByFieldCommand.execute(field, value)) {
                System.out.println("ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
            }
            return true;
        } else if (command.getCommandType().equals("count")) {
            System.out.println("Total count: " + CountCommand.execute());
        } else if (command.getCommandType().equals("sum")) {
            System.out.println("Calculating sum...");
            double sum = SumOfFieldCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')')-1).trim());
            System.out.println("Sum: " + sum);
        } else if (command.getCommandType().equals("average")) {
            System.out.println("Calculating average...");
            double avg = AverageOfFieldCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
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
            System.out.println("Unknown command: " + command.getCommandType());
            return false;
        }
        return true;
        }catch (Exception e){
            System.out.println("Error Occured" );
            return false;
        }
    }
}