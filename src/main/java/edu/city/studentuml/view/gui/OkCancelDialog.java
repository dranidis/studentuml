package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Base class for editor dialogs. Provides common functionality for showing
 * modal dialogs with OK/Cancel buttons.
 * 
 * @author StudentUML Team
 */
public abstract class OkCancelDialog extends JPanel implements ActionListener {

    protected JDialog dialog;
    protected boolean ok; // stores whether the user has pressed ok
    protected JButton okButton;
    protected JButton cancelButton;

    protected Component parent;
    protected String title;
    private boolean initialized = false;

    public OkCancelDialog(Component parent, String title) {
        this.parent = parent;
        this.title = title;
        setLayout(new BorderLayout());
    }

    /**
     * Initialize the panel lazily - called before first use. This allows subclass
     * constructors to set fields before makeCenterPanel() is called.
     */
    protected void initializeIfNeeded() {
        if (!initialized) {
            JPanel bottomPanel = new JPanel();
            FlowLayout bottomLayout = new FlowLayout();
            bottomLayout.setHgap(30);
            bottomPanel.setLayout(bottomLayout);
            okButton = new JButton("OK");
            okButton.addActionListener(this);
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            bottomPanel.add(okButton);
            bottomPanel.add(cancelButton);

            JPanel centerPanel = makeCenterPanel();

            add(centerPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
            initialized = true;
        }
    }

    protected abstract JPanel makeCenterPanel();

    /**
     * Show the editor dialog. The dialog returns false unless ok is changed by the
     * actionPerformed event.
     * 
     * @param parent The parent component for positioning the dialog
     * @param title  The title of the dialog window
     * @return true if OK was pressed, false if Cancel was pressed
     */
    public boolean showDialog() {
        initializeIfNeeded(); // Ensure panel is created before showing dialog
        ok = false;

        // find the owner frame
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        dialog = new JDialog(owner, title, true);
        dialog.getContentPane().add(this);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        // Add ESC key handler to act as Cancel
        addEscapeKeyHandler(dialog);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        return ok;
    }

    /**
     * Adds an ESC key handler to the dialog to act as Cancel.
     * 
     * @param dialog The dialog to add the ESC handler to
     */
    private void addEscapeKeyHandler(JDialog dialog) {
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        dialog.getRootPane().registerKeyboardAction(
                e -> {
                    ok = false;
                    dialog.setVisible(false);
                },
                escapeKey,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Handle button click events for OK and Cancel buttons. Subclasses can override
     * this method to handle additional events, but should call
     * super.actionPerformed(event) for OK/Cancel handling.
     * 
     * @param event The action event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == okButton) {
            // OK button pressed
            actionOK(event);
        } else if (source == cancelButton) {
            // Cancel button pressed
            dialog.setVisible(false);
        } else {
            // other events can be handled by subclasses
            actionRest(event);
        }
    }

    protected void actionOK(ActionEvent event) {
        ok = true;
        dialog.setVisible(false);
    }

    /**
     * Handle other events that are not OK or Cancel. Subclasses can override this
     * method to provide additional event handling.
     * 
     * @param event The action event
     */
    protected void actionRest(ActionEvent event) {
        // Default implementation does nothing
    }

}
