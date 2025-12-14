package Engine;

import Models.Student;
import Storage.StorageManager;
import Engine.Commands.*;

import java.util.*;

public class ExecutionEngine {

    TransactionStack transactionStack;
    Queue<Command> batchQueue;
    private ProgramMode currentMode;
    private StorageManager sm = StorageManager.getInstance();

    Map<CommandType, CommandHandler> commandHandlers = new HashMap<>();

    public ExecutionEngine() {
        this.currentMode = ProgramMode.NORMAL;
        commandHandlers.put(CommandType.INSERT_ONE_DIRECT, this::handleInsertOneDirect);
        commandHandlers.put(CommandType.INSERT_ONE, this::handleInsertOne);
        commandHandlers.put(CommandType.DELETE_ONE, this::handleDeleteOne);
        commandHandlers.put(CommandType.DELETE_ONE_DIRECT, this::handleDeleteOneDirect);
        commandHandlers.put(CommandType.SAVE, this::handleSave);
        commandHandlers.put(CommandType.SAVE_AS, this::handleSave_As);
        commandHandlers.put(CommandType.LOAD_SAVED, this::handleLoad_Saved);
        commandHandlers.put(CommandType.FIND_BY_ID, this::handleFindByID);
        commandHandlers.put(CommandType.FIND_ALL, this::handleFindAll);
        commandHandlers.put(CommandType.COUNT, this::handleCount);
        commandHandlers.put(CommandType.SUM, this::handleSum);
        commandHandlers.put(CommandType.AVERAGE, this::handleAverage);
        commandHandlers.put(CommandType.IMPORT_DATA, this::handleImportData);
        commandHandlers.put(CommandType.FILTER, this::handleFilter);
        commandHandlers.put(CommandType.BEGIN_TRANSACTION, this::handleBeginTransaction);
        commandHandlers.put(CommandType.ROLLBACK, this::handleRollback);
        commandHandlers.put(CommandType.COMMIT, this::handleCommit);
        commandHandlers.put(CommandType.START_BATCH, this::handleStartBatch);
        commandHandlers.put(CommandType.EXECUTE_BATCH, this::handleExecuteBatch);

    }

    public boolean executeCommand(Command command) {
        CommandHandler handler = commandHandlers.get(command.getCommandType());
        if (handler != null) {
            return handler.handle(command);
        }
        else    {
            System.out.println("Unknown command.");
            return false;
        }
    }

    public boolean executeCommandDirectly(Command command) {
        CommandHandler handler = commandHandlers.get(command.getCommandType());
        if (handler != null) {
            try {
                return handler.handle(command);
            } catch (Exception e) {
                System.out.println("Command execution failed: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Unknown command.");
            return false;
        }
    }

    public boolean handleInsertOneDirect(Command command) {
        if (InsertOneCommand.execute(new Student(Integer.parseInt(command.getArgs()[3]), command.getArgs()[4], Double.parseDouble(command.getArgs()[5])))) {
            System.out.println("Insert successful.");
            return true;
        } else {
            System.out.println("Insert failed.");
            return false;
        }
    }

    public boolean handleInsertOne(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            if (executeCommandDirectly(command)) {
                Command tmp = new Command(command.getRoot(), command.getCollection(), CommandType.DELETE_ONE,
                        new String[]{
                                command.getArgs()[0],
                                command.getArgs()[1],
                                "deleteOne",
                                command.getArgs()[3]
                        });
                transactionStack.push(tmp);
                return true;
            }
        } else if (currentMode == ProgramMode.BATCH) {
            batchQueue.add(command);
            System.out.println("Command added to batch queue.");
            return true;
        } else {
            return executeCommandDirectly(command);
        }
        return false;
    }

    public boolean handleDeleteOneDirect(Command command) {
        if (DeleteOneCommand.execute(Integer.parseInt(command.getArgs()[3]))) {
            System.out.println("Delete successful.");
            return true;
        } else {
            System.out.println("Delete failed.");
            return false;
        }
    }

