package net.sharkfw.knowledgeBase.inmemory;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkURI;
import net.sharkfw.knowledgeBase.SpatialSNSemanticTag;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
@SuppressWarnings("unchecked")
public class InMemoSpatialSemanticTag extends InMemo_SN_TX_SemanticTag implements SpatialSNSemanticTag {
    private SharkGeometry geom;
    public static final String STD_SST_NAME = "SpatialSemanticTag";
    
    public InMemoSpatialSemanticTag(String name, String si[], SharkGeometry geom) {
        super(name, si);
        
        this.geom = geom;
    }

    protected InMemoSpatialSemanticTag() {
        this(STD_SST_NAME, new String[] { SharkURI.geoST() } );
    }
    
    public InMemoSpatialSemanticTag(SharkGeometry geom) {
        this(STD_SST_NAME, 
                new String[] { InMemoSharkGeometry.createSpatialSI(geom) }, 
                geom);
    }
    
    @SuppressWarnings("rawtypes")
    public InMemoSpatialSemanticTag(SystemPropertyHolder ph, InMemoGenericTagStorage storage) {
        super(ph, storage);
    }
    
    protected InMemoSpatialSemanticTag(String name, String[] sis) {
        super(name, sis);
    }
    
    /**
     * 
     * @param name
     * @param si
     * @param point
     * @param radius
     * @deprecated sowas von alt - bitte raus damit - asap!!
     */
    public InMemoSpatialSemanticTag(String name, String[] si, Double[] point, double radius) {
        this(name, si);
        
        // TODO
//
//        Double[] tmp = null;
//        if (point.length >= 3) {
//            tmp = new Double[]{point[LATITUDE], point[LONGITUDE], point[ALTITUDE]};
//        } else if (point.length >= 2) {
//            tmp = new Double[]{point[LATITUDE], point[LONGITUDE], 0.0};
//        } else {
//            return; // FIXME: exception would be better!
//        }
//        point = tmp;
//        boolean isDoublett = false;
//        String pointsSI = SpatialAlgebra.pointToSI(point, radius);
//        for(int j = 0; j < si.length; j++) {
//            if(si[j].equalsIgnoreCase(pointsSI)) {
//                isDoublett = true;
//                break;
//            }
//        }
//        if(!isDoublett || pointsSI == null) {
//            try {
//                this.addSI(pointsSI);
//            }
//            catch(SharkKBException s) {
//                // TODO
//            }
//        }
//
//        this.setSystem("WGS84", 6378.137);
//        this.setRadius(radius);
    }
    /**
     * 
     * @param name
     * @param si
     * @param points
     * @throws SharkKBException 
     * @deprecated remove that
     */

    public InMemoSpatialSemanticTag(String name, String[] si, Double[][] points) throws SharkKBException {
        this(name, si);
        
        // TODO
        
//        for (int i = 0; i < points.length; i++) {
//            if (points[i].length < 3) {
//                Double[] tmp = new Double[]{points[i][0], points[i][1], 0.0, 0.0};
//                points[i] = tmp;
//            }
//        }
//        
//        boolean isDoublett = false;
//        String pointsSI = SpatialAlgebra.pointsToSI(points);
//        for(int j = 0; j < si.length; j++) {
//            if(si[j].equalsIgnoreCase(pointsSI)) {
//                isDoublett = true;
//                break;
//            }
//        }
//        if(!isDoublett || pointsSI == null) {
//            this.addSI(pointsSI);
//        }
//        
//
//        super.setProperty(AREA, TRUE);
//        this.setSystem("WGS84", 6378.137);
//        this.setRadius(0);
    }
    
    /**
     * 
     * @param name
     * @param si
     * @param points
     * @throws SharkKBException 
     * @deprecated remove that stuff asap
     */

    @SuppressWarnings("rawtypes")
    public InMemoSpatialSemanticTag(String name, String[] si, Vector/*<Double[]>*/ points) throws SharkKBException {
        this(name, si);
        
        // TODO
    
//        if(points != null) {
//            Double[][] dPoints = new Double[points.size()][3];
//            Enumeration pointEnum = points.elements();
//            int i = 0;
//            while(pointEnum != null && pointEnum.hasMoreElements()) {
//                dPoints[i] = (Double[]) pointEnum.nextElement();            
//                Double[] tmp = new Double[]{dPoints[i][0], dPoints[i][1], 0.0, 0.0};
//                dPoints[i] = tmp;
//                i++;
//            }
//
//            boolean isDoublett = false;
//            String pointsSI = SpatialAlgebra.pointsToSI(dPoints);
//            for(int j = 0; j < si.length; j++) {
//                if(si[j].equalsIgnoreCase(pointsSI)) {
//                    isDoublett = true;
//                    break;
//                }
//            }
//            if(!isDoublett && pointsSI != null) {
//                this.addSI(pointsSI);
//            }
//        }
//        
//        super.setProperty(AREA, TRUE);
//        this.setSystem("WGS84", 6378.137);
//        this.setRadius(0);
    }
    
    public static final String GEOM_WKT = "Geom_WKT";
    
    @Override
    public void persist() {
        super.persist();
        
        if(this.geom != null) {
            try {
                this.setProperty(GEOM_WKT, this.geom.getEWKT());
            } catch (SharkKBException ex) {
                L.e("cannot access properties", this);
            }
        }
    }
    
    //TODO
    @Override
    public void refreshStatus() {
        super.refreshStatus();
        
        String wkt = null;
        try {
            wkt = this.getProperty(GEOM_WKT);
        } catch (SharkKBException ex) {
                L.e("cannot access properties", this);
        }
        
        if(wkt != null) {
            try {
                this.geom = InMemoSharkGeometry.createGeomByWKT(wkt);
            } catch (SharkKBException ex) {
                L.d("couldn't refresh geometry: " + ex.getMessage());
            }
        }
    }
        
    @Override
    public SharkGeometry getGeometry() {
        if(this.geom == null) {
            this.refreshStatus();
        }
        
        return this.geom;
    }
}
