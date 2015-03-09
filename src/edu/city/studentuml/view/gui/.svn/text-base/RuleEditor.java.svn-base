package edu.city.studentuml.view.gui;

import edu.city.studentuml.view.gui.DocumentSizeFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class RuleEditor extends JPanel {

    private String currentRuleFile;
    JTextPane textPane;
    JLabel statusLabel;
    AbstractDocument doc;
    static final int MAX_CHARACTERS = 99000000;
    String newline = "\n";
    HashMap<Object, Action> actions;
    //undo helpers
    protected UndoAction undoAction;
    protected RedoAction redoAction;
    protected UndoManager undo = new UndoManager();

    public RuleEditor(String currentRuleFile) {
        super(new java.awt.BorderLayout());
        this.currentRuleFile = currentRuleFile;
        //Create the text pane and configure it.
        textPane = new JTextPane() {

            public void setSize(Dimension d) {
                if (d.width < getParent().getSize().width) {
                    d.width = getParent().getSize().width;
                }

                super.setSize(d);
            }

            public boolean getScrollableTracksViewportWidth() {
                return false;
            }
        };
        //textPane.setEditable( false);//FIXME: STAVI GO NA KRAJ TRUE
        textPane.setCaretPosition(0);
        textPane.setMargin(new Insets(5, 5, 5, 5));
        StyledDocument styledDoc = textPane.getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            doc = (AbstractDocument) styledDoc;
            doc.setDocumentFilter(new DocumentSizeFilter(MAX_CHARACTERS));
        } else {
            System.err.println("Text pane's document isn't an AbstractDocument!");
            System.exit(-1);
        }
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        //Create the text area for the status log and configure it.


        //Create a split pane for the change log and the text area.


        //Create the status area.
        JPanel statusPane = new JPanel(new GridLayout(1, 1));
        statusLabel = new JLabel("Status");
        statusPane.add(statusLabel);

        //Add the components.
        add(scrollPane, BorderLayout.CENTER);
        add(statusPane, BorderLayout.PAGE_END);

        //Set up the menu bar.
        createActionTable(textPane);


        //Add some key bindings.
        addBindings();

        //Put the initial text into the text pane.
        initDocument();
        textPane.setCaretPosition(0);

        //Start watching for undoable edits and caret changes.
        doc.addUndoableEditListener(new MyUndoableEditListener());
        //textPane.addCaretListener(caretListenerLabel);
        doc.addDocumentListener(new MyDocumentListener());
    }

    //This listens for and reports caret movements.
//    protected class CaretListenerLabel extends JLabel
//                                       implements CaretListener {
//        public CaretListenerLabel(String label) {
//            super(label);
//        }
//
//        //Might not be invoked from the event dispatch thread.
//        public void caretUpdate(CaretEvent e) {
//            displaySelectionInfo(e.getDot(), e.getMark());
//        }
//
//        //This method can be invoked from any thread.  It
//        //invokes the setText and modelToView methods, which
//        //must run on the event dispatch thread. We use
//        //invokeLater to schedule the code for execution
//        //on the event dispatch thread.
//        protected void displaySelectionInfo(final int dot,
//                                            final int mark) {
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    if (dot == mark) {  // no selection
//
//                            setText("caret: text position: " + dot
//                                    + newline);
//
//                    } else if (dot < mark) {
//                        setText("selection from: " + dot
//                                + " to " + mark + newline);
//                    } else {
//                        setText("selection from: " + mark
//                                + " to " + dot + newline);
//                    }
//                }
//            });
//        }
//    }
    //This one listens for edits that can be undone.
    protected class MyUndoableEditListener implements UndoableEditListener {

        public void undoableEditHappened(UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }
    }

    //And this one listens for any changes to the document.
    protected class MyDocumentListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            displayEditInfo(e);
        }

        public void removeUpdate(DocumentEvent e) {
            displayEditInfo(e);
        }

        public void changedUpdate(DocumentEvent e) {
            displayEditInfo(e);
        }

        private void displayEditInfo(DocumentEvent e) {
            statusLabel.setText("Rules changed. Press Ctrl+S to save, Ctrl+Z to undo, Ctrl+Y to redo.");
//            Document document = (Document)e.getDocument();
//            int changeLength = e.getLength();
//            changeLog.append(e.getType().toString() + ": " +
//                changeLength + " character" +
//                ((changeLength == 1) ? ". " : "s. ") +
//                " Text length = " + document.getLength() +
//                "." + newline);
        }
    }

    //Add a couple of  key bindings.
    protected void addBindings() {
        InputMap inputMap = textPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        //Ctrl-b to go backward one character
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.backwardAction);

        //Ctrl-f to go forward one character
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.forwardAction);

        //Ctrl-p to go up one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.upAction);

        //Ctrl-n to go down one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.downAction);

        //Ctrl-z undo
        undoAction = new UndoAction();
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
        inputMap.put(key, undoAction);

        //Ctrl-y redo
        redoAction = new RedoAction();
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
        inputMap.put(key, redoAction);

        //Ctrl-x cut
        key = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.cutAction);

        //Ctrl-c copy
        key = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.copyAction);

        //Ctrl-v paste
        key = KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.pasteAction);

        //Ctrl-s save
        Action save = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                BufferedWriter bw;
                try {
                    bw = new BufferedWriter(new FileWriter(new File("rules/" + currentRuleFile)));
                    bw.write(textPane.getText());
                    bw.flush();
                    bw.close();
                    statusLabel.setText("Rules saved.");
                } catch (FileNotFoundException ex1) {
                    // TODO Auto-generated catch block
                    ex1.printStackTrace();
                } catch (IOException ex2) {
                    // TODO Auto-generated catch block
                    ex2.printStackTrace();
                }
            }
        };
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
        inputMap.put(key, save);

    }

    protected void initDocument() {
        /* BufferedReader objBrIn;
        try {
        objBrIn = new BufferedReader(new FileReader(new File("rules/" + currentRuleFile)));
        String strTemp;
        StringBuffer strOut = new StringBuffer();

        while ((strTemp = objBrIn.readLine()) != null) {
        strOut.append(strTemp).append(newline);
        }
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, "Courier New");
        StyleConstants.setFontSize(attrs, 14);

        textPane.setCharacterAttributes( attrs, true);
        textPane.setText(strOut.toString());

        objBrIn.close();
        }
        catch( FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        catch( IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } */
    }

    //The following two methods allow us to find an
    //action provided by the editor kit by its name.
    private void createActionTable(JTextComponent textComponent) {
        actions = new HashMap<Object, Action>();
        Action[] actionsArray = textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
    }

    private Action getActionByName(String name) {
        return actions.get(name);
    }

    class UndoAction extends AbstractAction {

        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    class RedoAction extends AbstractAction {

        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }
}
