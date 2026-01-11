# Deep Domain Model Analysis: Association Editors

**Status**: UPDATED - showArrow and labelDirection now fully supported for association classes.

This document provides a comprehensive analysis of the Association and AssociationClass domain model hierarchy, crucial for understanding the refactoring challenges in Category 4 editors.

## Association Class Hierarchy

```
Association (standalone class)
├── Properties: name, direction, showArrow, labelDirection, roleA, roleB
├── Direction constants: BIDIRECTIONAL(0), AB(1), BA(2), BIDIRECTIONAL_FIX(3)
└── Label direction: FROM_A_TO_B(3), FROM_B_TO_A(4)

AbstractAssociationClass (abstract base)
├── Composition: contains Association + AbstractClass
├── Properties delegated to Association: direction, roles
├── Properties delegated to AbstractClass: name, attributes
└── Subclasses:
    ├── ConceptualAssociationClass
    │   └── AssociationClass: ConceptualClass (with attributes only)
    └── DesignAssociationClass
        └── AssociationClass: DesignClass (with attributes + methods)
```

## Key Architectural Insights

### 1. Composition over Inheritance

**Critical Discovery**: `AbstractAssociationClass` does NOT extend `Association`!

```java
public abstract class AbstractAssociationClass {
    protected Association association;        // HAS-A relationship
    protected AbstractClass associationClass;  // HAS-A relationship

    // Properties are delegated to inner objects
    public String getName() {
        return associationClass.getName();  // Name from inner class!
    }

    public int getDirection() {
        return association.getDirection();  // Direction from inner association
    }
}
```

**Implications**:

-   `AbstractAssociationClass` is a **composite wrapper** around two objects
-   It's NOT a subtype of `Association` - type hierarchies are separate
-   Cannot use `Association` methods directly on `AbstractAssociationClass`

### 2. API Differences

| Property   | Association                       | AbstractAssociationClass                                                |
| ---------- | --------------------------------- | ----------------------------------------------------------------------- |
| Name       | `association.getName()`           | `assocClass.getName()` → `associationClass.getName()`                   |
| Direction  | `association.getDirection()`      | `assocClass.getDirection()` → `association.getDirection()`              |
| Show Arrow | `association.getShowArrow()`      | `assocClass.getShowArrow()` → `association.getShowArrow()` ✅           |
| Label Dir. | `association.getLabelDirection()` | `assocClass.getLabelDirection()` → `association.getLabelDirection()` ✅ |
| Roles      | `association.getRoleA()`          | `assocClass.getRoleA()` → `association.getRoleA()`                      |
| Attributes | N/A                               | `assocClass.getAttributes()` → `associationClass.getAttributes()`       |
| Methods    | N/A                               | `designAssocClass.getMethods()` (DCD only)                              |

**Critical API Points**:

-   **Name Storage Location**:
    -   Regular Association: name stored in `Association` object
    -   Association Class: name stored in inner `AbstractClass` (ConceptualClass/DesignClass)
-   **showArrow and labelDirection**: ✅ NOW FULLY SUPPORTED via delegation to inner Association
-   **Access Pattern**: Must call `getAssociation()` to access inner Association object (for advanced scenarios)

### 3. Controller Expectations

#### CCDAssociationEditor / AssociationEditor (Regular Associations)

```java
// Controller usage pattern
String name = editor.getAssociationName();          // Direct from nameField
boolean showArrow = editor.getShowArrow();          // From checkbox
int labelDirection = editor.getLabelDirection();    // From toggle button
String roleAName = editor.getRoleAName();
String roleAMult = editor.getRoleAMultiplicity();
int direction = editor.getDirection();              // DCD only
```

#### ConceptualAssociationClassEditor / DesignAssociationClassEditor

```java
// Controller usage pattern
String name = editor.getAssociationClassName();     // NOT getAssociationName()!
boolean showArrow = editor.isShowArrow();           // ✅ Now supported!
int labelDirection = editor.getLabelDirection();    // ✅ Now supported!
String roleAName = editor.getRoleAName();
String roleAMult = editor.getRoleAMultiplicity();
Vector<Attribute> attrs = editor.getAttributes();
int direction = editor.getDirection();              // DCD only
Vector<Method> methods = editor.getMethods();       // DCD only
```

**Key Difference**: Controllers expect `getAssociationClassName()` for association classes, NOT `getAssociationName()`

### 4. Initialization Patterns

#### Regular Association Editors

```java
public void initialize() {
    Association a = associationGR.getAssociation();
    initializeCommonFields(a);  // Works directly
}
```

#### Association Class Editors

```java
public void initialize() {
    ConceptualAssociationClass assocClass = (ConceptualAssociationClass) associationClassGR.getAssociationClass();

    // WRONG: Cannot pass AbstractAssociationClass to initializeCommonFields(Association)
    // initializeCommonFields(assocClass);  // Compilation error!

    // CORRECT: Extract inner Association object
    initializeCommonFields(assocClass.getAssociation());

    // IMPORTANT: Name must be initialized separately from inner class
    nameField.setText(assocClass.getName());  // From associationClass, not association!
}
```

