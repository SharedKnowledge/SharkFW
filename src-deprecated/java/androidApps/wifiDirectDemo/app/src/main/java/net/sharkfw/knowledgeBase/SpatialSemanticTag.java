package net.sharkfw.knowledgeBase;

import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 *
 * @author thsc
 */
public interface SpatialSemanticTag extends SemanticTag {
    /**
     * Returns geometry of this given spatial tag
     */
    public SharkGeometry getGeometry();
}
