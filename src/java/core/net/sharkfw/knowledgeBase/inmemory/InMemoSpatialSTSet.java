package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import java.util.Vector;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.Geometry;
import net.sharkfw.system.Util;

/**
 *
 * @author Jacob Zschunke
 */
public class InMemoSpatialSTSet extends InMemoSTSet implements SpatialSTSet, STSet {

    InMemoSpatialSTSet() {
        super(new InMemoGenericTagStorage<SpatialSemanticTag>());
    }

    public InMemoSpatialSTSet(InMemoGenericTagStorage storage) {
        super(storage);
    }

    // TODO
    @Override
    public SpatialSemanticTag getSpatialSemanticTag(Double[] point) throws SharkKBException {
//        Enumeration tagEnum = this.tags();
//        while (tagEnum != null && tagEnum.hasMoreElements()) {
//            SpatialSemanticTag gst = (SpatialSemanticTag) tagEnum.nextElement();
//            Enumeration pointEnum = gst.getPoints().elements();
//            while (pointEnum != null && pointEnum.hasMoreElements()) {
//                Double[] curPoint = (Double[]) pointEnum.nextElement();
//                if (curPoint[SpatialSemanticTag.LATITUDE] == point[SpatialSemanticTag.LATITUDE]
//                        && curPoint[SpatialSemanticTag.LONGITUDE] == point[SpatialSemanticTag.LONGITUDE]) {
//                    return gst;
//                }
//            }
//        }
        return null;
    }

    /**
     * TODO
     *
     * @param gst
     */
    public void addGeoSemanticTag(SpatialSemanticTag gst) throws SharkKBException {
        super.add(gst);
    }

    // TODO
    public static SpatialSTSet fragmentSpatialSTSet(Enumeration<String> anchor, SpatialSTSet gsts, double range) throws SharkKBException {
        return gsts;
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, Vector points) throws SharkKBException {
        SpatialSemanticTag gst = this.getSpatialSemanticTag(sis);
        if (gst != null) {
            return gst;
        }

        gst = new InMemoSpatialSemanticTag(name, sis, points);

        this.addGeoSemanticTag(gst);

        return gst;
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, Geometry geom) throws SharkKBException {
        SpatialSemanticTag st = this.getSpatialSemanticTag(sis);
        if (st != null) {
            return st;
        }

        st = new InMemoSpatialSemanticTag(name, sis, geom);
        this.addGeoSemanticTag(st);

        return st;
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, Double[] centerPoint, double radius) throws SharkKBException {
        SpatialSemanticTag gst = this.getSpatialSemanticTag(sis);
        if (gst != null) {
            return gst;
        }

        gst = new InMemoSpatialSemanticTag(name, sis, centerPoint, radius);

        this.addGeoSemanticTag(gst);

        return gst;
    }

    /**
     * TODO
     *
     * @param context
     * @param fp
     * @return
     * @throws SharkKBException
     */
    @Override
    public SpatialSTSet contextualize(SpatialSTSet context, FragmentationParameter fp) throws SharkKBException {
        return this;
    }

    private SpatialSemanticTag castGST(SemanticTag st) throws SharkKBException {
        if (st == null) {
            return null;
        }

        if (st instanceof SpatialSemanticTag) {
            return (SpatialSemanticTag) st;
        }

        throw new SharkKBException("cannot use non geo semantic tag in geo semantic tag set");
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        return this.createSpatialSemanticTag(name, sis, (Vector) null);
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.createSemanticTag(name, new String[]{si});
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, Double[][] points) throws SharkKBException {
        SpatialSemanticTag gst = this.getSpatialSemanticTag(sis);
        if (gst != null) {
            return gst;
        }

        return new InMemoSpatialSemanticTag(name, sis, points);
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException {
        return this.castGST(super.getSemanticTag(sis));
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String si) throws SharkKBException {
        return this.castGST(super.getSemanticTag(si));
    }

    @Override
    public Enumeration<SpatialSemanticTag> spatialTags() throws SharkKBException {
        Enumeration tags = super.tags();
        return tags;
    }

    @Override
    public double getDistance(SpatialSemanticTag gc1, SpatialSemanticTag gc2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isInRange(SpatialSemanticTag gc1, SpatialSemanticTag gc2, double radius) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSTSet fragment(Enumeration<String> anchor, double range) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
