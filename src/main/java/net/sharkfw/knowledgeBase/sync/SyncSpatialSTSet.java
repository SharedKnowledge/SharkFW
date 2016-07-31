package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
class SyncSpatialSTSet extends SyncSTSet implements SpatialSTSet {
    
    private final SpatialSTSet target;
    
    SyncSpatialSTSet(SpatialSTSet target) {
        super(target);
        
        this.target = target;
    }
    
    SyncSpatialSemanticTag wrapSyncObject(SpatialSemanticTag sst) {
        if(sst != null) {
            return new SyncSpatialSemanticTag(sst);
        }
        return null;
    }

    @Override
    public SpatialSTSet contextualize(SpatialSTSet context, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(context, fp);
    }

    @Override
    public double getDistance(SpatialSemanticTag gc1, SpatialSemanticTag gc2) {
        return this.target.getDistance(gc1, gc2);
    }

    @Override
    public boolean isInRange(SpatialSemanticTag gc1, SpatialSemanticTag gc2, double radius) {
        return this.target.isInRange(gc1, gc2, radius);
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry geom) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSpatialSemanticTag(name, si, geom));
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry[] geoms) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSpatialSemanticTag(name, si, geoms));
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSpatialSemanticTag(sis));
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String si) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSpatialSemanticTag(si));
    }

    @Override
    public Enumeration<SpatialSemanticTag> spatialTags() throws SharkKBException {
        Enumeration<SpatialSemanticTag> spatialTags = this.target.spatialTags();
        
        return new Iterator2Enumeration(this.wrapSTEnum(this, spatialTags).iterator());
    }
    
    @Override
    SpatialSTSet getChanges(Long since) throws SharkKBException {
        SpatialSTSet changes = InMemoSharkKB.createInMemoSpatialSTSet();
        this.putChanges(since, changes);
        return changes;
    }
}
