package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpSpatialSTSet extends DumpSTSet implements SpatialSTSet {
    private final SpatialSTSet spatialSTSet;

    public DumpSpatialSTSet(DumpSharkKB kb, SpatialSTSet set) {
        super(kb, set);
        spatialSTSet = set;
    }

    @Override
    public SpatialSTSet contextualize(SpatialSTSet context, FragmentationParameter fp) throws SharkKBException {
        SpatialSTSet contextualize = this.spatialSTSet.contextualize(context, fp);
        this.kb.persist();
        return new DumpSpatialSTSet(this.kb, contextualize);
    }

    @Override
    public double getDistance(SpatialSemanticTag gc1, SpatialSemanticTag gc2) {
        return this.spatialSTSet.getDistance(gc1, gc2);
    }

    @Override
    public boolean isInRange(SpatialSemanticTag gc1, SpatialSemanticTag gc2, double radius) {
        return this.spatialSTSet.isInRange(gc1, gc2, radius);
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry geometry) throws SharkKBException {
        SpatialSemanticTag spatialSemanticTag = this.spatialSTSet.createSpatialSemanticTag(name, sis, geometry);
        this.kb.persist();
        return new DumpSpatialSemanticTag(this.kb, spatialSemanticTag);
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry[] geometries) throws SharkKBException {
        SpatialSemanticTag spatialSemanticTag = this.spatialSTSet.createSpatialSemanticTag(name, sis, geometries);
        this.kb.persist();
        return new DumpSpatialSemanticTag(this.kb, spatialSemanticTag);

    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException {
        SpatialSemanticTag spatialSemanticTag = this.spatialSTSet.getSpatialSemanticTag(sis);
        this.kb.persist();
        return new DumpSpatialSemanticTag(this.kb, spatialSemanticTag);
    }

    @Override
    public SpatialSemanticTag getSpatialSemanticTag(String si) throws SharkKBException {
        SpatialSemanticTag spatialSemanticTag = this.spatialSTSet.getSpatialSemanticTag(si);
        this.kb.persist();
        return new DumpSpatialSemanticTag(this.kb, spatialSemanticTag);
    }

    @Override
    public Enumeration<SpatialSemanticTag> spatialTags() throws SharkKBException {
        Enumeration<SpatialSemanticTag> spatialSemanticTagEnumeration = this.spatialSTSet.spatialTags();
        List<SpatialSemanticTag> list = new ArrayList<>();
        while (spatialSemanticTagEnumeration.hasMoreElements()){
            list.add(new DumpSpatialSemanticTag(this.kb, spatialSemanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);    }

}
