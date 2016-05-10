package net.sharkfw.security.pki.storage;

import net.sharkfw.pki.SharkCertificate;

/**
 * Interface describing a listener for SharkPkiStorage events
 *
 * Currently not used. The provided methods of the {@link net.sharkfw.kp.KPListener} used in the
 * {@link net.sharkfw.security.pki.SharkPkiKP} covers the current demands.
 *
 * @author ac
 */
public interface SharkPkiStorageListener {

    /**
     * Called when certificate added
     * @param sharkCertificate {@link SharkCertificate}
     */
    void certificateAdded(SharkCertificate sharkCertificate);

    /**
     * Called when certificate updated
     * @param sharkCertificate  {@link SharkCertificate}
     */
    void certificateUpdated(SharkCertificate sharkCertificate);

    /**
     * Called when certificate expired
     * @param sharkCertificate  {@link SharkCertificate}
     */
    void certificateExpired(SharkCertificate sharkCertificate);

    /**
     * Called when certificate deleted
     * @param sharkCertificate  {@link SharkCertificate}
     */
    void certificateDeleted(SharkCertificate sharkCertificate);
}