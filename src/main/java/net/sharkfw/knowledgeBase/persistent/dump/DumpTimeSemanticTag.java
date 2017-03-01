package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 * Created by j4rvis on 2/27/17.
 */
public class DumpTimeSemanticTag extends DumpSemanticTag implements TimeSemanticTag {

    private final TimeSemanticTag timeSemanticTag;

    public DumpTimeSemanticTag(DumpSharkKB dumpSharkKB, TimeSemanticTag tag) {
        super(dumpSharkKB, tag);
        timeSemanticTag = tag;
    }

    @Override
    public long getFrom() {
        return this.timeSemanticTag.getFrom();
    }

    @Override
    public long getDuration() {
        return this.timeSemanticTag.getDuration();
    }
}
