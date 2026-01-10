package edu.city.studentuml.util.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import org.json.JSONObject;

/**
 * GitHub-based implementation of VersionProvider. Fetches the latest release
 * version from GitHub Releases API.
 */
public class GitHubVersionProvider {
    private static final Logger logger = Logger.getLogger(GitHubVersionProvider.class.getName());

    private final String apiUrl;

    public GitHubVersionProvider(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getLatestVersion() {
        String jsonData = fetchFromURL(apiUrl);

        if (jsonData.isEmpty()) {
            return "";
        }

        return parseVersionFromJSON(jsonData);
    }

    /**
     * Fetches JSON data from a URL. Package-private for testing.
     * 
     * @param url the URL to fetch from
     * @return the response body as a string, or empty string on error
     */
    String fetchFromURL(String url) {
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();

            logger.info(() -> "GET " + url + " STATUS: " + status);

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }
            connection.disconnect();

            logger.finest(content::toString);
            return content.toString();

        } catch (IOException e) {
            logger.warning(() -> "Failed to connect to " + url + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * Parses version from GitHub releases API JSON response. Package-private for
     * testing.
     * 
     * @param jsonData the JSON response from GitHub API
     * @return the version string, or empty string if parsing fails
     */
    String parseVersionFromJSON(String jsonData) {
        try {
            JSONObject obj = new JSONObject(jsonData);
            String tagName = obj.getString("tag_name");

            // Remove 'v' or 'V' prefix if present (e.g., "v1.3.1" -> "1.3.1")
            String version = (tagName.startsWith("v") || tagName.startsWith("V"))
                    ? tagName.substring(1)
                    : tagName;

            logger.info(() -> "Latest release tag: " + version);
            return version;

        } catch (Exception e) {
            logger.warning(() -> "Failed to parse GitHub release info: " + e.getMessage());
            return "";
        }
    }
}
