package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkURI;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
public class SQLTimeSTSet extends SQLSTSet implements TimeSTSet {
    
    SQLTimeSTSet(SQLSharkKB kb) {
        super(kb, SQLSharkKB.TIME_SEMANTIC_TAG_TYPE);
    }

    @Override
    public TimeSTSet fragment(TimeSemanticTag anchor) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSTSet contextualize(TimeSTSet context, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        SQLSemanticTagStorage sqlST = this.createSQLSemanticTag(
                this.getSSQLSharkKB(), null, null, 
                from, duration, 
                false, SQLSharkKB.TIME_SEMANTIC_TAG_TYPE, 
                new String[] {SharkURI.timeST(from, duration)}, // sis
                null); // no address
        
        return new SQLTimeSemanticTag(this.getSSQLSharkKB(), sqlST);
    }

    @Override
    public Enumeration<TimeSemanticTag> timeTags() throws SharkKBException {
        return new Iterator2Enumeration(this.tstTags());
    }

    @Override
    public Iterator<TimeSemanticTag> tstTags() throws SharkKBException {
        return this.tags(SQLSharkKB.TIME_SEMANTIC_TAG_TYPE);
    }
}
