package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 * Created by j4rvis on 2/27/17.
 */
public class DumpSpatialSemanticTag extends DumpSemanticTag implements SpatialSemanticTag {

    private final SpatialSemanticTag spatialSemanticTag;

    public DumpSpatialSemanticTag(DumpSharkKB dumpSharkKB, SpatialSemanticTag tag) {
        super(dumpSharkKB, tag);
        spatialSemanticTag = tag;
    }

    @Override
    public SharkGeometry getGeometry() {
        return new DumpSharkGeometry(this.kb, this.spatialSemanticTag.getGeometry());
    }
}
