Spring JDBC Tinyapp
=====================

This is a sample application that demonstrates various aspects of spring-jdbc, and how Spring helps you with JDBC heavy
code and tests.

The application uses HSQLDB for demonstration purposes.

## Building

    mvn clean install


## Running

The `exec-maven-plugin` is configured in a way such that you may start the HSQL server with maven:

    mvn exec:java -Ddatabase


And then, in a separate terminal, run the application. Make sure you've compiled the application before you run it!

    mvn exec:java

