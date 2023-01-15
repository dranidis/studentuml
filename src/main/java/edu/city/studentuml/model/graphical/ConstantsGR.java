package edu.city.studentuml.model.graphical;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dimitris
 */
public class ConstantsGR {
    private static ConstantsGR instance = new ConstantsGR();
    private Map<String, Map<String, Object>> parameters;

    public static final float[] DASHES = {8.0f};
    public static final boolean UNDERLINE_OBJECTS = false;
    
    private ConstantsGR() {
        parameters = new HashMap<>();
        parameters.put("SDMessageGR", new HashMap<>());
        parameters.get("SDMessageGR").put("messageDY", 4); // distance of sd message name from arrow
        parameters.get("SDMessageGR").put("barWidth", 10);
        parameters.get("SDMessageGR").put("initBarHeight", 40); // height of bar when call and return created
    }

    public static ConstantsGR getInstance() {
        return instance;
    }

    public int get(String graphicalElement, String parameter) {
        return (Integer) parameters.get(graphicalElement).get(parameter);
    }
    
}
