package at.ac.tuwien.ifs.tinyapp.cli;

import org.springframework.core.NestedRuntimeException;

/**
 * UnknownCommandException
 */
public class UnknownCommandException extends NestedRuntimeException {
    
    private final String command;
    
    public UnknownCommandException(String command) {
        super("Unknown command " + command);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
