package net.sharkfw.security.utilities;

/**
 * @author ac
 */

/**
 * Asymmetric encryption
 * see https://docs.oracle.com/javase/7/docs/api/java/security/KeyPairGenerator.html
 * for additional algorithms
 */
public enum SharkKeyPairAlgorithm {
    RSA("RSA/ECB/PKCS1Padding");

    private String spec;

    SharkKeyPairAlgorithm(String spec) {
        this.spec = spec;
    }

    public String getSpec() {
        return spec;
    }
}
