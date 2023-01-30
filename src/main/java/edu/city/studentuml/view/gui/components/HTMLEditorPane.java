package edu.city.studentuml.view.gui.components;

import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;

import edu.city.studentuml.frame.StudentUMLFrame;

public class HTMLEditorPane {

    private HTMLEditorPane() {
        // empty
    }
    
    public static void showHTMLbody(StudentUMLFrame frame, String htlmlBody) {
        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();

        // create some css from the label's font
        StringBuilder style = new StringBuilder("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");

        // html content
        JEditorPane editorPane = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" //
                + htlmlBody //
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

        JOptionPane.showMessageDialog(frame, editorPane, "StudentUML", JOptionPane.PLAIN_MESSAGE);
    }
    
}
