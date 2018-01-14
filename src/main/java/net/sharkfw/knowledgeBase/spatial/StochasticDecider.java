package net.sharkfw.knowledgeBase.spatial;

/**
 * @author Max Oehme (546545)
 */
public interface StochasticDecider {

    double calculateProbability(SpatialInformation spatialGeometryInformation);
}
