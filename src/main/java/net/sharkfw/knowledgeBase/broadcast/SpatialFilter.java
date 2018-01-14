package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.PointGeometry;
import net.sharkfw.knowledgeBase.spatial.LocationProfile;
import net.sharkfw.knowledgeBase.spatial.SpatialInformation;
import net.sharkfw.knowledgeBase.spatial.StochasticDecider;
import net.sharkfw.knowledgeBase.spatial.StochasticDeciderImpl;
import net.sharkfw.system.L;

import java.util.Enumeration;

public class SpatialFilter implements SemanticFilter {

    private Dimension dimension;
    private StochasticDecider stochasticDecider = new StochasticDeciderImpl();
    private LocationProfile locationProfile = null;
    private double decisionThreshold;

    public SpatialFilter(Dimension dimension, LocationProfile locationProfile, double decisionThreshold) {
        if (dimension == Dimension.SPATIAL) {
            L.d("Creating Spatial Filter!");
            this.dimension = dimension;
            this.locationProfile = locationProfile;
            this.decisionThreshold = decisionThreshold;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean filter(ASIPInMessage message, SharkKB newKnowledge, ASIPInterest activeEntryProfile) {
        boolean isInteresting = false;

        Enumeration<SpatialSemanticTag> dimensionTags = null;
        try {
            if (dimension == Dimension.SPATIAL) {
                dimensionTags = newKnowledge.getSpatialSTSet().spatialTags();
            }
            else {
                dimensionTags = newKnowledge.getSpatialSTSet().spatialTags();
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
            return false;
        }


        SpatialSemanticTag currentElement;
        while (dimensionTags.hasMoreElements()) {
            currentElement = dimensionTags.nextElement();

            if (checkSpatialSemanticTag(currentElement)) {
                isInteresting = true;
            }
        }
        return isInteresting;
    }

    private boolean checkSpatialSemanticTag(SpatialSemanticTag spatialSemanticTag) {
        if (PointGeometry.isPoint(spatialSemanticTag.getGeometry().getWKT())) {
            try {
                PointGeometry destPoint = new PointGeometry(spatialSemanticTag.getGeometry());

                SpatialInformation spatialInformation = locationProfile.calculateSpatialInformationFromProfile(destPoint);
                double probability = stochasticDecider.calculateProbability(spatialInformation);

                return decisionThreshold >= probability;
            } catch (SharkKBException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public Dimension getDimension() {
        return dimension;
    }

    public double getDecisionThreshold() {
        return decisionThreshold;
    }

    public void setDecisionThreshold(double decisionThreshold) {
        this.decisionThreshold = decisionThreshold;
    }

    public LocationProfile getLocationProfile() {
        return locationProfile;
    }
}
