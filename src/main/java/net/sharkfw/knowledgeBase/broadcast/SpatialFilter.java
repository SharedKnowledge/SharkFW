package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkPoint;
import net.sharkfw.knowledgeBase.spatial.ISharkLocationProfile;
import net.sharkfw.knowledgeBase.spatial.ISpatialInformation;
import net.sharkfw.knowledgeBase.spatial.ISpatialProbability;
import net.sharkfw.knowledgeBase.spatial.SpatialProbabilityImpl;
import net.sharkfw.system.L;

import java.util.Enumeration;

public class SpatialFilter implements SemanticFilter {

    private Dimension dimension;
    private ISpatialProbability spatialProbability;
    private ISharkLocationProfile sharkLocationProfile;
    private double decisionThreshold;

    public SpatialFilter(Dimension dimension, ISharkLocationProfile sharkLocationProfile, double decisionThreshold) {
        this(dimension, new SpatialProbabilityImpl(), sharkLocationProfile, decisionThreshold);
    }

    public SpatialFilter(Dimension dimension, ISpatialProbability spatialProbability, ISharkLocationProfile sharkLocationProfile, double decisionThreshold) {
        if (dimension == Dimension.SPATIAL && sharkLocationProfile != null) {
            L.d("Creating Spatial Filter!");
            this.dimension = dimension;
            this.sharkLocationProfile = sharkLocationProfile;
            this.decisionThreshold = decisionThreshold;
            this.spatialProbability = spatialProbability;
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
        if (SharkPoint.isPoint(spatialSemanticTag.getGeometry().getWKT())) {
            try {
                SharkPoint destPoint = new SharkPoint(spatialSemanticTag.getGeometry());

                ISpatialInformation spatialInformation = sharkLocationProfile.createSpatialInformationFromProfile(destPoint);
                double probability = spatialProbability.calculateProbability(spatialInformation);

                L.d("Probability: " + probability + ", Threshold: " + decisionThreshold + ", ForData: " + spatialInformation.toString(), this);

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

    public ISharkLocationProfile getSharkLocationProfile() {
        return sharkLocationProfile;
    }
}
