package net.sharkfw.knowledgeBase;

import net.sharkfw.knowledgeBase.geom.Geometry;

/**
 *
 * @author thsc
 */
public interface SpatialSemanticTag extends SemanticTag {
    /**
     * Returns geometry of this given spatial tag
     */
    public Geometry getGeometry();
}
