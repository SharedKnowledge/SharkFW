package net.sharkfw.knowledgeBase.spatial;

/**
 * @author Max Oehme (546545)
 */
public interface ISpatialInformation {

    double getSourceToProfileDistance();

    double getEntranceExitInProfileDistance();

    double getDestinationToProfileDistance();

    int getProfileEntrancePointWeight();

    int getProfileExitPointWeight();
}
