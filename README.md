# AttendanceBot
A discord attendance bot which can be used to register and create an overview of attendance for certain events.

## Building
In order to build the bot for yourself you will need Maven. Run the following Maven command to build the .jar file:
```
mvn clean install
```
## Running
In order to run the Bot you will need to have the Java 8 Runtime installed. The first time you run the bot it will
create a token.txt which will need a discord token in order to be able to connect.

Example:
```
java -jar AttendanceBot-1.0.0-SNAPSHOT.jar
```

### Getting a token
Quick guide to get a token:

- Login to https://discordapp.com/developers/applications/me
- Add an application
- Add a Bot for the application and use the mentioned token (see: `Token:click to reveal`)

### Adding the bot to a server
To add your bot to a server use the following URL:

https://discordapp.com/oauth2/authorize?&client_id=CLIENT_ID&scope=bot&permissions=0

`CLIENT_ID` should be replaced with the ID for the application you have just created. (App details -> Client ID)

The log displays the correct URL with `CLIENT_ID` for your bot, once the token for the bot has been set and the bot has been started.
