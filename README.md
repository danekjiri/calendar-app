# Calendar Application

A Java 21 client–server calendar application built with Maven. This project is designed with modularity, abstraction and common design patterns (such as Visitor, 'Factory', ...) to allow future enhancements (for example, adding a GUI, new commands or add different backing database). Currently, the application is meant to run on the command line backed with xml-file-base database.

## Requirements

- Java 21 or later
- Maven
- log4j, jdom2, jupiter

## Build

1. Clone the repository: `git clone https://github.com/danekjiri/calendar-app.git`
2. Navigate to the project directory: `cd calendar-app`
3. Build the project: `mvn clean install`
4. Go to `server` module: `cd server`
5. Run the server: `mvn exec:java`
6. Return to the project root: `cd ..`
7. Go to `client` module: `cd client`
8. Run how many clients you want: `mvn exec:java`

## Documentation
The project is documented using JavaDoc. To generate the documentation, run the following command from the *project root*: `mvn clean install javadoc:aggregate `.

The generated documentation will be available in the `target/reports/apidocs/` directory of the *project root*. [docs](calendar-application/target/reports/apidocs/index.html)

For user documentation, please refer to the `docs` directory. The user documentation is available in the `docs/user_guide.md` file. [user_guide](calendar-application/docs/user_guide.md)

## Modules

The project is organized as a multi-module Maven project with three sub-modules:

- **core**  
  Contains shared types (commands, responses, and models, exceptions, ...) that are exchanged between the client and server via object streams.

- **client**  
  Implements the client-side logic including network communication, command parsing, and a command-line user interface. The client connects to the server and sends commands (e.g. login, create_account, add_event, show_events etc.) after prompting the user for input and presenting the server's response.

- **server**  
  Implements the server-side logic, including handling concurrent client connections, session management, command dispatch (using the Visitor pattern), and persistence (using XML via the default Java DOM parser). The server ensures that only logged‐in users may perform privileged actions (such as adding events).

## Future Enhancements
- **Command Enhancements**: Add more commands and features to the client and server, such as event reminders, recurring events, etc.
- **GUI**: Implement a graphical user interface for the client to enhance user experience.
- **Database**: Integrate a more robust database system (e.g., PostgreSQL, MySQL) for better data management and scalability.
- ...