package at.ac.tuwien.ifs.tinyapp.persistence;

import java.util.List;

import at.ac.tuwien.ifs.tinyapp.domain.Contact;

/**
 * A repository of Contact entities.
 */
public interface ContactDao {

    /**
     * Returns the amount of entries in the underlying persistence data structure.
     * 
     * @return the amount of entries
     * @throws PersistenceException propagated persistence layer exceptions
     */
    long count() throws PersistenceException;

    /**
     * Saves the given contact. Updates an existing one, or creates a new one, based on whether the Contact has a set
     * ID.
     * 
     * @param contact the entity to save
     * @throws PersistenceException propagated persistence layer exceptions
     */
    void save(Contact contact) throws PersistenceException;

    /**
     * Deletes the given contact.
     * 
     * @param contact the entity to delete
     * @throws PersistenceException propagated persistence layer exceptions
     */
    void delete(Contact contact) throws PersistenceException;

    /**
     * Find by template. Returns an empty list if not records were found. Allows for '%' wildcards in String parameters.
     * 
     * @param template the template to build the query from
     * @return a result list
     * @throws PersistenceException propagated persistence layer exceptions
     */
    List<Contact> find(Contact template) throws PersistenceException;

    /**
     * Returns the Contact with the given id, or throws an EntityNotFoundException if the entry does not exist.
     * 
     * @param id the id of the Contact
     * @return the Contact from the repository
     * @throws PersistenceException
     * @throws EntityNotFoundException if the Contact with the given ID was not found
     */
    Contact findById(Long id) throws PersistenceException;
}
