package Engine;

import Models.Student;
import Storage.StorageManager;
import Engine.Commands.*;

import java.io.Console;
import java.io.IOException;
import java.util.*;
import Main.ConsoleUI;
import org.controlsfx.dialog.CommandLinksDialog;

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
        commandHandlers.put(CommandType.UPDATE , this :: handleUpdate);
        commandHandlers.put(CommandType.UPDATE_DIRECT,this:: handleUpdateDirect);
        commandHandlers.put(CommandType.DELETE_ALL , this :: handleDeleteAll);
    }

    public String executeCommand(Command command) {
        CommandHandler handler = commandHandlers.get(command.getCommandType());
        if (handler != null) {
            return handler.handle(command);
        }
        else    {
            ui.printlnError("Unknown Command !");
            return null;
        }
    }

    public String executeCommandDirectly(Command command) {
        CommandHandler handler = commandHandlers.get(command.getCommandType());
        if (handler != null) {
            try {
                if (command.getCommandType() == CommandType.INSERT_ONE ||
                        command.getCommandType() == CommandType.DELETE_ONE || command.getCommandType() == CommandType.UPDATE || command.getCommandType() == CommandType.DELETE_ALL) {
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
                    }else if (command.getCommandType() == CommandType.DELETE_ALL) {
                        List<Student> students = sm.findAll();
                        for (Student s : students) {
                            Command directCommand = new Command(command.getRoot(), command.getCollection(),
                                    CommandType.DELETE_ONE_DIRECT,
                                    new String[]{
                                            command.getArgs()[0],
                                            command.getArgs()[1],
                                            "deleteOne",
                                            String.valueOf(s.getId())
                                    });
                            handler = commandHandlers.get(CommandType.DELETE_ONE_DIRECT);
                            handler.handle(directCommand);
                        }
                        return ui.printlnSuccess("Delete all successful.");
                    }
                    else if (command.getCommandType() == CommandType.UPDATE) {
                        Command directCommand = new Command(command.getRoot(), command.getCollection(),
                                CommandType.UPDATE_DIRECT,
                                command.getArgs());
                        handler = commandHandlers.get(CommandType.UPDATE_DIRECT);
                    }
                }
                return handler.handle(command);
            } catch (Exception e) {
                ui.printlnError("Command execution failed: " + e.getMessage());
                return null;
            }
        } else {
            ui.printlnError("Unknown command.");
            return null;
        }
    }

    public String handleInsertOneDirect(Command command) {
        if (InsertOneCommand.execute(new Student(Long.parseLong(command.getArgs()[3]), command.getArgs()[4], Double.parseDouble(command.getArgs()[5])))) {
            return ui.printlnSuccess("Insert successful.");
        } else {
            return ui.printlnError("Insert failed.");
        }
    }

    public String handleInsertOne(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            String result = executeCommandDirectly(command);
            if (result != null) {
                Command tmp = new Command(command.getRoot(), command.getCollection(), CommandType.DELETE_ONE,
                        new String[]{
                                command.getArgs()[0],
                                command.getArgs()[1],
                                "deleteOne",
                                command.getArgs()[3]
                        });
                transactionStack.push(tmp);
            }
            return result;
        } else if (currentMode == ProgramMode.BATCH) {
            batchQueue.add(command);
            return ui.printlnSuccess("Command added to batch queue.");

        } else {
            return executeCommandDirectly(command);
        }

    }

    public String handleDeleteOneDirect(Command command) {
        if (DeleteOneCommand.execute(Long.parseLong(command.getArgs()[3]))) {
            return ui.printlnSuccess("Delete successful.");

        } else {
            return ui.printlnError("Delete failed.");
        }
    }
    public String handleDeleteAll(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            List<Student> students = sm.findAll();
            String result = executeCommandDirectly(command);
            if (result != null) {
                for (Student s : students) {
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
            return result;
        } else if (currentMode == ProgramMode.BATCH) {
            batchQueue.add(command);
            return ui.printlnSuccess("Command added to batch queue.");
        } else {
            return executeCommandDirectly(command);
        }
    }
    public String handleDeleteOne(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            Student s = sm.findByID(Long.parseLong(command.getArgs()[3]));
            if (s != null) {
                String result = executeCommandDirectly(command);
                if (result != null) {
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
                return result;
            } else {
                return ui.printlnError("Delete failed. Student not found.");
            }
        } else if (currentMode == ProgramMode.BATCH) {
            batchQueue.add(command);
            return ui.printlnSuccess("Command added to batch queue.");
        } else {
            return executeCommandDirectly(command);
        }
    }

    public String handleSave(Command command) {
        ui.printlnInfo("Saving data ...");
        if (SaveDataCommand.execute()) {
            return ui.printlnSuccess("Data saved successfully.");
        } else {
            return ui.printlnError("Data saving failed.");
        }
    }

    public String handleSave_As(Command command) {
        System.out.println("Saving data to " + command.getArgs()[2] + " ...");
        if (SaveDataCommand.execute(command.getArgs()[2])) {
            return ui.printlnSuccess("Data saved successfully.");

        } else {
            return ui.printlnError("Data saving failed.");
        }
    }

    public String handleLoad_Saved(Command command) {
        if (LoadDataCommand.execute()) {
            return ui.printlnSuccess("Data loaded successfully.");
        } else {
            return ui.printlnError("Data loading failed.");
        }
    }

    public String handleFindByID(Command command) {
        Student st = FindByIdCommand.execute(Long.parseLong(command.getArgs()[3]));
        if (st != null) {
            List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{String.valueOf(st.getId()), st.getName(), String.valueOf(st.getGpa())});
            return ui.printTable(
                    new String[]{"ID", "Name", "GPA"},
                    rows
            );

        } else {
            return ui.printlnError("Student not found.");
        }
    }

    public String handleFindAll(Command command) {
        List<Student> students = FindAllCommand.execute();
        StringBuilder result = new StringBuilder();
        result.append(ui.printlnInfo("All Students: \n"));
        result.append(ui.printTable(
                new String[]{"ID", "Name", "GPA"},
                students.stream()
                        .map(s -> new String[]{String.valueOf(s.getId()), s.getName(), String.valueOf(s.getGpa())})
                        .toList()
        ));
        return result.toString();
    }

    public String handleCount(Command command) {
        return ui.printlnInfo("Total count: " + CountCommand.execute());

    }

    public String handleSum(Command command) {
        ui.printlnInfo("Calculating sum...");
        double sum = SumOfFieldCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                command.getArgs()[2].lastIndexOf(')') - 1).trim());
        return ui.printlnInfo("Sum: " + sum);
    }

    public String handleAverage(Command command) {
        ui.printlnInfo("Calculating average...");
        double avg = AverageOfFieldCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                command.getArgs()[2].lastIndexOf(')') - 1).trim());
        return ui.printlnInfo("Average: " + avg);
    }

    public String handleUpdateDirect(Command command) {
        if (UpdateCommand.execute(new Student(Long.parseLong(command.getArgs()[3]), command.getArgs()[4], Double.parseDouble(command.getArgs()[5])))) {
            return ui.printlnSuccess("Update successful.");
        } else {
            return ui.printlnError("Update failed.");
        }
    }

    public String handleUpdate(Command command){
        if (currentMode == ProgramMode.TRANSACTION) {
            Student s = sm.findByID(Long.parseLong(command.getArgs()[3]));
            if (s != null) {
                String result = executeCommandDirectly(command);
                if (result != null) {
                    Command tmp = new Command(command.getRoot(), command.getCollection(), CommandType.UPDATE,
                            new String[]{
                                    command.getArgs()[0],
                                    command.getArgs()[1],
                                    "update",
                                    String.valueOf(s.getId()),
                                    s.getName(),
                                    String.valueOf(s.getGpa())
                            });
                    transactionStack.push(tmp);
                }
                return result;
            } else {
                return ui.printlnError("Update failed. Student not found.");
            }
        } else if (currentMode == ProgramMode.BATCH) {
            batchQueue.add(command);
            return ui.printlnSuccess("Command added to batch queue.");
        } else {
            return executeCommandDirectly(command);
        }
    }

    public String handleImportData(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            List<Command> rollbackCommands = ImportDataWithTransactionCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                    command.getArgs()[2].lastIndexOf(')') - 1).trim());
            if (rollbackCommands != null) {
                for (Command cmd : rollbackCommands) {
                    transactionStack.push(cmd);
                }
                return ui.printlnSuccess("Import successful.");

            } else {
                return ui.printlnError("Import failed.");

            }
        }
        else if (currentMode == ProgramMode.BATCH) {
            batchQueue.add(command);
            return ui.printlnSuccess("Command added to batch queue.");

        }
        else {
            if (command.getArgs()[2].equals("import()")) {
                if (ImportDataCommand.execute()){
                    return ui.printlnSuccess("Import successful.");

                } else {
                    return ui.printlnError("Import failed.");
                }
            } else {
                if (ImportDataWithTransactionCommand.execute(command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 2,
                        command.getArgs()[2].lastIndexOf(')') - 1).trim()) != null) {
                    return ui.printlnSuccess("Import successful.");

                } else {
                    return ui.printlnError("Import failed.");
                }
            }
        }
    }

    public String handleFilter(Command command) {
        List<Student> results;
        if (command.getArgs()[3] != ""){
            String[] conditions = command.getArgs()[2].split(",");
            String field = conditions[0].substring(conditions[0].indexOf('(') + 2, conditions[0].lastIndexOf('"')).trim();
            String start = conditions[1];
            String end = conditions[2].substring(0, conditions[2].lastIndexOf(')')).trim();
            ui.printlnInfo("Filtered Students:");
            results = FilterByFieldCommand.execute(field, start,end);
        }else{
            String[] conditions = command.getArgs()[2].split(",");
            String field = conditions[0].substring(conditions[0].indexOf('(') + 2, conditions[0].lastIndexOf('"')).trim();
            String value = conditions[1].substring(1, conditions[1].lastIndexOf(')') - 1).trim();
            ui.printlnInfo("Filtered Students:");
            results = FilterByFieldCommand.execute(field, value);
        }

        List<String[]> rows = new ArrayList<>();
        for (Student st : results) {
            rows.add(new String[]{String.valueOf(st.getId()), st.getName(), String.valueOf(st.getGpa())});
        }
        return (ui.printTable(
                new String[]{"ID", "Name", "GPA"},
                rows
        ));

    }

    public String handleBeginTransaction(Command command) {
        if (currentMode == ProgramMode.TRANSACTION) {
            return ui.printlnError("Already in transaction mode.");
        } else {
            currentMode = ProgramMode.TRANSACTION;
            transactionStack = new TransactionStack();
            return ui.printlnSuccess("Switched to transaction mode.");
        }

    }

    public String handleRollback(Command command) {
        if (currentMode != ProgramMode.TRANSACTION) {
            return ui.printlnError("Not in transaction mode.");
        } else {
            StringBuilder result = new StringBuilder();
            long count ;
            if (command.getArgs()[3] != ""){
                count = Long.parseLong(command.getArgs()[3]);
            }
            else{
                count = transactionStack.count();
            }
            while (!transactionStack.isEmpty() && (count != 0)) {
                Command cmd = transactionStack.pop();
                result.append(executeCommandDirectly(cmd)+"\n");
                if (count > 0) {
                    count--;
                }
            }
            currentMode = ProgramMode.NORMAL;
            result.append(ui.printBanner("Transaction rolled back."));
            return result.toString();
        }

    }

    public String handleCommit(Command command) {
        if (currentMode != ProgramMode.TRANSACTION) {
            return ui.printlnError("Not in transaction mode.");
        } else {
            currentMode = ProgramMode.NORMAL;
            transactionStack = null;
            return ui.printlnSuccess("Transaction committed.");
        }

    }

    public String handleExecuteBatch(Command command) {
        if (currentMode != ProgramMode.BATCH) {
            return ui.printlnError("Not in batch mode.");
        } else {
            currentMode = ProgramMode.NORMAL;
            StringBuilder result = new StringBuilder();
            while (!batchQueue.isEmpty()) {
                Command cmd = batchQueue.poll();
                result.append(executeCommandDirectly(cmd));
            }
            batchQueue = null;

            result.append(ui.printlnSuccess("Batch executed."));
            return result.toString();
        }

    }

    public String handleStartBatch(Command command) {
        if (currentMode == ProgramMode.BATCH) {
            return ui.printlnError("Already in batch mode.");
        } else {
            currentMode = ProgramMode.BATCH;
            batchQueue = new LinkedList<>();
            return ui.printlnSuccess("Switched to batch mode.");
        }
    }


}