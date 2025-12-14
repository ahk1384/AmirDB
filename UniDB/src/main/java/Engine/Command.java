package Engine;

public class Command {
    private String root;
    private String Collection ;
    private String commandType;
    private String[] args;
    public Command(String root ,String collection ,String commandType, String[] args) {
        this.commandType = commandType;
        this.args = args;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getCollection() {
        return Collection;
    }

    public void setCollection(String collection) {
        Collection = collection;
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
