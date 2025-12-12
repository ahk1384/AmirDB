package Engine;

public class Command {
    private String commandType;
    private String[] args;
    public Command(String commandType, String[] args) {
        this.commandType = commandType;
        this.args = args;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getCommandType() {
        return commandType;
    }

    public String[] getArgs() {
        return args;
    }
}
