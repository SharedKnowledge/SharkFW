package net.sharkfw.knowledgeBase.spatial;

/**
 * @author Max Oehme (546545)
 */
public class SpatialProbabilityImpl implements ISpatialProbability {

//    public double calculateProbability(ISpatialInformation spatialGeometryInformation) {
//        double d_src = spatialGeometryInformation.getSourceToProfileDistance(),
//                d_middle = spatialGeometryInformation.getEntranceExitInProfileDistance(),
//                d_dest = spatialGeometryInformation.getDestinationToProfileDistance();
//        double k_ent = spatialGeometryInformation.getProfileEntrancePointWeight(),
//                k_ex = spatialGeometryInformation.getProfileExitPointWeight();
//
//        double p_source, p_destination;
//        double k_pow = 1 / k_ent / k_ex;
//
//        d_middle = d_middle == 0 ? 1 : d_middle;
//
//        if (d_src > 0) {
//            p_source = Math.pow(1 / ((d_src / d_middle) + 1), k_pow);
//        } else{
//            p_source = 1;
//        }
//
//        if (d_dest > 0) {
//            p_destination = Math.pow(1 / ((d_dest / d_middle) + 1), k_pow);
//        } else{
//            p_destination = 1;
//        }
//
//        double delta_k = k_ex - k_ent;
//        delta_k = delta_k == 0 ? 1 : delta_k;
//
//        double part_src = d_src * ((( 1 - (delta_k / (delta_k * (k_ent > k_ex ? -1 : 1))) ) / 2) + (k_ent > k_ex ? -1 : 1) * p_source);
//        double part_dest = d_dest * p_destination;
//
//        double p;
//
//        if (part_src + part_dest != 0) {
//            p = (part_src + part_dest) / (d_src + d_dest);
//        } else {
//            p = 1;
//        }
//
//        return p;
//    }
}
