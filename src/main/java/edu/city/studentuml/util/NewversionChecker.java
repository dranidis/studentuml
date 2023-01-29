package edu.city.studentuml.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.JSONObject;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.view.gui.components.HTMLEditorPane;

public class NewversionChecker {
    private static final Logger logger = Logger.getLogger(NewversionChecker.class.getName());

    private static final String DOWNLOAD_URL = "https://bitbucket.org/studentuml/studentuml-public/downloads/";
    private static final String JSON_URL = "https://api.bitbucket.org/2.0/repositories/studentuml/studentuml-public/downloads";
    private static final String CURRENT_VERSION = getCurrentVersion();

    private NewversionChecker() {
        throw new IllegalStateException("Utility class");
    }

    public static void checkForNewVersion(StudentUMLFrame frame) {
        if (thereIsANewVersion() && !currentVersionIsSnapShot()) {
            showNewVersionDialog(frame);
        } else {
            showOKVersionDialog(frame);
        }
    }


    public static boolean thereIsANewVersion() {
        logger.info(() -> "CURRENT VERSION: " + CURRENT_VERSION);

        String newVersion = getNewestVersion();
        logger.info(() -> "LATEST VERSION: " + newVersion);

        return !newVersion.equals("") && !newVersion.equals(CURRENT_VERSION);
    }

    private static boolean currentVersionIsSnapShot() {
        return CURRENT_VERSION.contains("SNAPSHOT");
    }

    /**
     * Gets the current version from the pom.xml
     * https://stackoverflow.com/questions/11500533/access-maven-properties-defined-in-the-pom
     * 
     * @return
     */
    public static String getCurrentVersion() {
        InputStream inputStream = NewversionChecker.class.getClassLoader().getResourceAsStream("my.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty("version");
    }

    /**
     * Returns the newest version assuming a naming pattern:
     * xxxxxxx-version-xxxxxxxxxx
     * 
     * @return
     */
    private static String getNewestVersion() {
        String fromURL = getFromURL();

        if (fromURL.equals("")) {
            return "";
        }

        JSONObject obj = new JSONObject(fromURL);
        String latestJar = obj.getJSONArray("values").getJSONObject(0).get("name").toString();

        String[] latest = latestJar.split("-");

        if (latest.length < 2) {
            logger.info(() -> "File at " + JSON_URL + " does not follow the pattern xxxx-version-xxx");
            return "";
        }

        return latestJar.split("-")[1];
    }

    private static String getFromURL() {
        URL url;
        HttpURLConnection con;
        int status;
        StringBuilder content = new StringBuilder();

        try {
            url = new URL(JSON_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            status = con.getResponseCode();

            logger.info(() -> "GET " + JSON_URL + " STATUS: " + status);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

        } catch (IOException e) {
            logger.info("Failed to connect to " + JSON_URL);
            return "";
        }
        logger.finest(content::toString);

        return content.toString();
    }

    private static void showNewVersionDialog(StudentUMLFrame frame) {
        HTMLEditorPane.showHTMLbody(frame, "A new version is available for download at <a href=\"" + DOWNLOAD_URL + "\">" + DOWNLOAD_URL + "</a>" //
                + "</body></html>");
    }

    private static void showOKVersionDialog(StudentUMLFrame frame) {
        HTMLEditorPane.showHTMLbody(frame, "You are already using the latest version.");
    }


}
