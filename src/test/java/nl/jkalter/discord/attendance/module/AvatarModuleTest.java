package nl.jkalter.discord.attendance.module;

import org.junit.Test;

import static org.junit.Assert.*;

public class AvatarModuleTest {

    @Test
    public void getDetermineType() {
        final AvatarModule avatarModule = new AvatarModule();
        assertNull("Expecting no mime type", avatarModule.determineType("http://example.org/image.php"));
        assertEquals("Expecting jpeg mime type", "image/jpeg", avatarModule.determineType("http://example.org/image.jpeg"));
        assertEquals("Expecting jpeg mime type", "image/jpeg", avatarModule.determineType("https://example.org/folder/image.jpg"));
        assertEquals("Expecting png mime type", "image/png", avatarModule.determineType("http://example.org/image.png"));
        assertEquals("Expecting webp mime type", "image/webp", avatarModule.determineType("http://example.org/image.webp"));
        assertEquals("Expecting svg mime type", "image/svg+xml", avatarModule.determineType("http://example.org/image.svg"));
    }

}