/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.GeoSTSet;
import net.sharkfw.knowledgeBase.GeoSemanticTag;
import net.sharkfw.knowledgeBase.ROGeoSTSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;

/**
 *
 * @author mfi
 */
public class SQLGeoSTSet extends SQLSTSet implements GeoSTSet{

  public SQLGeoSTSet(DataBaseHandler handler, int dimension) {
    super(handler, dimension);
  }
  
  @Override
  public GeoSemanticTag createGeoSemanticTag(double latitude, double longitude) throws SharkKBException {
    
    // Prepare properties
    String latString = String.valueOf(latitude);
    String longString = String.valueOf(longitude);
    
    JSONObject props = new JSONObject();
    try {
      props.put(GeoSemanticTag.LATITUDE, latString);
      props.put(GeoSemanticTag.LONGITUDE, longString);
    } catch (JSONException e) {
      
      e.printStackTrace();
      throw new SharkKBException("Can't set lat/long properties: " + e.getMessage());
    }
    
    // Prepare SI
    String[] si = {"http://" + Double.toString(latitude) + ";" + Double.toString(longitude)};
    
    // Write to DB
    DBSemanticTagModel stm = this.handler.createSemanticTag(props.toString(), this.dim, si);
    return new SQLGeoSemanticTag(stm, this.handler);
  }

  @Override
  public GeoSemanticTag getGeoSemanticTag(String si) throws SharkKBException {
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    return new SQLGeoSemanticTag(stm, this.handler);
  }

  @Override
  public GeoSemanticTag getGeoSemanticTag(String[] si) throws SharkKBException {
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    return new SQLGeoSemanticTag(stm, this.handler);
  }

  @Override
  public GeoSemanticTag getGeoSemanticTag(double latitude, double longitude) throws SharkKBException {
    // Prepare SI
    String[] si = {"http://" + Double.toString(latitude) + ";" + Double.toString(longitude)};

    // Query DB
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    return new SQLGeoSemanticTag(stm, this.handler);
  }

  @Override
  public double getDistance(GeoSemanticTag gc1, GeoSemanticTag gc2) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isInRange(GeoSemanticTag gc1, GeoSemanticTag gc2, double radius) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ROGeoSTSet fragment(Enumeration anchor, double range) throws SharkKBException {
    throw new UnsupportedOperationException("Not supported yet.");
  }


}
