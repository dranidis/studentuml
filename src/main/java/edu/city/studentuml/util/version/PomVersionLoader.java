package edu.city.studentuml.util.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Utility class for loading the application version from properties file.
 */
public class PomVersionLoader {
    private static final Logger logger = Logger.getLogger(PomVersionLoader.class.getName());
    private static final String PROPERTIES_FILE = "my.properties";
    private static final String VERSION_PROPERTY = "version";
    private static final String DEFAULT_VERSION = "unknown";

    /**
     * Gets the current application version from the properties file.
     * 
     * @return the current version string, or "unknown" if not found
     */
    public String getCurrentVersion() {
        try (InputStream inputStream = PomVersionLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                logger.warning("Properties file not found: " + PROPERTIES_FILE);
                return DEFAULT_VERSION;
            }

            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty(VERSION_PROPERTY, DEFAULT_VERSION);

        } catch (IOException e) {
            logger.warning(() -> "Failed to load version properties: " + e.getMessage());
            return DEFAULT_VERSION;
        }
    }
}
