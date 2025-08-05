# User guide for client-server CLI Calendar

## Introduction

The calendar application is a console-based tool designed to manage calendar events through a client-server architecture. The client-server communication is build on top of socket and commands & responses are sent & received through java serialization API. If the client is not interacting with the server for longer than 180 seconds, the session will be closed and client as to reconnect. All the calendars with theirs events are stored on server-side and linked to specific account (User account is linked to exactly one calendar). User can add, delete, modify and show events that has been sent to server using some logged account. There are two type of privileges: 

1. unlogged eg: `help` or `create_account`, ...
2. logged eg: `add_event` or `show_events`, ...


This documentation provides step-by-step instructions on how to use the application, including prerequisites, startup procedures, and detailed command usage.

## Prerequisites

Before using the application, ensure the following:

- Server Availibity: The calendar server will be running by *default* (executing with `mvn exec:java`) on address `127.0.0.1:8080`. But you can specify given port by running the server with -Dexec.args="<port>" argument, where <port> is valid port number. Make sure given port is available.
- Client connection: The calendar client will be connecting to server by *default* on address `127.0.0.1:8080`, but as with server, you can specify ip and port by starting the client with this command `mvn exec:java -Dexec.args="<server-address> <port>"`
- Dependencies: You need Java 21 and newer, Maven and potentially jdom2, log4j, junit5, mockito, javadoc
- Operating systems: multiplatform (developed on unix-like system)

## Building the application

To build and run the client-server application, follow these steps for *unix-like* systems:

1. Clone the repository:
```bash
git clone https://github.com/danekjiri/calendar-app.git
```

2. Build the project:
```bash
cd calendar-application # navigate to project root
mvn clean install javadoc:aggregate # build the project and generate documentation from comments
```

3. Run the server first:
```bash
cd server # navigate to server root
mvn exec:java # run the server with default (8080) port
# mvn exec:java -Dexec.args="12345" # for specific port
```
expected output:
```
22:38:17.442 [cz.cuni.mff.danekji.calendar.server.Server.main()] INFO  cz.cuni.mff.danekji.calendar.server.Server - Server initialized with event repository: XMLEventRepository
22:38:17.450 [cz.cuni.mff.danekji.calendar.server.Server.main()] INFO  cz.cuni.mff.danekji.calendar.server.Server - Server started on port 8080

```

4. Run the client as second:
```bash
# navigate back to project root if you 
cd client # navigate to client root
mvn exec:java # run the client with default connection to 127.0.0.1:8080
# mvn exec:java -Dexec.args="192.168.0.2 12345" # for specific address and port connection
```
expected output:
```
23:01:11.452 INFO  - Connected to '127.0.0.1:8080' with sessionId '1562431130'
Available commands:
help: Displays a list of available commands for current privilege status.
quit: Inform the server about quitting and exit the client application.
create_account: Create a new account with the provided username and password.
login: Login to the calendar system with your username and password.


$unlogged@calendar> 

```

## Available Commands

The application uses a CLI with tow privilege levels (**unlogged** and **logged**). Commands are entered at a prompt that changes based on your login status (eg.: `$unlogged > ` for unlogged user or `$username > ` for logged user). Some commands are available both when logged and unlogged. If you are not sure which commands you can you, only type `help`, and all available commands will appear.

### Unlogged commands

These commands are available wen you are not logged in:

- help: Displays a list of available commands for current privilege status.
- quit: Inform the server about quitting and exit the client application.
- create_account: Create a new account with the provided username and password.
- login: Login to the calendar system with your username and password.

### Logged commands

These commands are available only ehn logged in:

- help: Displays a list of available commands for current privilege status.
- logout: Logout from current logged account.
- add_event: Add a new event to the calendar for currently logged user.
- show_events: Displays a list of all events inserted.
- update_event: Updates the specified event by id in the currently logged users calendar.
- quit: Inform the server about quitting and exit the client application.
- create_account: Create a new account with the provided username and password.
- delete_event: Delete an event specified by id from the logged users calendar.

_*NOTE*_ The help, quit, create_account command are available in both states

## Using the commands

Below are detailed isntructions for each command with examples

### Creating a User

- This command creates a new user account in the server XML database.
- Enter: `create_account`
- At a prompt provide username and then password
- ! The username cannot be empty or *unlogged* and the password cannot be empty

Example:
```
$unlogged@calendar> create_account
Enter username: alice
Enter password: ***
Account for account 'alice' created successfully.

```

### Logging in

- This command promote the user priivileges to logged in, if valid credentials are provided.
- Enter: `login`
- At a prompt provide username and then password matching given account
- ! You have to be unlogged to proceed with this command, if any invalid credentials provided an error output will be displayed

Example:
```
$unlogged@calendar> login
Enter username: alice
Enter password: ***
Login as user 'alice' is successful.

$alice@calendar> 

```

### Adding an event

