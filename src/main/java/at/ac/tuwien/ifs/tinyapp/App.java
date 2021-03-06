package at.ac.tuwien.ifs.tinyapp;

import java.net.ConnectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

/**
 * Hello world!
 */
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final String[] configs = { "applicationContext.xml" };

    public static ApplicationContext context;

    public static void main(String[] args) {

        /**
         * Because we load the DataSource bean in the XML config, we need to catch connection exceptions while
         * bootstrapping the context.
         */
        try {
            context = new ClassPathXmlApplicationContext(configs);
        } catch (BeanCreationException e) {
            if (e.contains(ConnectException.class) || e.contains(CannotGetJdbcConnectionException.class)) {
                // obviously this is only valid for our example
                System.err.println("Could not connect to database. The exception was: " + e.getMessage());
                System.exit(1);
            } else {
                throw e;
            }
        }

        // TODO: demonstrate something useful

    }

}
