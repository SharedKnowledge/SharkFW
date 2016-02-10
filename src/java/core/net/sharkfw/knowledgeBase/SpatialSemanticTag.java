package net.sharkfw.knowledgeBase;

import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 *
 * @author thsc
 */
public interface SpatialSemanticTag extends SemanticTag {
    
    public static final String GEOMETRY = "GEOMETRY";
    
    /**
     * Returns geometry of this given spatial tag
     */
    public SharkGeometry getGeometry();
}
