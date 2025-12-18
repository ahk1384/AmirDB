package Parser;

import Engine.Command;
import Engine.CommandType;
import Engine.ExecutionEngine;

import java.util.*;

public class QueryParser {

    // main entry: returns engine.executeCommand(...) result
    public static String parseAndExecute(String input, ExecutionEngine engine) {
        if (input == null || engine == null) return null;
        input = input.trim();
        if (input.isEmpty()) return engine.executeCommand(makeErrorCommand("empty input"));

        try {
            int idxOpen = input.indexOf('(');
            int idxClose = input.lastIndexOf(')');
            if (idxOpen < 0 || idxClose < 0 || idxClose < idxOpen) {
                return engine.executeCommand(makeErrorCommand("missing parentheses"));
            }

            int firstDot = input.indexOf('.');
            int secondDot = input.lastIndexOf('.', idxOpen);
            if (firstDot < 0 || secondDot < 0 || secondDot == firstDot) {
                return engine.executeCommand(makeErrorCommand("expected format db.collection.method(...)"));
            }

            String db = input.substring(0, firstDot).trim();
            String collection = input.substring(firstDot + 1, secondDot).trim();
            String method = input.substring(secondDot + 1, idxOpen).trim();
            String argsInside = input.substring(idxOpen + 1, idxClose).trim();

            if (db.isEmpty() || collection.isEmpty() || method.isEmpty()) {
                return engine.executeCommand(makeErrorCommand("empty db/collection/method"));
            }

            CommandType commandType = stringToCommandType(method);
            Command command = new Command(db, collection, commandType, new String[]{db, collection, method + "(" + argsInside + ")"});

            switch (method) {
                case "insertOne": {
                    Map<String, String> obj = parseObject(argsInside);
                    if (obj == null) return engine.executeCommand(makeErrorCommand(command, "invalid object for insertOne"));
                    Integer id = parseIntFromMap(obj, "id");
                    if (id == null) id = parseIntFromMap(obj, "_id");
                    String name = getStringFromMap(obj, "name");
                    Double gpa = parseDoubleFromMap(obj, "gpa");
                    if (id == null || name == null || gpa == null) {
                        return engine.executeCommand(makeErrorCommand(command, "insertOne requires id, name, gpa"));
                    }
                    command.setArgs(new String[]{db, collection, method + "(" + argsInside + ")", String.valueOf(id), name, String.valueOf(gpa)});
                    break;
                }

                case "deleteOne": {
                    if (argsInside.startsWith("{") && argsInside.endsWith("}")) {
                        Map<String, String> obj = parseObject(argsInside);
                        if (obj == null) return engine.executeCommand(makeErrorCommand(command, "invalid object for deleteOne"));
                        Integer id = parseIntFromMap(obj, "id");
                        if (id == null) id = parseIntFromMap(obj, "_id");
                        if (id == null) return engine.executeCommand(makeErrorCommand(command, "deleteOne requires id"));
                        command.setArgs(new String[]{db, collection, method + "(" + argsInside + ")", String.valueOf(id)});
                    } else {
                        Integer id = tryParsePlainId(argsInside);
                        if (id == null) return engine.executeCommand(makeErrorCommand(command, "invalid id for deleteOne"));
                        command.setArgs(new String[]{db, collection, method + "(" + argsInside + ")", String.valueOf(id)});
                    }
                    break;
                }

                case "findByID": {
                    String idArg = stripWrappingQuotes(argsInside);
                    if (idArg == null || idArg.isEmpty()) return engine.executeCommand(makeErrorCommand(command, "empty id for findByID"));
                    command.setArgs(new String[]{db, collection, method + "(" + argsInside + ")", idArg});
                    break;
                }

                default:
                    // generic: keep raw args as single cleaned entry
                    command.setArgs(new String[]{db, collection, method + "(" + argsInside + ")", argsInside});
                    break;
            }

            return engine.executeCommand(command);
        } catch (Exception ex) {
            return engine.executeCommand(makeErrorCommand("unexpected parse error: " + ex.getMessage()));
        }
    }

    // helper to create an error Command with a message prefix the engine can detect
    private static Command makeErrorCommand(String msg) {
        return makeErrorCommand(new Command("", "", CommandType.UNKNOWN, new String[]{"", "", ""}), msg);
    }

    private static Command makeErrorCommand(Command base, String msg) {
        String err = "__ERROR__:" + (msg == null ? "unknown" : msg);
        base.setArgs(new String[]{base.getArgs() != null && base.getArgs().length > 0 ? base.getArgs()[0] : "",
                base.getArgs() != null && base.getArgs().length > 1 ? base.getArgs()[1] : "",
                base.getArgs() != null && base.getArgs().length > 2 ? base.getArgs()[2] : "", err});
        return base;
    }

