package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import java.util.Vector;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 *
 * @author Jacob Zschunke
 */
public class InMemoSpatialSTSet extends InMemoSTSet implements SpatialSTSet, STSet {

    InMemoSpatialSTSet() {
        super(new InMemoGenericTagStorage<SpatialSemanticTag>());
    }

    @SuppressWarnings("rawtypes")
    public InMemoSpatialSTSet(InMemoGenericTagStorage storage) {
        super(storage);
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
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry geom) throws SharkKBException {
        SpatialSemanticTag st = this.getSpatialSemanticTag(sis);
        if (st != null) {
            return st;
        }

        st = new InMemoSpatialSemanticTag(name, sis, geom);
        this.addGeoSemanticTag(st);

        return st;
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

    @SuppressWarnings("rawtypes")
    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        return super.createSemanticTag(name, sis);
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.createSemanticTag(name, new String[]{si});
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException {
        return this.castGST(super.getSemanticTag(sis));
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String si) throws SharkKBException {
        return this.castGST(super.getSemanticTag(si));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
}
