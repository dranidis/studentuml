package edu.city.studentuml.util.version;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test class for GitHubVersionProvider. Tests JSON parsing logic without
 * network calls.
 */
public class GitHubVersionProviderTest {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/dranidis/studentuml/releases/latest";

    @Test
    public void testParseVersionFromJSON_withVPrefix() {
        GitHubVersionProvider provider = new GitHubVersionProvider(GITHUB_API_URL);
        String json = "{\"tag_name\":\"v1.3.1\",\"name\":\"Release 1.3.1\"}";

        String version = provider.parseVersionFromJSON(json);

        assertEquals("1.3.1", version);
    }

    @Test
    public void testParseVersionFromJSON_withVPrefixUppercase() {
        GitHubVersionProvider provider = new GitHubVersionProvider(GITHUB_API_URL);
        String json = "{\"tag_name\":\"V1.3.1\",\"name\":\"Release 1.3.1\"}";

        String version = provider.parseVersionFromJSON(json);

        assertEquals("1.3.1", version);
    }

    @Test
    public void testParseVersionFromJSON_noPrefix() {
        GitHubVersionProvider provider = new GitHubVersionProvider(GITHUB_API_URL);
        String json = "{\"tag_name\":\"1.3.1\",\"name\":\"Release 1.3.1\"}";

        String version = provider.parseVersionFromJSON(json);

        assertEquals("1.3.1", version);
    }

    @Test
    public void testParseVersionFromJSON_invalidJSON() {
        GitHubVersionProvider provider = new GitHubVersionProvider(GITHUB_API_URL);
        String json = "not valid json";

        String version = provider.parseVersionFromJSON(json);

        assertEquals("", version);
    }

    @Test
    public void testParseVersionFromJSON_missingTagName() {
        GitHubVersionProvider provider = new GitHubVersionProvider(GITHUB_API_URL);
        String json = "{\"name\":\"Release 1.3.1\"}";

        String version = provider.parseVersionFromJSON(json);

        assertEquals("", version);
    }

    @Test
    public void testParseVersionFromJSON_emptyJSON() {
        GitHubVersionProvider provider = new GitHubVersionProvider(GITHUB_API_URL);
        String json = "{}";

        String version = provider.parseVersionFromJSON(json);

        assertEquals("", version);
    }

    @Test
    public void testGetLatestVersion_emptyFetch() {
        // Create a provider that simulates failed HTTP fetch
        GitHubVersionProvider provider = new GitHubVersionProvider(GITHUB_API_URL) {
            @Override
            String fetchFromURL(String url) {
                return ""; // Simulate network failure
            }
        };

        String version = provider.getLatestVersion();

        assertEquals("", version);
    }
}