    public boolean handleDeleteOne(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            Student s = sm.findByID(Integer.parseInt(command.getArgs()[3]));
            if (s != null) {
                if (executeCommandDirectly(command)) {
                    Command tmp = new Command(command.getRoot(), command.getCollection(), CommandType.INSERT_ONE,
                            new String[]{
                                    command.getArgs()[0],
                                    command.getArgs()[1],
                                    "insertOne",
                                    String.valueOf(s.getId()),
                                    s.getName(),
                                    String.valueOf(s.getGpa())
                            });
                    transactionStack.push(tmp);
                }
            }
            return true;
        } else if (currentMode == ProgramMode.BATCH) {
            batchQueue.add(command);
            System.out.println("Command added to batch queue.");
            return true;
        } else {
            return executeCommandDirectly(command);
        }
    }

    public boolean handleSave(Command command) {
        System.out.println("Saving data...");
        if (SaveDataCommand.execute()) {
            System.out.println("Data saved successfully.");
            return true;
        } else {
            System.out.println("Data saving failed.");
            return false;
        }
    }

    public boolean handleSave_As(Command command) {
        System.out.println("Saving data to " + command.getArgs()[2] + " ...");
        if (SaveDataCommand.execute(command.getArgs()[2])) {
            System.out.println("Data saved successfully.");
            return true;
        } else {
            System.out.println("Data saving failed.");
            return false;
        }
    }

    public boolean handleLoad_Saved(Command command) {
        if (LoadDataCommand.execute()) {
            System.out.println("Data loaded successfully.");
            return true;
        } else {
            System.out.println("Data loading failed.");
            return false;
        }
    }

    public boolean handleFindByID(Command command) {
        Student st = FindByIdCommand.execute(Integer.parseInt(command.getArgs()[3]));
        if (st != null) {
            System.out.println("Found Student: ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
            return true;
        } else {
            System.out.println("Student not found.");
            return false;
        }
    }

    public boolean handleFindAll(Command command) {
        Student[] students = FindAllCommand.execute();
        System.out.println("All Students:");
        for (Student st : students) {
            System.out.println("ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
        }
        return true;
    }

    public boolean handleCount(Command command) {
        System.out.println("Total count: " + CountCommand.execute());
        return true;
    }

    public boolean handleSum(Command command) {
        System.out.println("Calculating sum...");
        double sum = SumOfFieldCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                command.getArgs()[2].lastIndexOf(')') - 1).trim());
        System.out.println("Sum: " + sum);
        return true;
    }

    public boolean handleAverage(Command command) {
        System.out.println("Calculating average...");
        double avg = AverageOfFieldCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                command.getArgs()[2].lastIndexOf(')') - 1).trim());
        System.out.println("Average: " + avg);
        return true;
    }

    public boolean handleImportData(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            List<Command> rollbackCommands = ImportDataWithTransactionCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')') - 1).trim());
            if (rollbackCommands != null) {
                for (Command cmd : rollbackCommands) {
                    transactionStack.push(cmd);
                }
                System.out.println("Import successful.");
                return true;
            } else {
                System.out.println("Import failed.");
                return false;
            }
        } else {
            if (ImportDataWithTransactionCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')') - 1).trim()) != null) {
                System.out.println("Import successful.");
                return true;
            } else {
                System.out.println("Import failed.");
                return false;
            }
        }
    }

    public boolean handleFilter(Command command) {
        String[] conditions = command.getArgs()[2].split(",");
        String field = conditions[0].substring(conditions[0].indexOf('(') + 2, conditions[0].lastIndexOf('"')).trim();
        String value = conditions[1].substring(1, conditions[1].lastIndexOf(')') - 1).trim();
        System.out.println("Filtering by " + field + " = " + value);
        for (Student st : FilterByFieldCommand.execute(field, value)) {
            System.out.println("ID=" + st.getId() + ", Name=" + st.getName() + ", Gpa=" + st.getGpa());
        }
        return true;
    }

    public boolean handleBeginTransaction(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            System.out.println("Already in transaction mode.");
        } else {
            currentMode = ProgramMode.TRANSACTION;
            transactionStack = new TransactionStack();
            System.out.println("Switched to transaction mode.");
        }
        return true;
    }

    public boolean handleRollback(Command command) {
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
        return true;
    }

    public boolean handleCommit(Command command) {
        if (currentMode != ProgramMode.TRANSACTION) {
            System.out.println("Not in transaction mode.");
        } else {
            currentMode = ProgramMode.NORMAL;
            transactionStack = null;
            System.out.println("Transaction committed.");
        }
        return true;
    }

    public boolean handleExecuteBatch(Command command) {
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
        return true;
    }

    public boolean handleStartBatch(Command command) {
        if (currentMode == ProgramMode.BATCH) {
            System.out.println("Already in batch mode.");
        } else {
            currentMode = ProgramMode.BATCH;
            batchQueue = new LinkedList<>();
            System.out.println("Switched to batch mode.");
        }
        return true;
    }


}