package at.ac.tuwien.ifs.tinyapp.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.ac.tuwien.ifs.tinyapp.service.AddressBook;

/**
 * CommandLineController
 */
@Component
public class CommandLineController implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CommandLineController.class);

    @Autowired
    private AddressBook addressBook;

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            String input;

            showWelcome();

            System.out.print("addressbook$ ");

            while ((input = reader.readLine()) != null) {
                if (input.isEmpty()) {
                    continue;
                }

                input = input.trim();

                if ("exit".equals(input)) {
                    tell("Okay bye!");
                    System.exit(0);
                    break;
                }

                try {
                    processCommand(input);
                } catch (UnknownCommandException e) {
                    tell("Im sorry i didn't understand '%s'", e.getCommand());
                }

                System.out.print("addressbook$ ");
            }

        } catch (IOException e) {
            LOG.error("IO Exception while reading command line input", e);
        }

        LOG.info("CommandLineController ending");
    }

    private void processCommand(String command) {

        if (command.startsWith("add")) {
            processAdd(command);
            return;
        }
        else if (command.startsWith("find")) {
            processFind(command);
            return;
        }

        switch (command) {
            case "help":
                showHelp();
                break;
            case "size":
                tell("You have %s contacts in your address book", addressBook.getAddressBookSize());
                break;
            default:
                throw new UnknownCommandException(command);
        }
    }

    private void processAdd(String command) {
        throw new UnsupportedOperationException("Cant yet add");
    }

    private void processFind(String command) {
        throw new UnsupportedOperationException("Cant yet find");
    }

    private void processList() {
        throw new UnsupportedOperationException("Cant yet list");
    }

    private void showWelcome() {
        tell("Welcome to the Tinyapp Address Book");
        showHelp();
    }

    private void showHelp() {
        tell("Commands:");
        tell("  size                 shows the amount of contacts in your address book");
        tell("  find <string>        searches the address book for the given search string");
        tell("  add <name> <email>   adds the given user to the contact list");
        tell("  list                 lists all your contacts");
        tell("  help                 shows this help message");
        tell("  exit                 exits the application");
    }

    private void tell(String msg) {
        System.out.println(msg);
    }

    private void tell(String format, Object... args) {
        System.out.printf(format + "\n", args);
    }
}
