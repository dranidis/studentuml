package edu.city.studentuml.model.graphical;

public class DiagramType {
    private DiagramType() {
        throw new IllegalStateException("Utility class");
    }

    public static final int UCD = 0;
    public static final int SSD = 1;
    public static final int SD = 2;
    public static final int CCD = 3;
    public static final int DCD = 4;
    public static final int AD = 5;
}
