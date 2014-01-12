/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.system.Util;

/**
 * DB impl for TimeSTSet. Storing FROM and TO values as properties in JSON
 * 
 * @see DBSemanticTagModel
 * @see DataBaseHandler
 * 
 * @author mfi
 */
public class SQLTimeSTSet extends SQLSTSet implements TimeSTSet {
  
  public SQLTimeSTSet(DataBaseHandler handler, int dimension) {
    super(handler, dimension);
  }

  @Override
  public TimeSemanticTag createTimeSemanticTag(long from, long to) throws SharkKBException {
    JSONObject props = new JSONObject();
    
    String fromString = String.valueOf(from);
    String toString = String.valueOf(to);
    
    try {
      props.put(TimeSemanticTag.FROM, fromString);
      props.put(TimeSemanticTag.TO, toString);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new SharkKBException("Can't set JSON properties:" + e.getMessage());
    }
    
    String[] si = {"sharkTime://" + fromString + "," + toString};
    
    // If from and to are == 0 the ANY Tag is meant. Add ANYURL to si[]
    if(from == TimeSTSet.TIME_FIRST_KNOWN_TIME && to == TimeSTSet.TIME_FOREVER) {
      Util.addString(si, ContextSpace.ANYURL);
    }
    DBSemanticTagModel stm = this.handler.createSemanticTag(props.toString(), this.dim, si);
    return new SQLTimeSemanticTag(stm, this.handler);
  }

  @Override
  public TimeSemanticTag createTimeSemanticTag(long from, long to, String name) throws SharkKBException {
    // TODO: Set properties in JSON. Create SI
    DBSemanticTagModel stm = this.handler.createSemanticTag("{}", this.dim, null);
    return new SQLTimeSemanticTag(stm, this.handler);
  }

  @Override
  public TimeSemanticTag createAnyTimeSemanticTag() throws SharkKBException {
    return this.createTimeSemanticTag(TimeSTSet.TIME_FIRST_KNOWN_TIME, TimeSTSet.TIME_FOREVER); 
     
    }

}


