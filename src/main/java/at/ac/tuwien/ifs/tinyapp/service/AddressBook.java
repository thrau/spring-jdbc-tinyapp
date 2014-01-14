package at.ac.tuwien.ifs.tinyapp.service;

import at.ac.tuwien.ifs.tinyapp.domain.Contact;

/**
 * AddressBook
 */
public interface AddressBook {
    long getAddressBookSize();

    void add(Contact contact);
}