    // parse a simple JSON-like top-level object into map (handles quoted strings and nested braces/brackets)
    private static Map<String, String> parseObject(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.startsWith("{") && s.endsWith("}")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        Map<String, String> map = new LinkedHashMap<>();
        StringBuilder token = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;
        int depth = 0;

        List<String> parts = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c == '"' || c == '\'') && (quoteChar == 0 || quoteChar == c)) {
                if (inQuotes && quoteChar == c) { inQuotes = false; quoteChar = 0; token.append(c); continue; }
                if (!inQuotes) { inQuotes = true; quoteChar = c; token.append(c); continue; }
            }
            if (!inQuotes) {
                if (c == '{' || c == '[') depth++;
                else if (c == '}' || c == ']') depth = Math.max(0, depth - 1);
                else if (c == ',' && depth == 0) { parts.add(token.toString()); token.setLength(0); continue; }
            }
            token.append(c);
        }
        if (token.length() > 0) parts.add(token.toString());

        for (String part : parts) {
            String p = part.trim();
            if (p.isEmpty()) continue;
            int colon = -1;
            boolean inQ = false;
            char qch = 0;
            for (int i = 0; i < p.length(); i++) {
                char c = p.charAt(i);
                if ((c == '"' || c == '\'') && (qch == 0 || qch == c)) {
                    if (inQ && qch == c) { inQ = false; qch = 0; } else if (!inQ) { inQ = true; qch = c; }
                    continue;
                }
                if (!inQ && c == ':') { colon = i; break; }
            }
            if (colon < 0) continue;
            String key = p.substring(0, colon).trim();
            String val = p.substring(colon + 1).trim();
            key = stripWrappingQuotes(key).toLowerCase(Locale.ROOT);
            val = stripWrappingQuotes(val);
            map.put(key, val);
        }
        return map;
    }

    private static String stripWrappingQuotes(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.length() >= 2) {
            if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
                return s.substring(1, s.length() - 1);
            }
        }
        return s;
    }

    private static Integer parseIntFromMap(Map<String, String> map, String key) {
        if (map == null) return null;
        String v = map.get(key.toLowerCase(Locale.ROOT));
        if (v == null) return null;
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException ex) { return null; }
    }

    private static Double parseDoubleFromMap(Map<String, String> map, String key) {
        if (map == null) return null;
        String v = map.get(key.toLowerCase(Locale.ROOT));
        if (v == null) return null;
        try { return Double.parseDouble(v.trim()); } catch (NumberFormatException ex) { return null; }
    }

    private static String getStringFromMap(Map<String, String> map, String key) {
        if (map == null) return null;
        return map.get(key.toLowerCase(Locale.ROOT));
    }

    private static Integer tryParsePlainId(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.contains(":")) {
            String[] parts = s.split(":", 2);
            return tryParseInt(parts[1].trim());
        }
        return tryParseInt(s);
    }

    private static Integer tryParseInt(String s) {
        s = stripWrappingQuotes(s);
        try { return Integer.parseInt(s); } catch (Exception ex) { return null; }
    }

    private static CommandType stringToCommandType(String method) {
        if (method == null) return CommandType.UNKNOWN;
        switch (method) {
            case "insertOne": return CommandType.INSERT_ONE;
            case "deleteOne": return CommandType.DELETE_ONE;
            case "findByID": return CommandType.FIND_BY_ID;
            case "findAll": return CommandType.FIND_ALL;
            case "import": return CommandType.IMPORT_DATA;
            case "filter": return CommandType.FILTER;
            case "save": return CommandType.SAVE;
            case "saveAs": return CommandType.SAVE_AS;
            case "load-saved": return CommandType.LOAD_SAVED;
            case "count": return CommandType.COUNT;
            case "sum": return CommandType.SUM;
            case "average": return CommandType.AVERAGE;
            case "start": return CommandType.START_BATCH;
            case "execute": return CommandType.EXECUTE_BATCH;
            case "beginTransaction": return CommandType.BEGIN_TRANSACTION;
            case "commit": return CommandType.COMMIT;
            case "rollback": return CommandType.ROLLBACK;
            case "exit": return CommandType.EXIT;
            default: return CommandType.UNKNOWN;
        }
    }
}




//package Parser;
//
//import Engine.Command;
//import Engine.ExecutionEngine;
//import Models.Student;
//
//public class QueryParser {
//    public static boolean parseAndExecute(String input, ExecutionEngine engine) {
//
//        String[] tokens = input.split("\\.");
//        if (tokens.length == 0) return false;
//        if (tokens.length < 2) return false;
//        String cmd = "";
//        Command command = null;
//        if (tokens.length == 4){
//            tokens[2] = tokens[2]+"."+tokens[3];
//            tokens = new String[]{tokens[0], tokens[1], tokens[2]};
//        }
//        if (tokens.length == 3) {
//             cmd = tokens[2].split("\\(")[0];
//            command = new Command(cmd, tokens);
//        } else if (tokens.length == 2) {
//            cmd = tokens[1].split("\\(")[0];
//            command = new Command(cmd, tokens);
//        }
//        if (command == null) return false;
//        if(cmd.equals("insertOne")){
//            String[] args = command.getArgs()[2].substring(command.getArgs()[2].indexOf('{') + 1,
//                    command.getArgs()[2].lastIndexOf('}')).split(",");
//            int id = Integer.parseInt(args[0].split(":")[1].trim());
//            String name = args[1].split(":")[1].trim().replaceAll("\"", "");
//            double gpa = Double.parseDouble(args[2].split(":")[1].trim());
//            String [] commandArgs = new String[]{command.getArgs()[0],command.getArgs()[1],command.getArgs()[2],String.valueOf(id), name, String.valueOf(gpa)};
//            command.setArgs(commandArgs);
//        }
//        else if (cmd.equals("deleteOne")){
//            String arg = command.getArgs()[2].substring(command.getArgs()[2].indexOf('{') + 1,
//                    command.getArgs()[2].lastIndexOf('}')).trim();
//            int id = Integer.parseInt(arg.split(":")[1].trim());
//            String [] commandArgs = new String[]{command.getArgs()[0],command.getArgs()[1],command.getArgs()[2], String.valueOf(id)};
//            command.setArgs(commandArgs);
//        }else if (cmd.equals("findByID")){
//            String arg = command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 1,
//                    command.getArgs()[2].lastIndexOf(')')).trim();
//
//            String [] commandArgs = new String[]{command.getArgs()[0],command.getArgs()[1],command.getArgs()[2], arg};
//            command.setArgs(commandArgs);
//        }
//        return engine.executeCommand(command);
//    }
//}
