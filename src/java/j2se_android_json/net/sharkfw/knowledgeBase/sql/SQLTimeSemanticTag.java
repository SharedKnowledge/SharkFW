/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;

/**
 *
 * @author mfi
 */
public class SQLTimeSemanticTag extends SQLSemanticTag implements TimeSemanticTag {

  public SQLTimeSemanticTag(DBSemanticTagModel model, DataBaseHandler handler) {
    super(model, handler);
  }
  
  @Override
  public long getFrom() {
    String fromString = this.getProperty(TimeSemanticTag.FROM);
    long from = Long.parseLong(fromString);
    
    return from;
  }

  @Override
  public long getTo() {
    String toString = this.getProperty(TimeSemanticTag.TO);
    long to = Long.parseLong(toString);
    
    return to;
  }


}
