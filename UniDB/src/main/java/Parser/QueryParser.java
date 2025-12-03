package Parser;

import Engine.Command;
import Engine.ExecutionEngine;

public class QueryParser {
    public static boolean parseAndExecute(String input, ExecutionEngine engine) {

        String[] tokens = input.split("\\.");
        if (tokens.length == 0) return false;
        if (tokens.length < 2) return false;
        String cmd = "";
        Command command = null;
        if (tokens.length == 3) {
             cmd = tokens[2].split("\\(")[0];
            command = new Command(cmd, tokens);
        } else if (tokens.length == 2) {
            cmd = tokens[1].split("\\(")[0];
            command = new Command(cmd, tokens);
        }
        if (command == null) return false;
        return engine.executeCommand(command);
    }
}
