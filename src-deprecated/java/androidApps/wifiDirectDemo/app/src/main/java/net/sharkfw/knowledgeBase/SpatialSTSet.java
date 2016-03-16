package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Vector;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 *
 * @author Jacob Zschunke, thsc
 */
public interface SpatialSTSet extends STSet {
    public SpatialSTSet contextualize(SpatialSTSet context, FragmentationParameter fp) throws SharkKBException;
    
    public double getDistance(SpatialSemanticTag gc1, SpatialSemanticTag gc2);
    public boolean isInRange(SpatialSemanticTag gc1, SpatialSemanticTag gc2, double radius);

    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry geom) throws SharkKBException;

    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException;
    
    public SpatialSemanticTag getSpatialSemanticTag(String si) throws SharkKBException;
    
    public Enumeration<SpatialSemanticTag> spatialTags() throws SharkKBException;
}
