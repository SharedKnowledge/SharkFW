package net.sharkfw.security.pki.storage;

import net.sharkfw.pki.SharkCertificate;

/**
 * Interface describing a listener for SharkPkiStorage events
 * @author ac
 */
public interface SharkPkiStorageListener {

    /**
     * Called when certificate added
     * @param sharkCertificate
     */
    public void certificateAdded(SharkCertificate sharkCertificate);

    /**
     * Called when certificate updated
     * @param sharkCertificate
     */
    public void certificateUpdated(SharkCertificate sharkCertificate);

    /**
     * Called when certificate expired
     * @param sharkCertificate
     */
    public void certificateExpired(SharkCertificate sharkCertificate);

    /**
     * Called when certificate deleted
     * @param sharkCertificate
     */
    public void certificateDeleted(SharkCertificate sharkCertificate);
}