package at.ac.tuwien.ifs.tinyapp.persistence;

/**
 * EntityNotFoundException
 */
public class EntityNotFoundException extends PersistenceException {

    public EntityNotFoundException() {

    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
