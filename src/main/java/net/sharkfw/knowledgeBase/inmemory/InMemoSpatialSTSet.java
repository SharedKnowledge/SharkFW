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

    // TODO
    public static SpatialSTSet fragmentSpatialSTSet(Enumeration<String> anchor, SpatialSTSet gsts, double range) throws SharkKBException {
        return gsts;
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

    /**
     * TODO Implement with multiple SharkGeometries
     * Currently just uses the first Geometry.
     * @param name
     * @param si
     * @param geoms
     * @return
     * @throws SharkKBException 
     */
    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry[] geoms) throws SharkKBException {
        return createSpatialSemanticTag(name, si, geoms[0]);
    }
}
