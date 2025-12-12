package Parser;

import Engine.Command;
import Engine.ExecutionEngine;
import Models.Student;

public class QueryParser {
    public static boolean parseAndExecute(String input, ExecutionEngine engine) {

        String[] tokens = input.split("\\.");
        if (tokens.length == 0) return false;
        if (tokens.length < 2) return false;
        String cmd = "";
        Command command = null;
        if (tokens.length == 4){
            tokens[2] = tokens[2]+"."+tokens[3];
            tokens = new String[]{tokens[0], tokens[1], tokens[2]};
        }
        if (tokens.length == 3) {
             cmd = tokens[2].split("\\(")[0];
            command = new Command(cmd, tokens);
        } else if (tokens.length == 2) {
            cmd = tokens[1].split("\\(")[0];
            command = new Command(cmd, tokens);
        }
        if (command == null) return false;
        if(cmd.equals("insertOne")){
            String[] args = command.getArgs()[2].substring(command.getArgs()[2].indexOf('{') + 1,
                    command.getArgs()[2].lastIndexOf('}')).split(",");
            int id = Integer.parseInt(args[0].split(":")[1].trim());
            String name = args[1].split(":")[1].trim().replaceAll("\"", "");
            double gpa = Double.parseDouble(args[2].split(":")[1].trim());
            String [] commandArgs = new String[]{command.getArgs()[0],command.getArgs()[1],command.getArgs()[2],String.valueOf(id), name, String.valueOf(gpa)};
            command.setArgs(commandArgs);
        }
        else if (cmd.equals("deleteOne")){
            String arg = command.getArgs()[2].substring(command.getArgs()[2].indexOf('{') + 1,
                    command.getArgs()[2].lastIndexOf('}')).trim();
            int id = Integer.parseInt(arg.split(":")[1].trim());
            String [] commandArgs = new String[]{command.getArgs()[0],command.getArgs()[1],command.getArgs()[2], String.valueOf(id)};
            command.setArgs(commandArgs);
        }else if (cmd.equals("findByID")){
            String arg = command.getArgs()[2].substring(command.getArgs()[2].indexOf('(') + 1,
                    command.getArgs()[2].lastIndexOf(')')).trim();

            String [] commandArgs = new String[]{command.getArgs()[0],command.getArgs()[1],command.getArgs()[2], arg};
            command.setArgs(commandArgs);
        }
        return engine.executeCommand(command);
    }
}
