# Calendar Application

A Java 21 client–server calendar application built with Maven. This project is designed with modularity, abstraction and common design patterns (such as Visitor, Factory, ...) to allow future enhancements (for example, adding a GUI, new commands or add different backing database). Currently, the application is meant to run on the command line backed with xml-file-base database.

## Modules

The project is organized as a multi-module Maven project with three sub-modules:

- **core**  
  Contains shared types (commands, responses, and models) that are exchanged between the client and server via object streams.

- **client**  
  Implements the client-side logic including network communication, command parsing, and a command-line user interface. The client connects to the server and sends commands (e.g. login, create_account, add_event, etc.) after prompting the user for input.

- **server**  
  Implements the server-side logic, including handling concurrent client connections, session management, command dispatch (using the Visitor pattern), and persistence (using XML via the default Java DOM parser). The server ensures that only logged‐in users may perform privileged actions (such as adding events).

## Building and Running

### Requirements

- Java 21 or later
- Maven

### Build

To build the entire project, run the following command from the project root:

```bash
mvn clean install
