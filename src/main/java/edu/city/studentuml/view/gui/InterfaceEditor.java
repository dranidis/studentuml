package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.Editor;
import edu.city.studentuml.view.gui.components.MethodsPanel;

/**
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class InterfaceEditor extends ClassifierEditor implements Editor<Interface> {

    private static final String TITLE = "Interface Editor";
    private MethodsPanel methodsPanel;

    /**
     * Constructor for Editor<Interface> pattern. Creates an InterfaceEditor with an
     * empty Interface and initializes the UI.
     */
    public InterfaceEditor(CentralRepository cr) {
        super(new Interface(""), cr, AUTO_COMPLETE);

        repository = cr;

        setLayout(new BorderLayout());

        methodsPanel = new MethodsPanel("Interface Methods", cr);

        add(namePanel, BorderLayout.NORTH);
        add(methodsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * @deprecated Use {@link #InterfaceEditor(CentralRepository)} and
     *             {@link #editDialog(Interface, Component)} instead.
     */
    @Deprecated
    public InterfaceEditor(Interface coreInterface, CentralRepository cr) {
        super(coreInterface, cr, AUTO_COMPLETE);

        repository = cr;

        setLayout(new BorderLayout());

        methodsPanel = new MethodsPanel("Interface Methods", cr);

        add(namePanel, BorderLayout.NORTH);
        add(methodsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        if (coreInterface != null) {
            methodsPanel.setElements(coreInterface.getMethods());
        }
    }

    @Override
    public Interface editDialog(Interface interfaceObj, Component parent) {
        setClassifierName(interfaceObj.getName());
        methodsPanel.setElements(interfaceObj.getMethods());

        if (!showDialog(parent, TITLE)) {
            return null;
        }
        return getInterface();
    }

    public Interface getInterface() {
        Interface newInterface = new Interface(getInterfaceName());

        // add the methods to the new interface
        getMethods().forEach(newInterface::addMethod);
        return newInterface;
    }

    public String getInterfaceName() {
        return getClassName();
    }

    public Vector<Method> getMethods() {
        return methodsPanel.getElements();
    }

    @Override
    protected List<Classifier> getTypes() {
        List<Classifier> l = new ArrayList<>();
        l.addAll(repository.getInterfaces());
        return l;
    }

    @Override
    protected void handleRest(ActionEvent event) {
        // empty
    }

    @Override
    protected void handleOK(ActionEvent event) {
        boolean matchingDesignClass = repository.getClasses().stream().anyMatch(i -> {
            String name = i.getName();
            return !name.equals("") && name.equals(getInterfaceName());
        });

        if (matchingDesignClass) {
            JOptionPane.showMessageDialog(this, "There is a class with the same name", "Warning",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        classifierDialog.setVisible(false);

        setReturnToTrue();

    }
}
