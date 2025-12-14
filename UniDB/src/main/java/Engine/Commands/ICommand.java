package Engine.Commands;

/**
 * Base interface for all command classes
 */
public interface ICommand<T> {
    T execute();
}


