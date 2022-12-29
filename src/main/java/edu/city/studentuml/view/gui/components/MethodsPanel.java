package edu.city.studentuml.view.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.MethodEditor;

public class MethodsPanel extends JPanel implements ActionListener {

    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JPanel buttonsPanel;
    private JList<Method> methodsList;

    private Vector<Method> methods;

    public Vector<Method> getMethods() {
        return methods;
    }

    public void setMethods(Vector<Method> originalMethods) {
        // make an exact copy of the methods for editing purposes
        // which may be discarded if the user presses <<Cancel>>

        this.methods = cloneMethods(originalMethods);
        updateMethodsList();
    }

    private CentralRepository repository;

    public MethodsPanel(String title, CentralRepository repository) {
        this.repository = repository;

        this.setLayout(new BorderLayout());

        TitledBorder title3 = BorderFactory.createTitledBorder(title);

        this.setBorder(title3);
        methodsList = new JList<>();
        methodsList.setFixedCellWidth(400);
        methodsList.setVisibleRowCount(5);
        addButton = new JButton("Add...");
        addButton.addActionListener(this);
        editButton = new JButton("Edit...");
        editButton.addActionListener(this);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        this.add(new JScrollPane(methodsList), BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == addButton) {
            addMethod();
        } else if (event.getSource() == editButton) {
            editMethod();
        } else if (event.getSource() == deleteButton) {
            deleteMethod();
        }        
    }

    private void addMethod() {
        MethodEditor methodEditor = new MethodEditor(null, repository);

        if (!methodEditor.showDialog(this, "Method Editor")) {    // cancel pressed
            return;
        }

        methods.add(methodEditor.createMethod());
        updateMethodsList();
    }

    private void editMethod() {
        if (methods.isEmpty() || methodsList.getSelectedIndex() < 0) {
            return;
        }

        Method method = methods.elementAt(methodsList.getSelectedIndex());
        MethodEditor methodEditor = new MethodEditor(method, repository);

        if (!methodEditor.showDialog(this, "Method Editor")) {    // cancel pressed
            return;
        }

        methodEditor.editMethod();
        updateMethodsList();
    }

    private void deleteMethod() {
        if (methods.isEmpty() || methodsList.getSelectedIndex() < 0) {
            return;
        }

        methods.remove(methodsList.getSelectedIndex());
        updateMethodsList();
    }    

    private void updateMethodsList() {
        methodsList.setListData(methods);
    }
    
    // make an exact copy of the passed methods list
    private Vector<Method> cloneMethods(Vector<Method> originalMethods) {
        Vector<Method> copyOfMethods = new Vector<>();
        originalMethods.forEach(originalMethod -> copyOfMethods.add(originalMethod.clone()));

        return copyOfMethods;
    }
}
