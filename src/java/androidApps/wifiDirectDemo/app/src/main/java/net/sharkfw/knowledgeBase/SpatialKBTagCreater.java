package net.sharkfw.knowledgeBase;

import java.util.Vector;

/**
 *
 * @author Jacob Zschunke
 */
public interface SpatialKBTagCreater {
    
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, Double[] point, double range) throws SharkKBException;

    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, Vector points) throws SharkKBException;

    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, Double[][] points) throws SharkKBException;
    
    
}
