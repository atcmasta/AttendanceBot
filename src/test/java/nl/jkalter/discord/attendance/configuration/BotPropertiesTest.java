package nl.jkalter.discord.attendance.configuration;

import nl.jkalter.discord.attendance.module.support.CommandName;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class BotPropertiesTest {
    @Test
    public void testDefaultRoles() {
        Properties properties = BotProperties.loadProperties();

        for (CommandName command : CommandName.values()) {
            assertNotNull(String.format("Expecting to find a role for the %s command.", command.toString()), properties.getProperty(command.toString()));
        }
    }

}