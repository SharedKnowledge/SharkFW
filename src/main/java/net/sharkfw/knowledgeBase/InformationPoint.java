package net.sharkfw.knowledgeBase;

/**
 *
 * @author thsc
 */
public interface InformationPoint extends ContextPoint {
    
    public final static String INFORMATIONS = "INFORMATION";
    public final static String INFOCOORDINATES = "INFOCOORDINATES";
    

    InformationCoordinates getInformationCoordinates();
    
}
