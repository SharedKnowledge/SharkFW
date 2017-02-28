package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

import java.util.Enumeration;

/**
 * Created by j4rvis on 2/27/17.
 */
public class FileDumpTimeSemanticTag extends FileDumpSemanticTag implements TimeSemanticTag {
    @Override
    public long getFrom() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }
}
