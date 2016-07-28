package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 * Created by thsc42 on 28.07.16.
 */
class SyncSpatialSemanticTag extends SyncSemanticTag implements SpatialSemanticTag {

    SyncSpatialSemanticTag(SpatialSemanticTag target) {
        super(target);
    }

    protected SyncSpatialSemanticTag getTarget() {
        return (SyncSpatialSemanticTag) super.getTarget();
    }

    @Override
    public SharkGeometry getGeometry() {
        return this.getTarget().getGeometry();
    }
}
