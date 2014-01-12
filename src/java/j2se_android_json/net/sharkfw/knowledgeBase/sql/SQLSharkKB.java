/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.AbstractSharkKB;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.ExposedInterest;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.GeoSTSet;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.LocalInterest;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.ROSTSet;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoAssociatedSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoGeoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerAssociatedSTSet;
import net.sharkfw.knowledgeBase.models.DBContextPointModel;
import net.sharkfw.knowledgeBase.time.InMemoTimeSTSet;

/**
 *
 * @author mfi
 */
public class SQLSharkKB extends AbstractSharkKB{

  private DataBaseHandler handler;
  
  // choosing a high number to not conflict with STSets
  private static final int KBPROP = 1000;
  
  /*
   * Handing out In Memory instances for temporary usages
   */
  public SQLSharkKB(String name, DataBaseHandler handler) {
    // TODO: Store name as property
    this.handler = handler;
    String kbprops = this.handler.getKbProperty(1000);
    //JSONObject kbprops = new JSONObject(kbprops);
  }
  
  @Override
  public STSet getTopicDimInstance() {
    return new InMemoAssociatedSTSet();
  }

  @Override
  public PeerSTSet getPeerDimInstance() {
    return new InMemoPeerAssociatedSTSet();
  }

  @Override
  public TimeSTSet getTimeDimInstance() {
    return new InMemoTimeSTSet();
  }

  @Override
  public GeoSTSet getGeoDimInstance() {
    return new InMemoGeoSTSet();
  }

  @Override
  public void assimilate(Knowledge k) throws SharkKBException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void assimilate(Knowledge k, FragmentationParameter[] otps, LocalInterest effBackground) throws SharkKBException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
    // Select contextpoint with the given coordinates from DB.

    DBContextPointModel cp = this.handler.getContextPoint(coordinates);
    if(cp == null) {
      throw new SharkKBException("ContextPoint not found for coordinates: " + coordinates);
    } else {
      return new SQLContextPoint(this.handler, cp);
    }
  }

  @Override
  public ContextPoint createContextPoint(ContextCoordinates coordinates, String[] dimNames) throws SharkKBException {
    /*
     * Create a new SQLContextPoint with the given coordinates inside the KB. 
     * Information is not present.
     */
    DBContextPointModel cp = this.handler.createContextPoint("{}", coordinates);
    if(cp == null) {
      throw new SharkKBException("Unable to create ContextPoint in DB!");
    }
    return new SQLContextPoint(this.handler, cp);
  }

  @Override
  public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException {
    /*
     * Delete a contextpoint, including all its Information from the KB
     * Deleting the ContextPoint won't touch the SemantcTags, that are addressed
     * in its dimensions.
     */
    if(!this.handler.deleteContextPoint(coordinates)) {
      throw new SharkKBException("Unable to remove ContextPoint with coordinates: " + coordinates);
    }
  }

  @Override
  public String saveExposedInterest(String ownerString, String uniqueName, ExposedInterest interest) throws SharkKBException {
    /*
     * Save a serialized version of the interest to a certain 
     * yet to be determined Contextpoint.
     */
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ExposedInterest getExposedInterestByName(String ownerName, String uniqueName) {
    /*
     * Return an ExposedInterest (object) from the serialized representation
     * which is stored under the uniqueName at the ContextPoints which
     * holds all interests of the local owner
     */
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void removeInterest(String ownerName, String uniqueName) throws SharkKBException {
    /*
     * Remove the information holding the serialized interest determined by
     * uniqueName from the kb.
     */
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Enumeration interests(String ownerName) {
    /*
     * Read all Information from the contextpoint that holds the LocalInterests
     * of the owner peer.
     */
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setProperty(String name, String value) throws SharkKBException {
    /*
     * Using numeric constants to identify either STSet's dimension
     * or a designated constant to identify the property as KB related.
     * value is a JSON string.
     */
    // TODO: attach value to existing JSON object
    this.handler.createKbProperty(SQLSharkKB.KBPROP, value);
    
  }

  @Override
  public String getProperty(String name) throws SharkKBException {

    /*
     * Get the value of the KB properties. 
     * Parse the value to K:V pairs.
     */
    // TODO: this.handler.getKbProperty(int key)
    return "";
  }

  @Override
  public ROSTSet getSTSet(int dim) throws SharkKBException {
    /*
     * Return an instance of SQLSTSet (or one of its subclasses) according
     * to the given dimension.
     */
    if(dim == ContextSpace.DIM_DIRECTION) {
      return new SQLSTSet(this.handler, ContextSpace.DIM_DIRECTION);
      
    } else if(dim == ContextSpace.DIM_ORIGINATOR) {
      return new SQLPeerAssociatedSTSet(this.handler, ContextSpace.DIM_ORIGINATOR);
      
    } else if(dim == ContextSpace.DIM_PEER) {
      return new SQLPeerAssociatedSTSet(this.handler, ContextSpace.DIM_PEER);
    
    } else if(dim == ContextSpace.DIM_REMOTEPEER) {
      return new SQLPeerAssociatedSTSet(this.handler, ContextSpace.DIM_REMOTEPEER);
    
    } else if(dim == ContextSpace.DIM_LOCATION) {
      return new SQLGeoSTSet(this.handler, ContextSpace.DIM_LOCATION);
      
    } else if(dim == ContextSpace.DIM_TIME) {
      return new SQLTimeSTSet(this.handler, ContextSpace.DIM_TIME);
      
    } else {
      return new SQLAssociatedSTSet(this.handler, ContextSpace.DIM_TOPIC);
    }
  }

}
