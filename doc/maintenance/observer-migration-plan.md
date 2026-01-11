# Migration Plan: Observable/Observer → PropertyChangeSupport/PropertyChangeListener

## 1. Identify All Affected Classes

-   `UMLProject` (extends Observable, implements Observer)
-   `DiagramModel` (extends Observable)
-   `CentralRepository` (extends Observable)
-   `ObjectFactory` (extends Observable)
-   `SystemWideObjectNamePool` (extends Observable)
-   `RepositoryTreeView` (implements Observer)
-   `ApplicationGUI` (implements Observer)
-   Any other class using addObserver, deleteObserver, notifyObservers, setChanged, or update

---

### Observer/Observable Relationships (Pre-Migration)

```
UMLProject (Observable, Observer)
├── observes: CentralRepository (Observable)
├── observes: SystemWideObjectNamePool (Observable)
├── observes: DiagramModel (Observable)
├── observed by: ApplicationGUI (Observer)
├── observed by: RepositoryTreeView (Observer)
├── observed by: ApplicationFrame (Observer)

CentralRepository (Observable)
├── observed by: UMLProject (Observer)
├── observed by: RepositoryTreeView (Observer)

SystemWideObjectNamePool (Observable)
├── observed by: UMLProject (Observer)

DiagramModel (Observable)
├── observed by: UMLProject (Observer)
├── observed by: ApplicationGUI (Observer)

ObjectFactory (Observable)
├── observed by: UMLProject (Observer)

RepositoryTreeView (Observer)
├── observes: UMLProject, CentralRepository

ApplicationGUI (Observer)
├── observes: UMLProject, DiagramModel

ApplicationFrame (Observer)
├── observes: UMLProject
```

After migration, all relationships use PropertyChangeSupport/PropertyChangeListener.

---

## CentralRepository Migration Details

-   Removed `extends Observable` from `CentralRepository`.
-   Did **not** add a `PropertyChangeSupport` field; the class no longer manages observer registration or notifications directly.
-   Notification and listener management for domain concept changes is now handled by the `NotifierVector` collections, which wrap each domain list and provide their own notification mechanism.
-   All observer/observable logic has been removed from the class itself, and the migration to a modern event model is achieved via `NotifierVector`.
-   `CentralRepository` now acts as a pure domain repository, with notification responsibilities delegated to its contained collections.

---

## 2. Refactor Observable Classes

For each class that extends Observable:

-   Remove `extends Observable`
-   Add a `PropertyChangeSupport` field:
    ```java
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    ```
-   Replace `addObserver(Observer o)` with `addPropertyChangeListener(PropertyChangeListener l)`
-   Replace `deleteObserver(Observer o)` with `removePropertyChangeListener(PropertyChangeListener l)`
-   Replace `notifyObservers(Object arg)` with `pcs.firePropertyChange("property", oldValue, newValue)` (choose appropriate property names and values)
-   Remove all `setChanged()` calls (not needed)
-   Update all internal calls to use the new property change support

---

## 3. Refactor Observer Classes

For each class that implements Observer:

-   Remove `implements Observer`
-   Implement `PropertyChangeListener` instead
-   Change `update(Observable o, Object arg)` to `propertyChange(PropertyChangeEvent evt)`
-   Update registration: replace `addObserver(this)` with `addPropertyChangeListener(this)`

---

## 4. Update All Usages

-   Search for all usages of `addObserver`, `deleteObserver`, `notifyObservers`, `setChanged`, and `update` and update to the new API.
-   Update all test code and factories that use these APIs.

---

## 5. Migrate ApplicationFrame.java

-   Refactor `ApplicationFrame.java`:
    -   Remove `implements Observer` (if present)
    -   Implement `PropertyChangeListener` instead
    -   Change `update(Observable o, Object arg)` to `propertyChange(PropertyChangeEvent evt)`
    -   Update registration: replace `addObserver(this)` with `addPropertyChangeListener(this)`
    -   Update all usages and logic accordingly

---

## 6. Test and Validate

-   Run all tests and verify that all observer/observable interactions still work as expected.
-   Manually test UI and undo/redo features that depend on observer notifications.

---

## 6. Documentation

-   Update project documentation to reflect the new observer pattern implementation.
