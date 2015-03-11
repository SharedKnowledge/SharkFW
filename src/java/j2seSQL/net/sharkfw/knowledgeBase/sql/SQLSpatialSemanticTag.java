package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 *
 * @author thsc
 */
public class SQLSpatialSemanticTag extends SQLSemanticTag implements SpatialSemanticTag {
    
    SQLSpatialSemanticTag(SQLSemanticTagStorage sqlST) throws SharkKBException {
        super(sqlST);
        
        if(sqlST.getType() != SQLSharkKB.TIME_SEMANTIC_TAG_TYPE) {
            throw new SharkKBException("cannot create time semantic tag with non time semantic tag values");
        }
    }

    @Override
    public SharkGeometry getGeometry() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
