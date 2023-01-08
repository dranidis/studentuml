package edu.city.studentuml.applet;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * 
 * @author Ervin Ramollari
 */
public class LoginPanel extends JPanel implements ActionListener {

    private JLabel usernameLabel;
    private JTextField usernameField;
    private JPanel usernamePanel;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JPanel passwordPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel bottomPanel;
    private JDialog loginDialog;
    private boolean ok; // stores whether the user has pressed ok

    public LoginPanel() {
        usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(15);

        passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(15);
        passwordField.addActionListener(this);

        usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout());
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        add(usernamePanel, c);

        c.gridx = 0;
        c.gridy = 1;
        add(passwordPanel, c);

        c.gridx = 0;
        c.gridy = 2;
        add(bottomPanel, c);
    }

    public boolean showDialog(Component parent, String title) {
        ok = false;

        // find the owner frame
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        loginDialog = new JDialog(owner, true);
        loginDialog.getContentPane().add(this);
        loginDialog.setTitle(title);
        loginDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        loginDialog.pack();
        loginDialog.setResizable(false);
        loginDialog.setLocationRelativeTo(owner);
        loginDialog.setVisible(true);

        return ok;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() == okButton) || (e.getSource() == passwordField)) {
            ok = true;
            loginDialog.setVisible(false);
        } else if (e.getSource() == cancelButton) {
            loginDialog.setVisible(false);
        }
    }
}
