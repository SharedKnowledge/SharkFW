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
    public SpatialFilter() {

    }

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
                double probability = calculateProbability(spatialInformation);

                L.d("Probability: " + probability + ", Threshold: " + decisionThreshold + ", ForData: " + spatialInformation.toString(), this);
                System.out.println("Probability: " + probability + ", Threshold: " + decisionThreshold + ", ForData: " + spatialInformation.toString());
                return decisionThreshold <= probability;
            } catch (SharkKBException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
    public double calculateProbability(ISpatialInformation spatialGeometryInformation) {
        double d_src = spatialGeometryInformation.getSourceToProfileDistance(),
                d_middle = spatialGeometryInformation.getEntranceExitInProfileDistance(),
                d_dest = spatialGeometryInformation.getDestinationToProfileDistance();
        double k_ent = spatialGeometryInformation.getProfileEntrancePointWeight(),
                k_ex = spatialGeometryInformation.getProfileExitPointWeight();

        double p_source, p_destination;
        double k_pow = 1 / k_ent / k_ex;

        d_middle = d_middle == 0 ? 1 : d_middle;

        if (d_src > 0) {
            p_source = Math.pow(1 / ((d_src / d_middle) + 1), k_pow);
        } else{
            p_source = 1;
        }

        if (d_dest > 0) {
            p_destination = Math.pow(1 / ((d_dest / d_middle) + 1), k_pow);
        } else{
            p_destination = 1;
        }

        double delta_k = k_ex - k_ent;
        delta_k = delta_k == 0 ? 1 : delta_k;

        double part_src = d_src * ((( 1 - (delta_k / (delta_k * (k_ent > k_ex ? -1 : 1))) ) / 2) + (k_ent > k_ex ? -1 : 1) * p_source);
        double part_dest = d_dest * p_destination;

        double p;

        if (part_src + part_dest != 0) {
            p = (part_src + part_dest) / (d_src + d_dest);
        } else {
            p = 1;
        }

        return p;
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
