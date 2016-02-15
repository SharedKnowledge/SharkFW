package net.sharkfw.asip;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

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
public interface ASIPInformationSpace {
    ASIPSpace getASIPSpace() throws SharkKBException;
    void setASIPSpace(ASIPSpace space) throws SharkKBException;
    
    Iterator<Information> informations() throws SharkKBException;
}
