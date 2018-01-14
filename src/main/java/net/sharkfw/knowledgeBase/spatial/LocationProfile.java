package net.sharkfw.knowledgeBase.spatial;

import net.sharkfw.knowledgeBase.geom.PointGeometry;

/**
 * @author Max Oehme (546545)
 */
public interface LocationProfile {
    SpatialInformation calculateSpatialInformationFromProfile(PointGeometry pointGeometry);
}
