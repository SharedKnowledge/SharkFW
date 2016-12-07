package net.sharkfw.asip;

import java.util.Iterator;

import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

/**
 * Information can be annotated with semantic tags. It plural, e.g.
 * information can have multiple topics to which they apply.
 * 
 * They can have multiple approvers who agree those topics describe
 * or classify information correctly. 
 * 
 * An information space has only two parts:
 * <ul>
 * <li>A context space describing the <i>geometry</i> of the space</li>
 * <li>A list of information</li>
 * </ul>
 * @author thsc
 */
public interface ASIPInformationSpace extends SystemPropertyHolder {
    
    public final static String ASIPSPACE = "ASIPSPACE";
    public final static String INFORMATION = "INFORMATION";
    
    ASIPSpace getASIPSpace() throws SharkKBException;

    int numberOfInformations();
    
    Iterator<ASIPInformation> informations() throws SharkKBException;
}
