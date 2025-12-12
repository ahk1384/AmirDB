// java
package Parser;

import Engine.Command;
import Engine.ExecutionEngine;

import java.util.*;

public class QueryParser {

    public static boolean parseAndExecute(String input, ExecutionEngine engine) {
        if (input == null) return false;
        input = input.trim();
        try {
            int idxOpen = input.indexOf('(');
            int idxClose = input.lastIndexOf(')');
            if (idxOpen < 0 || idxClose < 0 || idxClose < idxOpen) return false;

            // find dots: first dot and the dot immediately before the method name (the last dot before '(')
            int firstDot = input.indexOf('.');
            int secondDot = input.lastIndexOf('.', idxOpen);
            if (firstDot < 0 || secondDot < 0 || secondDot == firstDot) return false;

            String db = input.substring(0, firstDot).trim();
            String collection = input.substring(firstDot + 1, secondDot).trim();
            String methodAndArgs = input.substring(secondDot + 1).trim(); // e.g. insertOne({...})
            String method = methodAndArgs.substring(0, methodAndArgs.indexOf('(')).trim();

            String argsInside = input.substring(idxOpen + 1, idxClose).trim();

            // base command
            Command command = new Command(method, new String[]{db, collection, methodAndArgs});

            // handle common methods
            switch (method) {
                case "insertOne":
                    // expect a single JSON-like object
                    Map<String, String> obj = parseObject(argsInside);
                    if (obj == null) return false;
                    // tolerant key lookup
                    Integer id = parseIntFromMap(obj, "_id");
                    String name = getStringFromMap(obj, "name");
                    Double gpa = parseDoubleFromMap(obj, "gpa");
                    if (id == null || name == null || gpa == null) return false;
                    command.setArgs(new String[]{db, collection, methodAndArgs, String.valueOf(id), name, String.valueOf(gpa)});
                    break;

                case "deleteOne":
                    // could be {id:100} or id:100 or simply 100
                    if (argsInside.startsWith("{") && argsInside.endsWith("}")) {
                        Map<String, String> delObj = parseObject(argsInside);
                        if (delObj == null) return false;
                        Integer delId = parseIntFromMap(delObj, "_id");
                        if (delId == null) return false;
                        command.setArgs(new String[]{db, collection, methodAndArgs, String.valueOf(delId)});
                    } else {
                        // try to parse plain numeric or key:value
                        Integer plainId = tryParsePlainId(argsInside);
                        if (plainId == null) return false;
                        command.setArgs(new String[]{db, collection, methodAndArgs, String.valueOf(plainId)});
                    }
                    break;

                case "findByID":
                    // either numeric or quoted string
                    String idArg = stripWrappingQuotes(argsInside);
                    command.setArgs(new String[]{db, collection, methodAndArgs, idArg});
                    break;

                default:
                    // for other methods, leave original args but include a cleaned single arg entry
                    command.setArgs(new String[]{db, collection, methodAndArgs, argsInside});
                    break;
            }

            return engine.executeCommand(command);
        } catch (Exception ex) {
            // defensive: don't propagate parsing/runtime exceptions
            return false;
        }
    }

    // parse a JSON-like object into a map of key->rawValue (strings trimmed, quotes removed)
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
        int depth = 0; // track nested braces/brackets so we only split top-level commas

        List<String> pairs = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c == '"' || c == '\'') && (quoteChar == 0 || quoteChar == c)) {
                if (inQuotes && quoteChar == c) {
                    inQuotes = false;
                    quoteChar = 0;
                    token.append(c);
                } else if (!inQuotes) {
                    inQuotes = true;
                    quoteChar = c;
                    token.append(c);
                } else {
                    token.append(c);
                }
                continue;
            }
            if (!inQuotes) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth = Math.max(0, depth - 1);
                } else if (c == ',' && depth == 0) {
                    pairs.add(token.toString());
                    token.setLength(0);
                    continue;
                }
            }
            token.append(c);
        }
        if (token.length() > 0) pairs.add(token.toString());

        for (String pair : pairs) {
            String p = pair.trim();
            if (p.isEmpty()) continue;
            // split on first colon not inside quotes
            int colonIndex = -1;
            boolean inQ = false;
            char qch = 0;
            for (int i = 0; i < p.length(); i++) {
                char c = p.charAt(i);
                if ((c == '"' || c == '\'') && (qch == 0 || qch == c)) {
                    if (inQ && qch == c) { inQ = false; qch = 0; }
                    else if (!inQ) { inQ = true; qch = c; }
                    continue;
                }
                if (!inQ && c == ':') { colonIndex = i; break; }
            }
            if (colonIndex < 0) continue;
            String key = p.substring(0, colonIndex).trim();
            String val = p.substring(colonIndex + 1).trim();
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
        String v = map.get(key.toLowerCase());
        if (v == null) return null;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Double parseDoubleFromMap(Map<String, String> map, String key) {
        if (map == null) return null;
        String v = map.get(key.toLowerCase(Locale.ROOT));
        if (v == null) return null;
        try {
            return Double.parseDouble(v.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String getStringFromMap(Map<String, String> map, String key) {
        if (map == null) return null;
        String v = map.get(key.toLowerCase(Locale.ROOT));
        return v;
    }

    private static Integer tryParsePlainId(String s) {
        if (s == null) return null;
        s = s.trim();
        // support "id:100" plain
        if (s.contains(":")) {
            String[] parts = s.split(":", 2);
            return tryParseInt(parts[1].trim());
        }
        // or just numeric
        return tryParseInt(s);
    }

    private static Integer tryParseInt(String s) {
        s = stripWrappingQuotes(s);
        try {
            return Integer.parseInt(s);
        } catch (Exception ex) {
            return null;
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
