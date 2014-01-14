package at.ac.tuwien.ifs.tinyapp.persistence.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import at.ac.tuwien.ifs.tinyapp.domain.Contact;
import at.ac.tuwien.ifs.tinyapp.persistence.EntityNotFoundException;

/**
 * JdbcContactDaoTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:testContext.xml" })
/*
 * Forces Spring to reload the Context after each test method, reloading the in-memory database we set up using
 * `jdbc:embedded-database` in the process. We want a clean instance of our database with the test data before each test
 * invocation, and remove the changes the tests might have caused.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcContactDaoTest {

    @Autowired
    JdbcContactDao dao;

    @Test
    public void count_returnsCorrectCount() throws Exception {
        assertEquals(5, dao.count());
    }

    @Test
    public void count_afterSave_increases() throws Exception {
        assertEquals(5, dao.count());

        Contact c = new Contact();
        c.setFirstName("Foo");
        dao.save(c);

        assertEquals(6, dao.count());
    }

    @Test
    public void count_afterDelete_decreases() throws Exception {
        assertEquals(5, dao.count());

        Contact c = new Contact();
        c.setId(0L);
        dao.delete(c);

        assertEquals(4, dao.count());
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_withUnsetContactId_throwsException() throws Exception {
        Contact c = new Contact();
        dao.delete(c);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_withNegativeContactId_throwsException() throws Exception {
        Contact c = new Contact();

        c.setId(-1L);

        dao.delete(c);
    }

    @Test(expected = IllegalArgumentException.class)
    public void save_withInvalidId_throwsException() throws Exception {
        Contact contact = new Contact();

        contact.setId(-1L);

        dao.save(contact);
    }

    @Test
    public void save_correctlySetsId() throws Exception {
        Contact c = new Contact();

        c.setFirstName("Foo");
        c.setLastName("Bar");
        c.setEmail("foo.bar@example.com");

        dao.save(c);

        assertEquals(new Long(5), c.getId());
    }

    @Test
    public void find_withMultipleParameters_returnsCorrectEntries() throws Exception {
        Contact template = new Contact();

        template.setFirstName("%l%");
        template.setLastName("%on%");

        List<Contact> contacts = dao.find(template);

        assertEquals(1, contacts.size());

        Iterator<Contact> iterator = contacts.iterator();
        Contact contact;

        contact = iterator.next();
        assertEquals(new Long(4), contact.getId());
        assertEquals("Galvin", contact.getFirstName());
        assertEquals("Colon", contact.getLastName());
        assertEquals("pede.et.risus@Aliquam.ca", contact.getEmail());
    }

    @Test
    public void find_withWildcard_returnsCorrectEntries() throws Exception {
        Contact template = new Contact();

        template.setLastName("%on%");

        List<Contact> contacts = dao.find(template);

        assertEquals(2, contacts.size());

        Iterator<Contact> iterator = contacts.iterator();
        Contact contact;

        contact = iterator.next();
        assertEquals(new Long(2), contact.getId());
        assertEquals("Sierra", contact.getFirstName());
        assertEquals("Conway", contact.getLastName());
        assertEquals("Mauris.quis@dolor.edu", contact.getEmail());

        contact = iterator.next();
        assertEquals(new Long(4), contact.getId());
        assertEquals("Galvin", contact.getFirstName());
        assertEquals("Colon", contact.getLastName());
        assertEquals("pede.et.risus@Aliquam.ca", contact.getEmail());
    }

    @Test
    public void find_withEmptyTemplate_returnsAllEntries() throws Exception {
        List<Contact> contacts = dao.find(new Contact());

        assertEquals(5, contacts.size());

        Iterator<Contact> iterator = contacts.iterator();
        Contact contact;

        contact = iterator.next();
        assertEquals(new Long(0), contact.getId());
        assertEquals("Cassidy", contact.getFirstName());
        assertEquals("Brewer", contact.getLastName());
        assertEquals("vel@sapien.org", contact.getEmail());

        contact = iterator.next();
        assertEquals(new Long(1), contact.getId());
        assertEquals("Hakeem", contact.getFirstName());
        assertEquals("French", contact.getLastName());
        assertEquals("magna.Cras@Nulla.net", contact.getEmail());

        contact = iterator.next();
        assertEquals(new Long(2), contact.getId());
        assertEquals("Sierra", contact.getFirstName());
        assertEquals("Conway", contact.getLastName());
        assertEquals("Mauris.quis@dolor.edu", contact.getEmail());

        contact = iterator.next();
        assertEquals(new Long(3), contact.getId());
        assertEquals("Ursula", contact.getFirstName());
        assertEquals("Palmer", contact.getLastName());
        assertEquals("Donec@Fuscealiquam.com", contact.getEmail());

        contact = iterator.next();
        assertEquals(new Long(4), contact.getId());
        assertEquals("Galvin", contact.getFirstName());
        assertEquals("Colon", contact.getLastName());
        assertEquals("pede.et.risus@Aliquam.ca", contact.getEmail());
    }

    @Test
    public void find_returnsEmptyList() throws Exception {
        Contact template = new Contact();

        template.setLastName("FOOBAR");

        List<Contact> contacts = dao.find(template);

        assertNotNull(contacts);
        assertEquals(0, contacts.size());
    }

    @Test
    public void findById_returnsCorrectEntry() throws Exception {
        Contact contact = dao.findById(4L);

        assertEquals(new Long(4), contact.getId());
        assertEquals("Galvin", contact.getFirstName());
        assertEquals("Colon", contact.getLastName());
        assertEquals("pede.et.risus@Aliquam.ca", contact.getEmail());
    }

    @Test
    public void findById_afterUpdate_returnsUpdatedEntry() throws Exception {
        Contact contact = dao.findById(4L);
        contact.setLastName(null);
        contact.setEmail("foobar@example.com");

        dao.save(contact);

        contact = dao.findById(4L);

        assertEquals(new Long(4), contact.getId());
        assertEquals("Galvin", contact.getFirstName());
        assertEquals(null, contact.getLastName());
        assertEquals("foobar@example.com", contact.getEmail());
    }

    @Test(expected = EntityNotFoundException.class)
    public void findById_nonExistingEntry_throwsException() throws Exception {
        dao.findById(42L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findById_withNegativeId_throwsException() throws Exception {
        dao.findById(-1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findById_withNullId_throwsException() throws Exception {
        dao.findById(null);
    }
}
