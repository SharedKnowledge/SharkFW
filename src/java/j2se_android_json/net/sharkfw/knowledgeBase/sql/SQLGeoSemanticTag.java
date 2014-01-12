/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.GeoSemanticTag;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;

/**
 * DB impl of GeoSemanticTag. Longitude and Latitude are stored as properties inside
 * a JSON object.
 * 
 * Distance function needs to be redone.
 * 
 * @author mfi
 */
public class SQLGeoSemanticTag extends SQLSemanticTag implements GeoSemanticTag{

  public SQLGeoSemanticTag(DBSemanticTagModel model, DataBaseHandler handler) {
    super(model, handler);
  }
  
  public SQLGeoSemanticTag(DBSemanticTagModel model, DataBaseHandler handler, double latitude, double longitude) {
    super(model, handler);
    
    JSONObject props = new JSONObject();
    String latString = String.valueOf(latitude);
    String longString = String.valueOf(longitude);
    
    try {
      props.put(GeoSemanticTag.LATITUDE, latString);
      props.put(GeoSemanticTag.LONGITUDE, longString);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  @Override
  public double getLatitude() {
    return Double.parseDouble(this.getProperty(GeoSemanticTag.LATITUDE));
  }

  @Override
  public double getLongitude() {
    return Double.parseDouble(this.getProperty(GeoSemanticTag.LONGITUDE));
  }

  @Override
  public double getDistance(GeoSemanticTag gc) {
    // TODO: (Re)implement distance function
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isInRange(GeoSemanticTag gc, double range) {
    // TODO: Reimplement isInRange using distance function
    throw new UnsupportedOperationException("Not supported yet.");
  }


}
