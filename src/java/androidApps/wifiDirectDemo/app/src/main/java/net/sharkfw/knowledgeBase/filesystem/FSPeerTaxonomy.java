package net.sharkfw.knowledgeBase.filesystem;

import net.sharkfw.knowledgeBase.inmemory.InMemoPeerSemanticNet;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerTaxonomy;

/**
 *
 * @author thsc
 */
public class FSPeerTaxonomy extends InMemoPeerTaxonomy {
    public FSPeerTaxonomy(String foldername) {
        super(
            new InMemoPeerSemanticNet(
                new FSGenericTagStorage(foldername)
            )
        );
    }
}
