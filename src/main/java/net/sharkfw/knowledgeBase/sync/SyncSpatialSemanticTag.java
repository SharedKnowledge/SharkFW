package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 * Created by thsc42 on 28.07.16.
 */
class SyncSpatialSemanticTag extends SyncSemanticTag implements SpatialSemanticTag {
    private final SpatialSemanticTag target;
    
    SyncSpatialSemanticTag(SpatialSemanticTag target) {
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
    public SharkGeometry getGeometry() {
        return this.target.getGeometry();
    }
}
