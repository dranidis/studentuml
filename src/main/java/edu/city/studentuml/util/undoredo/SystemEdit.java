package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.SystemInstance;

/**
 *
 * @author draganbisercic
 */
public class SystemEdit {

    private SystemInstance system;
    private String systemName;

    public SystemEdit(SystemInstance system, String systemName) {
        this.system = system;
        this.systemName = systemName;
    }

    public SystemInstance getSystemInstance() {
        return system;
    }

    public void setObject(SystemInstance system) {
        this.system = system;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public SystemEdit clone() {
        SystemInstance sys = new SystemInstance(system.getName(), system.getSystem());
        SystemEdit copy = new SystemEdit(sys, systemName);
        return copy;
    }
}
