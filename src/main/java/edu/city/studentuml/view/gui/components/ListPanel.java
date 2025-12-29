package edu.city.studentuml.view.gui.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import edu.city.studentuml.model.repository.CentralRepository;

public abstract class ListPanel<T extends Copyable<T>> extends JPanel implements ActionListener {

    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JPanel buttonsPanel;
    private JList<T> elementsList;

    private Vector<T> elements;

    public Vector<T> getElements() {
        return elements;
    }

    public void setElements(Vector<T> originalElements) {
        // make an exact copy of the elements for editing purposes
        // which may be discarded if the user presses <<Cancel>>

        this.elements = cloneElements(originalElements);
        updateElementsList();
    }

    private CentralRepository repository;

    protected ListPanel(String title, CentralRepository repository) {
        this.repository = repository;

        this.setLayout(new BorderLayout());

        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);

        this.setBorder(titledBorder);
        elementsList = new JList<>();
        elementsList.setFixedCellWidth(400);
        elementsList.setVisibleRowCount(5);

        // Add double-click support to edit elements
        elementsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = elementsList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        elementsList.setSelectedIndex(index);
                        editElement();
                    }
                }
            }
        });

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
        this.add(new JScrollPane(elementsList), BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == addButton) {
            addElement();
        } else if (event.getSource() == editButton) {
            editElement();
        } else if (event.getSource() == deleteButton) {
            deleteElement();
        }
    }

    private void addElement() {
        ElementEditor<T> elementEditor = createElementEditor(null, repository);

        if (!elementEditor.showDialog(this)) { // cancel pressed
            return;
        }

        elements.add(elementEditor.createElement());
        updateElementsList();
    }

    protected abstract ElementEditor<T> createElementEditor(T object, CentralRepository repository);

    private void editElement() {
        if (elements.isEmpty() || elementsList.getSelectedIndex() < 0) {
            return;
        }

        T element = elements.elementAt(elementsList.getSelectedIndex());
        ElementEditor<T> elementEditor = createElementEditor(element, repository);

        if (!elementEditor.showDialog(this)) { // cancel pressed
            return;
        }

        elementEditor.editElement();
        updateElementsList();
    }

    private void deleteElement() {
        if (elements.isEmpty() || elementsList.getSelectedIndex() < 0) {
            return;
        }

        elements.remove(elementsList.getSelectedIndex());
        updateElementsList();
    }

    public void updateElementsList() {
        elementsList.setListData(elements);
    }

    // make an exact copy of the passed attributes list
    private Vector<T> cloneElements(Vector<T> originalElements) {
        Vector<T> copyOfElements = new Vector<>();
        originalElements.forEach(element -> copyOfElements.add(element.copyOf(element)));

        return copyOfElements;
    }
}
