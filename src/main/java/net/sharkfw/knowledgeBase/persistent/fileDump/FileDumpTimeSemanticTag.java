package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

import java.util.Enumeration;

/**
 * Created by j4rvis on 2/27/17.
 */
public class FileDumpTimeSemanticTag extends FileDumpSemanticTag implements TimeSemanticTag {

    private final TimeSemanticTag timeSemanticTag;

    public FileDumpTimeSemanticTag(FileDumpSharkKB fileDumpSharkKB, TimeSemanticTag tag) {
        super(fileDumpSharkKB, tag);
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
