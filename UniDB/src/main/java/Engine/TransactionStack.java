package Engine;

import java.util.Stack;

public class TransactionStack {
    private Stack<Command> commandStack;

    public TransactionStack() {
        commandStack = new Stack<>();
    }

    public void push(Command command) {
        commandStack.push(command);
    }

    public Command pop() {
        return commandStack.pop();
    }

    public boolean isEmpty() {
        return commandStack.isEmpty();
    }

    public int count() {
        return commandStack.size();
    }
}
