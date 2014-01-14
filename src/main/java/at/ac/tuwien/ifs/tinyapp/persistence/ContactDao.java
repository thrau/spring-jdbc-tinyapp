package at.ac.tuwien.ifs.tinyapp.persistence;

import at.ac.tuwien.ifs.tinyapp.domain.Contact;

import java.util.List;

/**
 * ContactDao
 */
public interface ContactDao {

    long count() throws PersistenceException;

    void save(Contact contact) throws PersistenceException;

    void delete(Contact contact) throws PersistenceException;

    List<Contact> find(Contact template) throws PersistenceException;

    Contact findById(Long id) throws PersistenceException;
}
