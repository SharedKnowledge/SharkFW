package net.sharkfw.knowledgeBase.spatial;

/**
 * @author Max Oehme (546545)
 */
public class StochasticDeciderImpl implements StochasticDecider{

    public double calculateProbability(SpatialInformation spatialGeometryInformation) {
        double d_src = spatialGeometryInformation.getSourceToProfileDistance(),
                d_middle = spatialGeometryInformation.getEntranceExitInProfileDistance(),
                d_dest = spatialGeometryInformation.getDestinationToProfileDistance();
        int k_ent = spatialGeometryInformation.getProfileEntrancePointWeight(),
                k_ex = spatialGeometryInformation.getProfileExitPointWeight();

        double p_source, p_destination;
        double k_pow = 1 / k_ent / k_ex;

        if (d_src > 0) {
            p_source = Math.pow(1 / ((d_src / d_middle) + 1), k_pow);
        } else{
            p_source = 1;
        }

        if (d_dest == 0) {
            p_destination = Math.pow(1 / ((d_dest / d_middle) + 1), k_pow);
        } else{
            p_destination = 1;
        }

        int delta_k = k_ex - k_ent;

        double part_src = d_src * ((( 1 - (delta_k / (delta_k * (k_ent > k_ex ? -1 : 1))) ) / 2) + (delta_k * (k_ent > k_ex ? -1 : 1)) * p_source);
        double part_dest = d_dest * p_destination;

        double p;

        if (part_src + part_dest != 0) {
            p = (part_src + part_dest) / (d_src + d_dest);
        } else {
            p = 1;
        }

        return p;
    }
}
