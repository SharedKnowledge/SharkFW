package net.sharkfw.knowledgeBase;

/**
 * All entities that can be configured using properties implement this interface.
 * It covers the basic operations required for using properties.
 * 
 * @author mfi
 */
public interface SystemPropertyHolder extends PropertyHolder {

    public void setSystemProperty(String name, String value);
    public String getSystemProperty(String name);
}
