# Manual Test Cases for StudentUML

## Copy/Paste Functionality

### Test Case 1: Copy/Paste Classes with Association

**Test Date:** December 22, 2025  
**Feature:** Copy/Paste (Ctrl+C / Ctrl+V)  
**Diagram Type:** Design Class Diagram (DCD)

**Test Setup:**

1. Create a new Design Class Diagram
2. Add Class A
3. Add Class B
4. Create an Association from A to B (A -> B)

**Test Steps:**

1. Select both Class A and Class B (but not the association)
2. Press Ctrl+C to copy
3. Press Ctrl+V to paste

**Expected Result:**

-   Classes A and B should be pasted at an offset (20 pixels right and down)
-   The pasted classes should be independent copies
-   The association should be created between the pasted classes (pasted A -> pasted B)

**Actual Result:**

-   ✅ Classes are pasted correctly with proper offset
-   ❌ **BUG:** Association A->B is created again between the original (copied) elements, not between the pasted elements
-   The pasted classes have no association between them

**Impact:** HIGH - Copy/paste of related elements does not preserve relationships

**Notes:**

-   This appears to be a limitation in the current implementation
-   The `clone()` methods for graphical elements do not handle relationship/association cloning
-   Associations are separate graphical elements that reference the connected classes
-   When associations are not selected for copy, they cannot be recreated for pasted elements

**Possible Solutions:**

1. Automatically detect and copy related associations when copying connected classes
2. Update association endpoints to point to cloned elements when pasting
3. Add intelligent relationship detection during paste operation

---
