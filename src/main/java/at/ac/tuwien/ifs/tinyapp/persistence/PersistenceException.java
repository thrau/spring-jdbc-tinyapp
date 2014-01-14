package at.ac.tuwien.ifs.tinyapp.persistence;

/**
 * PersistenceException
 */
public class PersistenceException extends Exception {

    public PersistenceException() {

    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
