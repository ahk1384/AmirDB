package Parser;

import Engine.ExecutionEngine;

public class QueryParser {
    public static boolean parseAndExecute(String input, ExecutionEngine engine) {

        String[] tokens = input.split("\\.");
        if (tokens.length == 0) return false;
        if (tokens.length < 2) return false;
        String cmd = "";
        if (tokens.length == 3) {
             cmd = tokens[2].split("\\(")[0];
        } else if (tokens.length == 2) {
            cmd = tokens[1].split("\\(")[0];
        }
        return engine.executeCommand(cmd, tokens);
    }
}
