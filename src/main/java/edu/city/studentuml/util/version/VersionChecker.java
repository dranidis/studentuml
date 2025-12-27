package edu.city.studentuml.util.version;

import java.util.logging.Logger;

/**
 * Service class for checking if a newer version is available. Separates version
 * checking logic from UI concerns.
 */
public class VersionChecker {
    private static final Logger logger = Logger.getLogger(VersionChecker.class.getName());

    private final String latestVersion;
    private String currentVersion;

    public VersionChecker(String latestVersion, String currentVersion) {
        this.latestVersion = latestVersion;
        this.currentVersion = currentVersion;
        logger.info("VersionChecker: latestVersion = " + latestVersion + " and currentVersion = "
                + currentVersion);
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean shouldNotify() {
        return !latestVersion.isEmpty() && !latestVersion.equals(currentVersion)
                && !currentVersion.contains("SNAPSHOT");
    }
}
