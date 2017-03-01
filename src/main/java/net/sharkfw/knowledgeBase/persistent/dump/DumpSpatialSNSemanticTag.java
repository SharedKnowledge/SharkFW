package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.SpatialSNSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpSpatialSNSemanticTag extends DumpSNSemanticTag implements SpatialSNSemanticTag {

    private final SpatialSNSemanticTag snSemanticTag;

    public DumpSpatialSNSemanticTag(DumpSharkKB dumpSharkKB, SpatialSNSemanticTag tag) {
        super(dumpSharkKB, tag);
        snSemanticTag = tag;
    }

    @Override
    public SharkGeometry getGeometry() {
        return new DumpSharkGeometry(this.kb, this.snSemanticTag.getGeometry());
    }
}