### 5. XML Persistence Structure

#### Association XML

```xml
<association name="employs" direction="0" showArrow="true" labelDirection="3">
    <roleA name="employer" multiplicity="1"/>
    <roleB name="employee" multiplicity="*"/>
</association>
```

#### AbstractAssociationClass XML

```xml
<associationClass name="Employment" direction="0">
    <roleA name="employer" multiplicity="1"/>
    <roleB name="employee" multiplicity="*"/>
    <attributes>
        <attribute name="startDate" type="Date"/>
    </attributes>
    <!-- For DesignAssociationClass only: -->
    <methods>
        <method name="calculateDuration"/>
    </methods>
</associationClass>
```

**Note**: No `showArrow` or `labelDirection` in AssociationClass XML

### 6. Type Safety Issues

```java
// AssociationEditorBase.java
protected void initializeCommonFields(Association association) {
    nameField.setText(association.getName());  // OK for Association
    // ...
}

// Problem: AbstractAssociationClass is NOT an Association!
AbstractAssociationClass assocClass = ...;
initializeCommonFields(assocClass);  // ❌ Compilation error: incompatible types
initializeCommonFields(assocClass.getAssociation());  // ✅ OK, but name is wrong!
```

**The Name Problem**:

-   `association.getName()` returns empty string (association name field is usually empty)
-   `assocClass.getName()` returns the actual association class name
-   These are **different values** stored in **different objects**!

## Refactoring Implications

### Problem 1: Base Class Type Mismatch

`AssociationEditorBase` is designed for `Association` objects, but association class editors work with `AbstractAssociationClass` objects.

**Solutions**:

#### Option A: Overload initializeCommonFields()

```java
// In AssociationEditorBase.java
protected void initializeCommonFields(Association association) {
    nameField.setText(association.getName());
    // ... initialize roles, show arrow, label direction
}

protected void initializeCommonFields(AbstractAssociationClass assocClass) {
    nameField.setText(assocClass.getName());  // From inner class
    // Initialize roles from inner association
    Association innerAssoc = assocClass.getAssociation();
    roleAPanel.setRole(innerAssoc.getRoleA());
    roleBPanel.setRole(innerAssoc.getRoleB());
    // Note: No showArrow/labelDirection for association classes
}
```

#### Option B: Extract in Subclass (Current Approach - Has Bug!)

```java
// In ConceptualAssociationClassEditor.java
public void initialize() {
    ConceptualAssociationClass a = (ConceptualAssociationClass) associationClassGR.getAssociationClass();

    // BUG: This initializes nameField with association.getName() (usually empty!)
    initializeCommonFields(a.getAssociation());

    // FIX: Must override nameField separately
    nameField.setText(a.getName());  // Get name from associationClass
}
```

#### Option C: Separate Base Class (Most Robust)

```java
// New class: AssociationClassEditorBase
public abstract class AssociationClassEditorBase extends JPanel {
    // Similar to AssociationEditorBase but works with AbstractAssociationClass
    protected void initializeCommonFields(AbstractAssociationClass assocClass) {
        nameField.setText(assocClass.getName());  // Correct source
        // No showArrow or labelDirection components
        // Initialize roles from inner association
    }
}
```

### Problem 2: API Compatibility

Controllers expect different method names:

| Editor Type         | Method Name                 | Source                |
| ------------------- | --------------------------- | --------------------- |
| Regular Association | `getAssociationName()`      | `nameField.getText()` |
| Association Class   | `getAssociationClassName()` | `nameField.getText()` |

**Solution**: Provide both methods, or use appropriate naming in subclasses

### Problem 3: Optional Components

Not all editors use all components:

| Component              | CCD Assoc | DCD Assoc | CCD AssocClass | DCD AssocClass |
| ---------------------- | --------- | --------- | -------------- | -------------- |
| Name field             | ✅        | ✅        | ✅             | ✅             |
| Role A/B panels        | ✅        | ✅        | ✅             | ✅             |
| Show arrow checkbox    | ✅        | ✅        | ❌             | ❌             |
| Label direction toggle | ✅        | ✅        | ❌             | ❌             |
| Direction combo box    | ❌        | ✅        | ❌             | ✅             |
| Attributes panel       | ❌        | ❌        | ✅             | ✅             |
| Methods panel          | ❌        | ❌        | ❌             | ✅             |

**Solution**: Keep optional components creation in base class (already implemented via `createLabelDirectionComponents()`)

## Recommended Refactoring Approach

### Phase 1: Fix Current Implementation ✅

1. Add overloaded `initializeCommonFields(AbstractAssociationClass)` to `AssociationEditorBase`
2. Provide `getAssociationClassName()` method in association class editors
3. Fix initialization to properly handle name from inner class

### Phase 2: Refactor Association Editors ✅

1. CCDAssociationEditor - Simple, has show arrow + label direction
2. AssociationEditor (DCD) - Add direction combo box

