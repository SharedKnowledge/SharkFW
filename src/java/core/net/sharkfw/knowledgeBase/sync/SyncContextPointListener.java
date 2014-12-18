package net.sharkfw.knowledgeBase.sync;

/**
 * A listener interface to listen for version increments on a given SyncContextPoint.
 * Events usually cover the version increments on a ContextPoint.
 * 
 * @author hellerve
 */
public interface SyncContextPointListener {
    
  public void versionChanged(SyncContextPoint p);
 
}
