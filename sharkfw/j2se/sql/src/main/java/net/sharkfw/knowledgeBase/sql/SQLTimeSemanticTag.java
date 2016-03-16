package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 *
 * @author thsc
 */
class SQLTimeSemanticTag extends SQLSemanticTag implements TimeSemanticTag {
    SQLTimeSemanticTag(SQLSharkKB kb, SQLSemanticTagStorage sqlST) throws SharkKBException {
        super(kb, sqlST);
        
        if(sqlST.getType() != SQLSharkKB.TIME_SEMANTIC_TAG_TYPE) {
            throw new SharkKBException("cannot create time semantic tag with non time semantic tag values");
        }
    }

    @Override
    public long getFrom() {
        return this.sqlST.getStartTime();
    }

    @Override
    public long getDuration() {
        return this.sqlST.getDurationTime();
    }
}
