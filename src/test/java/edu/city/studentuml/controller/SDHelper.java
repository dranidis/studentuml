package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;

class SDHelper {
    private SDModel model;

    SDHelper(SDModel model) {
        this.model = model;
    }

    /**
     * Adds an SD object to the model at the specified X coordinate.
     */
    RoleClassifierGR addSDObject(String name, int x) {
        DesignClass dc = new DesignClass(name);
        SDObject sdObject = new SDObject(name, dc);
        SDObjectGR sdObjectGR = new SDObjectGR(sdObject, x);
        model.addGraphicalElement(sdObjectGR);
        return sdObjectGR;
    }

    /**
     * Adds a call message between two SD objects at the specified Y coordinate.
     */
    CallMessageGR addCallMessage(RoleClassifierGR from, RoleClassifierGR to, String messageName, int y) {
        GenericOperation operation = new GenericOperation(messageName);
        CallMessage callMessage = new CallMessage(from.getRoleClassifier(), to.getRoleClassifier(), operation);
        CallMessageGR callMessageGR = new CallMessageGR(from, to, callMessage, y);
        model.addGraphicalElement(callMessageGR);
        return callMessageGR;
    }

    /**
     * Adds a return message between two SD objects at the specified Y coordinate.
     */
    ReturnMessageGR addReturnMessage(RoleClassifierGR from, RoleClassifierGR to, String returnValue, int y) {
        ReturnMessage returnMessage = new ReturnMessage(from.getRoleClassifier(), to.getRoleClassifier(), returnValue);
        ReturnMessageGR returnMessageGR = new ReturnMessageGR(from, to, returnMessage, y);
        model.addGraphicalElement(returnMessageGR);
        return returnMessageGR;
    }
}
