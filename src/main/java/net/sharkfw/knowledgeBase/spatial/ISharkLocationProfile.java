package net.sharkfw.knowledgeBase.spatial;

import net.sharkfw.knowledgeBase.geom.SharkPoint;

/**
 * @author Max Oehme (546545)
 */
public interface ISharkLocationProfile {
    ISpatialInformation createSpatialInformationFromProfile(SharkPoint sharkPoint);
}
