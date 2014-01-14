package at.ac.tuwien.ifs.tinyapp.persistence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.ifs.tinyapp.domain.Contact;
import at.ac.tuwien.ifs.tinyapp.persistence.ContactDao;
import at.ac.tuwien.ifs.tinyapp.persistence.EntityNotFoundException;
import at.ac.tuwien.ifs.tinyapp.persistence.PersistenceException;

/**
 * JdbcContactDao
 */
@Repository
public class JdbcContactDao implements ContactDao {

    @Autowired
    private JdbcTemplate jdbc;

    private RowMapper<Contact> mapper = new ContactRowMapper();

    @Override
    public long count() throws PersistenceException {
        return jdbc.queryForObject("SELECT COUNT(*) FROM CONTACTS", Long.class);
    }

    @Override
    public void save(Contact contact) throws PersistenceException {
        if (contact == null) {
            throw new IllegalArgumentException("Contact is null");
        }

        if (contact.getId() != null) {
            update(contact);
        } else {
            create(contact);
        }
    }

    @Override
    public void delete(Contact contact) throws PersistenceException {
        assertContactId(contact);

        try {
            jdbc.update("DELETE FROM CONTACTS WHERE ID = ?", contact.getId());
        } catch (DataAccessException e) {
            throw new PersistenceException("Could not remove contact with id " + contact.getId(), e);
        }
    }

    @Override
    public List<Contact> find(Contact template) throws PersistenceException {
        StringBuilder sql = new StringBuilder("SELECT * FROM");
        Map<String, Object> values = new HashMap<>();

        if (template.getFirstName() != null) {
            values.put("FIRST_NAME", template.getFirstName());
        }
        if (template.getLastName() != null) {
            values.put("LAST_NAME", template.getLastName());
        }
        if (template.getEmail() != null) {
            values.put("EMAIL", template.getEmail());
        }

        throw new UnsupportedOperationException("Can't yet find contacts by template");
    }

    @Override
    public Contact findById(Long id) throws PersistenceException {
        assertId(id);

        try {
            Contact object = new ContactByIdQuery().findObject(id);

            if (object == null) {
                throw new EntityNotFoundException("Contact with id " + id + " not found");
            }

            return object;
        } catch (DataAccessException e) {
            throw new PersistenceException("Spring JDBC error", e);
        }
    }

    private void update(Contact contact) throws PersistenceException {
        assertContactId(contact);

        String sql = "UPDATE CONTACTS SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ? WHERE ID = ?";

        try {
            jdbc.update(sql, contact.getFirstName(), contact.getLastName(), contact.getEmail(), contact.getId());
        } catch (DataAccessException e) {
            throw new PersistenceException("Spring JDBC error", e);
        }
    }

    private void create(Contact contact) throws PersistenceException {
        Map<String, Object> values = new ContactValueMap(contact);

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc).withTableName("CONTACTS").usingGeneratedKeyColumns("ID");

        try {
            Long key = (Long) insert.executeAndReturnKey(values);
            contact.setId(key);
        } catch (DataAccessException e) {
            throw new PersistenceException("Spring JDBC error", e);
        }
    }

    private static void assertId(Long id) throws PersistenceException {
        String msg;
        if (id == null) {
            msg = "Contact ID is null";
        } else if (id < 0) {
            msg = String.format("%d is not a valid Contact ID", id);
        } else {
            return;
        }

        throw new IllegalArgumentException(msg);
    }

    private static void assertContactId(Contact contact) throws PersistenceException {
        String msg;
        if (contact == null) {
            msg = "Contact is null";
        } else if (contact.getId() == null) {
            msg = "Contact ID is null";
        } else if (contact.getId() < 0) {
            msg = String.format("%d is not a valid Contact ID", contact.getId());
        } else {
            return;
        }

        throw new IllegalArgumentException(msg);
    }

    private static class ContactValueMap extends HashMap<String, Object> {
        public ContactValueMap(Contact contact) {
            put("ID", contact.getId());
            put("FIRST_NAME", contact.getFirstName());
            put("LAST_NAME", contact.getLastName());
            put("EMAIL", contact.getEmail());
        }
    }

    /**
     * Maps an SQL ResultSet to a Contact object.
     */
    private static class ContactRowMapper implements RowMapper<Contact> {
        @Override
        public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
            Contact contact = new Contact();

            contact.setId(rs.getLong("ID"));
            contact.setFirstName(rs.getString("FIRST_NAME"));
            contact.setLastName(rs.getString("LAST_NAME"));
            contact.setEmail(rs.getString("EMAIL"));

            return contact;
        }
    }

    private abstract class AbstractContactQuery extends MappingSqlQuery<Contact> {
        public AbstractContactQuery() {
            super();
            setDataSource(jdbc.getDataSource());
        }

        public AbstractContactQuery(String sql) {
            this();
            setSql(sql);
        }

        @Override
        protected Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapper.mapRow(rs, rowNum);
        }
    }

    /**
     * @link http://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/persistence.html#persistence-object
     */
    private class ContactByIdQuery extends AbstractContactQuery {

        public ContactByIdQuery() {
            super("SELECT * FROM CONTACTS WHERE ID = ?");
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
    }

    /**
     * Unfortunately Spring JDBC does not have facilities for building where clauses (like the JPA CriteriaBuilder). So
     * we have to emulate something like it.
     * 
     * @link http://stackoverflow.com/questions/6088944/spring-jdbctemplate-dynamic-where-clause
     */
    private class ContactByTemplateQuery extends AbstractContactQuery {

        private Contact template;

        public ContactByTemplateQuery(Contact template) {
            super();
            this.template = template;
            setSql(buildSql(template));
            compile();
        }

        private String buildSql(Contact template) {
            StringBuilder sql = new StringBuilder("SELECT * FROM CONTACTS");

            List<String> predicates = new ArrayList<>();

            if (template.getFirstName() != null) {
                predicates.add("FIRST_NAME LIKE :FIRST_NAME");
                declareParameter(new SqlParameter("FIRST_NAME", Types.VARCHAR));
            }
            if (template.getLastName() != null) {
                predicates.add("LAST_NAME LIKE :LAST_NAME");
                declareParameter(new SqlParameter("LAST_NAME", Types.VARCHAR));
            }
            if (template.getEmail() != null) {
                predicates.add("EMAIL LIKE :EMAIL");
                declareParameter(new SqlParameter("EMAIL", Types.VARCHAR));
            }

            if (predicates.isEmpty()) {
                return sql.toString();
            }

            // conjugate the predicates
            joinPredicates(sql, predicates);

            return sql.toString();
        }

        @Override
        public List<Contact> execute() throws DataAccessException {
            return super.execute(new ContactValueMap(template));
        }

        private void joinPredicates(StringBuilder sql, List<String> predicates) {
            int i = 0;
            for (; i < (predicates.size() - 1); i++) {
                sql.append(predicates.get(i));
                sql.append(" AND ");
            }
            sql.append(predicates.get(i));
        }

    }
}