### Phase 3: Refactor Association Class Editors

1. ConceptualAssociationClassEditor - No show arrow/label direction, has attributes
2. DesignAssociationClassEditor - Has direction combo, attributes, and methods

### Phase 4: Consider Future Improvements

1. Extract `AssociationClassEditorBase` if pattern becomes too complex
2. Create shared component for direction combo box
3. Document the composition pattern clearly for future developers

## Implementation Update (December 2025)

### Changes Made

The restriction on `showArrow` and `labelDirection` for association classes has been **removed**. Association classes now fully support these properties.

#### Domain Model Changes

1. **AbstractAssociationClass.java** - Added delegating methods:

    ```java
    public boolean getShowArrow() {
        return association.getShowArrow();
    }

    public void setShowArrow(boolean show) {
        association.setShowArrow(show);
    }

    public int getLabelDirection() {
        return association.getLabelDirection();
    }

    public void setLabelDirection(int direction) {
        association.setLabelDirection(direction);
    }

    public void changeLabelDirection() {
        association.changeLabelDirection();
    }
    ```

2. **XML Serialization** - Updated `streamFromXML()` and `streamToXML()` to persist these properties:

    ```java
    // Read with backward compatibility (empty string check for old files)
    String showArrowAttr = node.getAttribute("showArrow");
    if (showArrowAttr != null && !showArrowAttr.isEmpty()) {
        setShowArrow(Boolean.parseBoolean(showArrowAttr));
    }

    // Write always includes these properties
    node.setAttribute("showArrow", String.valueOf(getShowArrow()));
    node.setAttribute("labelDirection", Integer.toString(getLabelDirection()));
    ```

#### Editor Changes

1. **AssociationEditorBase.java** - Added overloaded `initializeCommonFields()`:

    ```java
    protected void initializeCommonFields(AbstractAssociationClass assocClass) {
        // Name comes from inner AbstractClass
        nameField.setText(assocClass.getName());

        // showArrow and labelDirection now supported
        if (showArrowCheckBox != null) {
            showArrowCheckBox.setSelected(assocClass.getShowArrow());
            readLabelDirection = assocClass.getLabelDirection();
            // Update toggle button text
        }

        // Roles from inner Association
        roleAPanel.setRole(assocClass.getRoleA());
        roleBPanel.setRole(assocClass.getRoleB());
    }
    ```

2. **ConceptualAssociationClassEditor.java** - Now creates label direction components:

    ```java
    createLabelDirectionComponents(Association.FROM_A_TO_B);
    ```

3. **DesignAssociationClassEditor.java** - Now creates label direction components:
    ```java
    createLabelDirectionComponents(Association.FROM_A_TO_B);
    ```

#### Controller Changes

Controllers now set these properties for association classes:

1. **CCDSelectionController.java**:

    ```java
    associationClass.setName(editor.getAssociationClassName());
    associationClass.setShowArrow(editor.isShowArrow());
    associationClass.setLabelDirection(editor.getLabelDirection());
    ```

2. **DCDSelectionController.java**:
    ```java
    associationClass.setName(editor.getAssociationClassName());
    associationClass.setDirection(editor.getDirection());
    associationClass.setShowArrow(editor.isShowArrow());
    associationClass.setLabelDirection(editor.getLabelDirection());
    ```

### Benefits

1. **Consistency**: All associations (regular and association classes) now support the same label features
2. **Unified Code**: No special cases for association classes - the same base class methods work for all
3. **Backward Compatibility**: Old XML files without these attributes still load correctly
4. **Future-Proof**: Association classes now have feature parity with regular associations

### Important Note: Arrow Display Requirements

✅ **The label arrow displays when the association class has a non-empty name and showArrow is enabled.**

**Implementation Detail:**

The `AssociationGR` (which handles the line rendering) reads the name from the inner `Association` object, not from the `AbstractClass`. To ensure the arrow displays correctly, `AbstractAssociationClass.setName()` now sets the name on **both** objects:

```java
public void setName(String n) {
    associationClass.setName(n);  // Set on the class (for display in class box)
    association.setName(n);        // Set on association (for label arrow rendering)
}
```

**To see the arrow:**

1. Give the association class a name (e.g., "Enrollment", "Manages")
2. Check the "Show Label Arrow" checkbox
3. The arrow will appear next to the name label on the association line

This ensures both the class box and the association line display the same name, and the arrow can be rendered correctly.

## Summary

The key challenge in refactoring association editors is understanding that `AbstractAssociationClass` is **not** an `Association` - it's a composite wrapper containing both an `Association` and an `AbstractClass`. This architectural decision impacts:

-   Type compatibility (cannot pass AbstractAssociationClass where Association is expected)
-   Name storage location (different objects hold the name)
-   API expectations (controllers use different method names)

**UPDATE**: Previously, association classes didn't support show arrow/label direction. This restriction has been lifted - all associations now have full feature parity.

The refactoring must handle these differences while still maximizing code reuse for the common elements (roles, dialog management, OK/Cancel buttons).
