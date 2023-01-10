package edu.city.studentuml.view.gui;

import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.ActionListener;


public class MyComboBoxEditor implements ComboBoxEditor {
    private JTextField editor;

    public MyComboBoxEditor() {
        editor = new JTextField();
    }

    @Override
    public void setItem(Object item) {
        if (item == null) {
            editor.setText("");
        } else {
            editor.setText(item.toString());
        }
    }

    @Override
    public Object getItem() {
        return editor.getText();
    }

    @Override
    public Component getEditorComponent() {
        return editor;
    }

    @Override
    public void selectAll() {
        editor.selectAll();
    }

    @Override
    public void addActionListener(ActionListener l) {
        editor.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        editor.removeActionListener(l);
    }
}