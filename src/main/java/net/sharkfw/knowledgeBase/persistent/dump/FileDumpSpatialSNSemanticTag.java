package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.SpatialSNSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpSpatialSNSemanticTag extends FileDumpSNSemanticTag implements SpatialSNSemanticTag {

    private final SpatialSNSemanticTag snSemanticTag;

    public FileDumpSpatialSNSemanticTag(FileDumpSharkKB fileDumpSharkKB, SpatialSNSemanticTag tag) {
        super(fileDumpSharkKB, tag);
        snSemanticTag = tag;
    }

    @Override
    public SharkGeometry getGeometry() {
        return new FileDumpSharkGeometry(this.kb, this.snSemanticTag.getGeometry());
    }
}
