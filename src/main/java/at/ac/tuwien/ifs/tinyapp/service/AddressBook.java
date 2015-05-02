package at.ac.tuwien.ifs.tinyapp.service;

import at.ac.tuwien.ifs.tinyapp.domain.Contact;

/**
 * An Address Book backed by some persistence mechanism.
 */
public interface AddressBook {

    /**
     * Returns the amount of entries in the address book.
     *
     * @return the amount of entries
     */
    long getAddressBookSize();

    /**
     * Adds the given Contact to the address book.
     *
     * @param contact the Contact to add
     */
    void add(Contact contact);
}
