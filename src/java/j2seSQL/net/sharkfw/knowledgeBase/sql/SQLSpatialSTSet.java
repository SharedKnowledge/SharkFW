package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
public class SQLSpatialSTSet extends SQLSTSet implements SpatialSTSet {

    SQLSpatialSTSet(SQLSharkKB kb) {
        super(kb, SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE);
    }

    @Override
    public SpatialSTSet contextualize(SpatialSTSet context, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry geom) throws SharkKBException {
        SQLSemanticTagStorage sqlTag = this.createSQLSemanticTag(kb, name, null, 0, 0, false, SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE, si, null);
        return (SpatialSemanticTag) SQLSharkKB.wrapSQLTagStorage(kb, sqlTag, SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE);
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException {
        SQLSemanticTagStorage sqlTag = this.getSQLSemanticTagStorage(sis);
        return (SpatialSemanticTag) SQLSharkKB.wrapSQLTagStorage(kb, sqlTag, SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE);
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String si) throws SharkKBException {
        return this.getSpatialSemanticTag(new String[] {si});
    }

    @Override
    public Enumeration<SpatialSemanticTag> spatialTags() throws SharkKBException {
        Iterator tags = this.tags(SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE);
        
        return new Iterator2Enumeration(tags);
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry[] geoms) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
