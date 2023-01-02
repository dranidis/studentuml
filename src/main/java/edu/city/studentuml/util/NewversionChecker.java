package edu.city.studentuml.util;

import java.awt.Desktop;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.json.JSONObject;

import edu.city.studentuml.frame.StudentUMLFrame;

public class NewversionChecker {
    private static final Logger logger = Logger.getLogger(NewversionChecker.class.getName());

    private static final String DOWNLOAD_URL = "https://bitbucket.org/studentuml/studentuml-public/downloads/";
    private static final String JSON_URL = "https://api.bitbucket.org/2.0/repositories/studentuml/studentuml-public/downloads";

    private NewversionChecker() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean newVersion() {
        String currentVersion = getCurrentVersion();
        logger.info(() -> "CURRENT VERSION: " + currentVersion);

        String newVersion = getNewestVersion();
        logger.info(() -> "LATEST VERSION: " + newVersion);

        return !newVersion.equals("") && !newVersion.equals(currentVersion);
    }

    /**
     * Gets the current version from the pom.xml
     * https://stackoverflow.com/questions/11500533/access-maven-properties-defined-in-the-pom
     * 
     * @return
     */
    private static String getCurrentVersion() {
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
        StringBuffer content = new StringBuffer();

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

    public static void checkForNewVersion(StudentUMLFrame frame) {
        if (newVersion()) {
            showInfoDialog(frame);
        }
    }

    private static void showInfoDialog(StudentUMLFrame frame) {
        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();

        // create some css from the label's font
        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");

        // html content
        JEditorPane editorPane = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" //
                + "A new version is available for download at <a href=\"" + DOWNLOAD_URL + "\">" + DOWNLOAD_URL + "</a>" //
                + "</body></html>");

        // handle link events
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }

        });
        editorPane.setEditable(false);
        editorPane.setBackground(label.getBackground());

        JOptionPane.showMessageDialog(frame, editorPane);
    }

}
