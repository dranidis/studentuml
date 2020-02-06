/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    private ConstantsGR() {
        parameters = new HashMap<>();
        parameters.put("SDMessageGR", new HashMap<>());
        parameters.get("SDMessageGR").put("messageDY", 3);
        parameters.get("SDMessageGR").put("barWidth", 10);
        parameters.get("SDMessageGR").put("initBarHeight", 20);
    }

    public static ConstantsGR getInstance() {
        return instance;
    }

    public int get(String graphicalElement, String parameter) {
        return (Integer) parameters.get(graphicalElement).get(parameter);
    }
    
}
