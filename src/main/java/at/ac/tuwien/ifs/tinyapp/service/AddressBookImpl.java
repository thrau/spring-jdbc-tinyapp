package at.ac.tuwien.ifs.tinyapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.tuwien.ifs.tinyapp.domain.Contact;
import at.ac.tuwien.ifs.tinyapp.persistence.ContactDao;
import at.ac.tuwien.ifs.tinyapp.persistence.PersistenceException;

/**
 * AddressBook
 */
@Service
public class AddressBookImpl implements AddressBook {

    private static final Logger LOG = LoggerFactory.getLogger(AddressBookImpl.class);

    @Autowired
    private ContactDao contacts;

    @Override
    public long getAddressBookSize() {
        try {
            return contacts.count();
        } catch (PersistenceException e) {
            LOG.error("Could not get Contacts count from Dao", e);
            return 0;
        }
    }

    @Override
    public void add(Contact contact) {
        try {
            contacts.save(contact);
        } catch (PersistenceException e) {
            LOG.error("Could not save contact", e);
        }
    }

    public void setContactDao(ContactDao contactDao) {
        this.contacts = contactDao;
    }
}
