package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Vector;
import net.sharkfw.knowledgeBase.geom.Geometry;

/**
 *
 * @author Jacob Zschunke, thsc
 */
public interface SpatialSTSet extends STSet {
    public SpatialSTSet contextualize(SpatialSTSet context, FragmentationParameter fp) throws SharkKBException;
    
    public SpatialSemanticTag getSpatialSemanticTag(Double[] point) throws SharkKBException;
    
    public double getDistance(SpatialSemanticTag gc1, SpatialSemanticTag gc2);
    public boolean isInRange(SpatialSemanticTag gc1, SpatialSemanticTag gc2, double radius);

    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, Geometry geom) throws SharkKBException;

    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sI, Vector points) throws SharkKBException;

    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sI, Double[] centerPoint, double radius) throws SharkKBException;
    
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, Double[][] points) throws SharkKBException;  
    
    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException;
    
    public SpatialSemanticTag getSpatialSemanticTag(String si) throws SharkKBException;
    
    public SpatialSTSet fragment(Enumeration<String> anchor, double range) throws SharkKBException;
    
    public Enumeration<SpatialSemanticTag> spatialTags() throws SharkKBException;
}
