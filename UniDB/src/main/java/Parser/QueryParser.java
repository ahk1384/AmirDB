package Parser;

import Engine.Command;
import Engine.CommandType;
import Engine.ExecutionEngine;
import Main.ConsoleUI;

import java.util.*;

public class QueryParser {
    public static String parseAndExecute(String input, ExecutionEngine engine) {
        ConsoleUI ui = new ConsoleUI();
        if (input == null || engine == null) return null;
        input = input.trim();
        if (input.isEmpty()) return ui.printlnError("empty query");

        try {
            ParsedQuery pq = parseInput(input);
            if (pq == null) return ui.printlnError("invalid query format");
            CommandType commandType = stringToCommandType(pq.method);
            Command command = new Command(pq.db, pq.collection, commandType, new String[]{pq.db, pq.collection, pq.method + "(" + pq.argsInside + ")"});
            if (commandType == CommandType.UNKNOWN) {
                return ui.printlnError("unknown command: " + pq.method);
            }
            switch (commandType) {
                case INSERT_ONE:
                case UPDATE: {
                    Map<String, String> obj = parseObject(pq.argsInside);
                    if (obj == null) return ui.printlnError("invalid object format");
                    Integer id = parseIntFromMap(obj, "id");
                    if (id == null) id = parseIntFromMap(obj, "_id");
                    String name = getStringFromMap(obj, "name");
                    Double gpa = parseDoubleFromMap(obj, "gpa");
                    if (id == null || name == null || gpa == null) {
                        return ui.printlnError("insertOne/update requires id, name, and gpa");
                    }
                    command.setArgs(new String[]{pq.db, pq.collection, pq.method + "(" + pq.argsInside + ")", String.valueOf(id), name, String.valueOf(gpa)});
                    break;
                }
                case DELETE_ONE: {
                    Integer id = extractIdForDelete(pq.argsInside);
                    if (id == null) return  ui.printlnError("invalid or missing id for deleteOne");
                    command.setArgs(new String[]{pq.db, pq.collection, pq.method + "(" + pq.argsInside + ")", String.valueOf(id)});
                    break;
                }
                case FIND_BY_ID: {
                    String idArg = stripWrappingQuotes(pq.argsInside);
                    if (idArg == null || idArg.isEmpty()) return  ui.printlnError("missing id for findByID");
                    command.setArgs(new String[]{pq.db, pq.collection, pq.method + "(" + pq.argsInside + ")", idArg});
                    break;
                }
                default:
                    command.setArgs(new String[]{pq.db, pq.collection, pq.method + "(" + pq.argsInside + ")", pq.argsInside});
                    break;
            }
            return engine.executeCommand(command);
        } catch (Exception ex) {
            return ui.printlnError("error executing command: " + ex.getMessage());
        }
    }

    private static class ParsedQuery {
        String db, collection, method, argsInside;
        ParsedQuery(String db, String collection, String method, String argsInside) {
            this.db = db; this.collection = collection; this.method = method; this.argsInside = argsInside;
        }
    }
    private static ParsedQuery parseInput(String input) {
        int idxOpen = input.indexOf('(');
        int idxClose = input.lastIndexOf(')');
        if (idxOpen < 0 || idxClose < 0 || idxClose < idxOpen) return null;
        int firstDot = input.indexOf('.');
        int secondDot = input.lastIndexOf('.', idxOpen);
        if (firstDot < 0 || secondDot < 0 || secondDot == firstDot) return null;
        String db = input.substring(0, firstDot).trim();
        String collection = input.substring(firstDot + 1, secondDot).trim();
        String method = input.substring(secondDot + 1, idxOpen).trim();
        String argsInside = input.substring(idxOpen + 1, idxClose).trim();
        if (db.isEmpty() || collection.isEmpty() || method.isEmpty()) return null;
        return new ParsedQuery(db, collection, method, argsInside);
    }

    private static Map<String, String> parseObject(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.startsWith("{") && s.endsWith("}")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        Map<String, String> map = new LinkedHashMap<>();
        if (s.isEmpty()) return map;
        String[] parts = s.split(",");
        for (String part : parts) {
            String[] kv = part.split(":", 2);
            if (kv.length != 2) continue;
            String key = kv[0].trim();
            String value = kv[1].trim();
            key = stripWrappingQuotes(key).toLowerCase(Locale.ROOT);
            value = stripWrappingQuotes(value);
            map.put(key, value);
        }
        return map;
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
    private static Integer extractIdForDelete(String argsInside) {
        if (argsInside.startsWith("{") && argsInside.endsWith("}")) {
            Map<String, String> obj = parseObject(argsInside);
            if (obj == null) return null;
            Integer id = parseIntFromMap(obj, "id");
            if (id == null) id = parseIntFromMap(obj, "_id");
            return id;
        } else {
            return tryParsePlainId(argsInside);
        }
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
            case "update" : return CommandType.UPDATE;
            case "deleteAll": return CommandType.DELETE_ALL;
            default: return CommandType.UNKNOWN;
        }
    }
    public static boolean isValidQuery(String input) {
        return parseInput(input) != null;
    }
}
