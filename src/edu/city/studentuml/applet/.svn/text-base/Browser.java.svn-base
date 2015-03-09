package edu.city.studentuml.applet;

import javax.swing.text.html.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
A simple Web Browser with minimal functionality.
@author IJ
 */
public class Browser {

    private static String authenticationToken = null;
    private static JDialog theFrame = null;
    public static String selected = null;

    /** Set the page.
    @param jep the pane on which to display the url
    @param url the url to display */
    protected static void setPage(JEditorPane jep, String url) {

        if (url.contains("nodeid")) {
            //System.out.println("Should open"+url);
            selected = url;
            theFrame.dispose();
            return;
        }

        if (!url.contains("?")) {
            url += "?auth_token=" + authenticationToken;
        } else {
            url += "&auth_token=" + authenticationToken;
        }

        if (!url.contains("browser.php")) {
            url = "browser.php" + url;
        }

        try {
            jep.setPage(url);
        } catch (IOException e) {
            System.err.println(e);
            //System.exit(-1);
        }
    }

    /** An inner class which listens for keypresses on the Back button. */
    class backButtonListener implements ActionListener {

        protected JEditorPane jep;
        protected JButton backButton;
        protected Vector history;

        public backButtonListener(JEditorPane jep, JButton backButton, Vector history) {
            this.jep = jep;
            this.backButton = backButton;
            this.history = history;

        }

        /** The action is to show the last url in the history.
        @param e the event*/
        public void actionPerformed(ActionEvent e) {
            try {
                //the current page is the last, remove it
                String curl = (String) history.lastElement();
                history.removeElement(curl);

                curl = (String) history.lastElement();
                System.out.println("Back to " + curl);
                setPage(jep, curl);

                if (history.size() == 1) {
                    backButton.setEnabled(false);
                }
            } catch (Exception ex) {
                System.out.println("Exception " + ex);
            }
        }
    }

    /** An inner class that listens for hyperlinkEvent.*/
    class LinkFollower implements HyperlinkListener {

        protected JEditorPane jep;
        protected JButton backButton;
        protected Vector history;

        public LinkFollower(JEditorPane jep, JButton backButton, Vector history) {
            this.jep = jep;
            this.backButton = backButton;
            this.history = history;

        }

        /** The action is to show the page of the URL the user clicked on.
        @param evt the event. We only care when its type is ACTIVATED. */
        public void hyperlinkUpdate(HyperlinkEvent evt) {
            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    String currentURL = evt.getURL().toString();
                    history.add(currentURL);
                    backButton.setEnabled(true);
                    System.out.println("Going to " + currentURL);
                    setPage(jep, currentURL);
                } catch (Exception e) {
                    System.out.println("ERROR: Trouble fetching url");
                }
            }
        }
    }

    /** The contructor runs the browser. It displays the main frame with the
    fetched initialPage
    @param initialPage the first page to show */
    public Browser(String initialPage, String curAuthToken) {

        this.authenticationToken = curAuthToken;

        /** A vector of String containing the past urls */
        Vector history = new Vector();
        history.add(initialPage);

        // set up the editor pane
        JEditorPane jep = new JEditorPane();

        jep.setEditable(false);
        setPage(jep, initialPage);

        // set up the window
        JScrollPane scrollPane = new JScrollPane(jep);

        JDialog f = new JDialog((Frame) null, true);

        theFrame = f;
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //Exit the program when user closes window.
        f.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                theFrame.dispose();
                //System.exit(0);
            }
        });

        //Label where we show the url

        JButton backButton = new JButton("Back");
        backButton.setActionCommand("back");
        backButton.setToolTipText("Go to previous page");
        backButton.setEnabled(false);
        backButton.addActionListener(new backButtonListener(jep, backButton, history));

        JButton exitButton = new JButton("Exit");
        exitButton.setActionCommand("exit");
        exitButton.setToolTipText("Quit this application");
        exitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //
                theFrame.dispose();
            }
        });

        //A toolbar to hold all our buttons
        JToolBar toolBar = new JToolBar();
        toolBar.add(backButton);
        toolBar.add(exitButton);

        jep.addHyperlinkListener(new LinkFollower(jep, backButton, history));

        //Set up the toolbar and scrollbar in the contentpane of the frame
        JPanel contentPane = (JPanel) f.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setPreferredSize(new Dimension(400, 100));
        contentPane.add(toolBar, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        //contentPane.add(label, BorderLayout.SOUTH);

        f.pack();
        f.setSize(640, 480);
        f.setVisible(true);
    }
}
