package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.PeerSNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.TXSemanticTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpPeerSNSemanticTag extends DumpSNSemanticTag implements PeerSNSemanticTag {

    private final PeerSNSemanticTag peerSNSemanticTag;

    public DumpPeerSNSemanticTag(DumpSharkKB dumpSharkKB, PeerSNSemanticTag tag) {
        super(dumpSharkKB, tag);
        peerSNSemanticTag = tag;
    }

    @Override
    public String[] getAddresses() {
        return this.peerSNSemanticTag.getAddresses();
    }

    @Override
    public void setAddresses(String[] addresses) {
        this.peerSNSemanticTag.setAddresses(addresses);
        this.kb.persist();
    }

    @Override
    public void removeAddress(String address) {
        this.peerSNSemanticTag.removeAddress(address);
        this.kb.persist();
    }

    @Override
    public void addAddress(String address) {
        this.peerSNSemanticTag.addAddress(address);
        this.kb.persist();
    }


    @Override
    public Enumeration<SemanticTag> subTags() {
        Enumeration<SemanticTag> semanticTagEnumeration = this.peerSNSemanticTag.subTags();
        ArrayList<SemanticTag> list = new ArrayList<>();
        while (semanticTagEnumeration.hasMoreElements()){
            list.add(new DumpSemanticTag(this.kb, semanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public TXSemanticTag getSuperTag() {
        return new DumpTXSemanticTag(this.kb, this.peerSNSemanticTag.getSuperTag());
    }

    @Override
    public Enumeration<TXSemanticTag> getSubTags() {
        Enumeration<TXSemanticTag> semanticTagEnumeration = this.peerSNSemanticTag.getSubTags();
        ArrayList<TXSemanticTag> list = new ArrayList<>();
        while (semanticTagEnumeration.hasMoreElements()){
            list.add(new DumpTXSemanticTag(this.kb, semanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public void move(TXSemanticTag supertag) {
        this.peerSNSemanticTag.move(supertag);
        this.kb.persist();
    }

    @Override
    public void merge(TXSemanticTag toMerge) {
        this.peerSNSemanticTag.merge(toMerge);
        this.kb.persist();
    }

}
