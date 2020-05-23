# AttendanceBot
A discord attendance bot based on Discord4J (https://github.com/Discord4J/Discord4J) which can be used to register and create an overview of attendance for certain events.

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

### Commanding your bot
In order to get a list of commands, just type !help in a channel or private chat. This will list the available 
commands. For some commands further information is available bij adding the specific command to the !help command.

For instance: ```!help list``` 

### Changing command permission
The discord group that is allowed to use a specific command is defined by default in the
 [default.properties](attendance-bot/src/main/resources/default.properties). Every default can be overridden 
 individually by specifying the command in a bot.properties file in the config/ directory relative to the directory 
 where the bot is being ran.
 
 An example config/bot.properties file might look like this:
 
   ```exit=moderator```

Changes will be picked up immediately.