- This command add a new event to the calendar for the logged-in user, if title & date & time are provided in correct format
- Enter: `add_event`
- At a prompt provide title (required, nonempty string), date (YYYY-MM-DD format, testing for existence), time (HH:MM format, testing for existence), location (optional, could be empty, string), description (optional, could be empty, string)
- ! You have to be logged to proceed with this command, if any error occures (authorization, credentails, data, storing...) and error mesage will be displayed

Example:
```
$alice@calendar> add_event
Enter event name: date with bob
Enter event date (YYYY-MM-DD): 2025-05-01
Enter event time (HH:MM): 15:15
Enter event location (optional): Pub Local
Enter event description (optional):    
Event with id '1' added successfully.

$alice@calendar> 

```

### Showing all events

- This command list all events associated with the loged-in user.
- Enter: `show_events`

Example:
```
$alice@calendar> show_events
Event list:
        Event { id=1, title='date with bob', date='2025-05-01', time='15:15', location='Pub Local', description=''}
        Event { id=2, title='send secret', date='2025-05-23', time='12:00', location='', description=''}
        Event { id=3, title='buy flower', date='2025-05-10', time='09:00', location='Prague, city center', description='Rose or Monstera'}

$alice@calendar> 

```

### Modifying an event

- This command updates and existing event in the logged-in users calendar by specified `id`
- Enter: `update_event`
- Enter the event id of the event to modify and then override the old values with new ones, or let the prompt empty for keeping the old value. The format is the same as in [Adding an event](#adding-an-eventd)
- If some invalid data is input such as nonexisting id or invalid data in update prompt, an error message is displayed.

Example:
```
$alice@calendar> update_event
Enter the ID of the event to be updated: 1
Enter event name or left empty for previous value: date with rob
Enter event date (YYYY-MM-DD) or left empty for previous value:   
Enter event time (HH:MM) or left empty for previous value: 16:00
Enter event location or left empty for previous value: Stromovka Prague park
Enter event description or left empty for previous value: bring swimming suit
Event with id '1' updated successfully.

$alice@calendar> show_events
Event list:
        Event { id=1, title='date with rob', date='2025-05-01', time='16:00', location='Stromovka Prague park', description='bring swimming suit'}
        Event { id=2, title='send secret', date='2025-05-23', time='12:00', location='', description=''}
        Event { id=3, title='buy flower', date='2025-05-10', time='09:00', location='Prague, city center', description='Rose or Monstera'}

```

### Deleting an event

- This command removes an event from logged-in users calendar by specified id.
- Enter: `delete_event`
- At a prompt input an id of event that will be deleted
- If invalid id is passed or some error occures during the deletion process on server-side, an error message is displayed

Example:
```
$alice@calendar> delete_event
Enter the ID of the event to delete: 2
Event with id '2' deleted successfully.

$alice@calendar> show_events
Event list:
        Event { id=1, title='date with rob', date='2025-05-01', time='16:00', location='Stromovka Prague park', description='bring swimming suit'}
        Event { id=3, title='buy flower', date='2025-05-10', time='09:00', location='Prague, city center', description='Rose or Monstera'}

$alice@calendar> 

```

### Loggin Out

- This command logs out the current user and returns him to unlogged state
- Enter: `logout`
- The user have to be logged-in to be able to use this command, otherwise and error message appears

Example:
```
$alice@calendar> logout
Logout successful.

$unlogged@calendar> 
```

### Quitting the application

- This command quit the application (informs server that he can close the session with client)
- Enter: `quit`

Example:
```
$unlogged@calendar> quit
Quitting...
```

## Showing Future Events

- This command lists events for a specific upcoming period that you choose from a menu.
- Enter: `show_future_events`
- The user has to be logged-in; hw will be prompted to select a period: Tomorrow, This Week, This Month, This Year, or a Custom Date Range.

```
$alice@calendar> show_future_events
Select a period to show future events:
1. Tomorrow
2. This Week
3. This Month
4. This Year
5. Custom Date Range
   Enter your choice (1-5): 3
   Event list:
   Event { id=1, title='date with bob', date='2025-08-04', time='15:15', location='Pub Local', description=''}
   Event { id=3, title='meeting', date='2025-08-15', time='09:00', location='Prague, city center', description='SW Project meeting'}

$alice@calendar>
```
## Deleting a User Account

- This command permanently deletes your user account and all associated calendar data. This action cannot be undone
- Enter: `delete_account`
- The user has to be logged-in; he will be prompted to confirm the deletion by entering 'yes' or 'no'. If 'yes' is entered, the account and all associated data will be deleted.

```
$alice@calendar> delete_user
For security, please re-enter your password to confirm deletion: ***
Account 'alice' has been successfully deleted.

$unlogged@calendar>
```

## Server

Sever runs and tracks the sessions with clients. Right now there is no limitation of clients to be logged in, but for each client there is the 180 seconds of inactivity interval, for which he must send any command so he is not terminated by the server. Server also gives each client an sessionId which uniquelly identifies the client. The server logs each action performed by every clint with some status (info, warn, fatal...).