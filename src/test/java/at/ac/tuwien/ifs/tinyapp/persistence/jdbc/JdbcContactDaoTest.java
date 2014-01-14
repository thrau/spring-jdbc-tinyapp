package at.ac.tuwien.ifs.tinyapp.persistence.jdbc;

import static org.junit.Assert.assertEquals;

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
 * Tells Spring to reload the Context after each test method, reloading the in-memory database in the process. We want a
 * clean instance of our database with the test data before each test invocation.
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
    public void findById_returnsCorrectEntry() throws Exception {
        Contact contact = dao.findById(4L);

        assertEquals(new Long(4), contact.getId());
        assertEquals("Galvin", contact.getFirstName());
        assertEquals("Colon", contact.getLastName());
        assertEquals("pede.et.risus@Aliquam.ca", contact.getEmail());
    }

    @Test(expected = EntityNotFoundException.class)
    public void findById_nonExistingEntry_throwsException() throws Exception {
        dao.findById(42L);
    }
}
