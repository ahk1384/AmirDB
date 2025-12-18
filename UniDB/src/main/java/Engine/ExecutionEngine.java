package Engine;

import Models.Student;
import Storage.StorageManager;
import Engine.Commands.*;

import java.io.Console;
import java.io.IOException;
import java.util.*;
import Main.ConsoleUI;
public class ExecutionEngine {

    ConsoleUI ui = new ConsoleUI();

    TransactionStack transactionStack;
    Queue<Command> batchQueue;
    private ProgramMode currentMode;
    private StorageManager sm = StorageManager.getInstance();

    Map<CommandType, CommandHandler> commandHandlers = new HashMap<>();

    public ExecutionEngine() throws IOException {
        ConsoleUI.init();
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
                if (command.getCommandType() == CommandType.INSERT_ONE ||
                        command.getCommandType() == CommandType.DELETE_ONE) {
                    if (command.getCommandType() == CommandType.INSERT_ONE) {
                        Command directCommand = new Command(command.getRoot(), command.getCollection(),
                                CommandType.INSERT_ONE_DIRECT,
                                command.getArgs());
                        handler = commandHandlers.get(CommandType.INSERT_ONE_DIRECT);
                        return handler.handle(directCommand);
                    }else if (command.getCommandType() == CommandType.DELETE_ONE) {
                        Command directCommand = new Command(command.getRoot(), command.getCollection(),
                                CommandType.DELETE_ONE_DIRECT,
                                command.getArgs());
                        handler = commandHandlers.get(CommandType.DELETE_ONE_DIRECT);
                        return handler.handle(directCommand);
                    }
                }
                return handler.handle(command);
            } catch (Exception e) {
                ui.printlnError("Command execution failed: " + e.getMessage());
                return false;
            }
        } else {
            ui.printlnError("Unknown command.");
            return false;
        }
    }

    public boolean handleInsertOneDirect(Command command) {
        if (InsertOneCommand.execute(new Student(Long.parseLong(command.getArgs()[3]), command.getArgs()[4], Double.parseDouble(command.getArgs()[5])))) {
            ui.printlnSuccess("Insert successful.");
            return true;
        } else {
            ui.printlnError("Insert failed.");
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
            ui.printlnSuccess("Command added to batch queue.");
            return true;
        } else {
            return executeCommandDirectly(command);
        }
        return false;
    }

    public boolean handleDeleteOneDirect(Command command) {
        if (DeleteOneCommand.execute(Long.parseLong(command.getArgs()[3]))) {
            ui.printlnSuccess("Delete successful.");
            return true;
        } else {
            ui.printlnError("Delete failed.");
            return false;
        }
    }

    public boolean handleDeleteOne(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            Student s = sm.findByID(Long.parseLong(command.getArgs()[3]));
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
            ui.printlnSuccess("Command added to batch queue.");
            return true;
        } else {
            return executeCommandDirectly(command);
        }
    }

    public boolean handleSave(Command command) {
        ui.printlnInfo("Saving data ...");
        if (SaveDataCommand.execute()) {
            ui.printlnSuccess("Data saved successfully.");
            return true;
        } else {
            ui.printlnError("Data saving failed.");
            return false;
        }
    }

    public boolean handleSave_As(Command command) {
        System.out.println("Saving data to " + command.getArgs()[2] + " ...");
        if (SaveDataCommand.execute(command.getArgs()[2])) {
            ui.printlnSuccess("Data saved successfully.");
            return true;
        } else {
            ui.printlnError("Data saving failed.");
            return false;
        }
    }

    public boolean handleLoad_Saved(Command command) {
        if (LoadDataCommand.execute()) {
            ui.printlnSuccess("Data loaded successfully.");
            return true;
        } else {
            ui.printlnError("Data loading failed.");
            return false;
        }
    }

    public boolean handleFindByID(Command command) {
        Student st = FindByIdCommand.execute(Long.parseLong(command.getArgs()[3]));
        if (st != null) {
            List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{String.valueOf(st.getId()), st.getName(), String.valueOf(st.getGpa())});
            ui.printTable(
                    new String[]{"ID", "Name", "GPA"},
                    rows
            );
            return true;
        } else {
            System.out.println("Student not found.");
            return false;
        }
    }

    public boolean handleFindAll(Command command) {
        List<Student> students = FindAllCommand.execute();
        ui.printlnInfo("All Students:");
        ui.printTable(
                new String[]{"ID", "Name", "GPA"},
                students.stream()
                        .map(s -> new String[]{String.valueOf(s.getId()), s.getName(), String.valueOf(s.getGpa())})
                        .toList()
        );
        return true;
    }

    public boolean handleCount(Command command) {
        ui.printlnInfo("Total count: " + CountCommand.execute());
        return true;
    }

    public boolean handleSum(Command command) {
        ui.printlnInfo("Calculating sum...");
        double sum = SumOfFieldCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                command.getArgs()[2].lastIndexOf(')') - 1).trim());
        ui.printlnInfo("Sum: " + sum);
        return true;
    }

    public boolean handleAverage(Command command) {
        ui.printlnInfo("Calculating average...");
        double avg = AverageOfFieldCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                command.getArgs()[2].lastIndexOf(')') - 1).trim());
        ui.printlnInfo("Average: " + avg);
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
                ui.printlnSuccess("Import successful.");
                return true;
            } else {
                ui.printlnError("Import failed.");
                return false;
            }
        }
        else if (currentMode == ProgramMode.BATCH) {
            batchQueue.add(command);
            ui.printlnSuccess("Command added to batch queue.");
            return true;}
        else {
            if (ImportDataWithTransactionCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')') - 1).trim()) != null) {
                ui.printlnSuccess("Import successful.");
                return true;
            } else {
                ui.printlnError("Import failed.");
                return false;
            }
        }
    }

    public boolean handleFilter(Command command) {
        String[] conditions = command.getArgs()[2].split(",");
        String field = conditions[0].substring(conditions[0].indexOf('(') + 2, conditions[0].lastIndexOf('"')).trim();
        String value = conditions[1].substring(1, conditions[1].lastIndexOf(')') - 1).trim();
        ui.printlnInfo("Filtered Students:");
        List<Student> results = FilterByFieldCommand.execute(field, value);
        List<String[]> rows = new ArrayList<>();
        for (Student st : results) {
            rows.add(new String[]{String.valueOf(st.getId()), st.getName(), String.valueOf(st.getGpa())});
        }
        ui.printTable(
                new String[]{"ID", "Name", "GPA"},
                rows
        );
        return true;
    }

    public boolean handleBeginTransaction(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            ui.printlnError("Already in transaction mode.");
        } else {
            currentMode = ProgramMode.TRANSACTION;
            transactionStack = new TransactionStack();
            ui.printlnSuccess("Switched to transaction mode.");
        }
        return true;
    }

    public boolean handleRollback(Command command) {
        if (currentMode != ProgramMode.TRANSACTION) {
            ui.printlnError("Not in transaction mode.");
        } else {
            int count = command.getArgs().length > 3 ? Integer.parseInt(command.getArgs()[3]) : -1;
            while (!transactionStack.isEmpty() && (count != 0)) {
                Command cmd = transactionStack.pop();
                executeCommandDirectly(cmd);
                if (count > 0) {
                    count--;
                }
            }
            currentMode = ProgramMode.NORMAL;
            ui.printBanner("Transaction rolled back.");
        }
        return true;
    }

    public boolean handleCommit(Command command) {
        if (currentMode != ProgramMode.TRANSACTION) {
            ui.printlnError("Not in transaction mode.");
        } else {
            currentMode = ProgramMode.NORMAL;
            transactionStack = null;
            ui.printlnSuccess("Transaction committed.");
        }
        return true;
    }

    public boolean handleExecuteBatch(Command command) {
        if (currentMode != ProgramMode.BATCH) {
            ui.printlnError("Not in batch mode.");
        } else {
            currentMode = ProgramMode.NORMAL;
            while (!batchQueue.isEmpty()) {
                Command cmd = batchQueue.poll();
                executeCommandDirectly(cmd);
            }
            batchQueue = null;
            ui.printlnSuccess("Batch executed.");
        }
        return true;
    }

    public boolean handleStartBatch(Command command) {
        if (currentMode == ProgramMode.BATCH) {
            ui.printlnError("Already in batch mode.");
        } else {
            currentMode = ProgramMode.BATCH;
            batchQueue = new LinkedList<>();
            ui.printlnSuccess("Switched to batch mode.");
        }
        return true;
    }


}