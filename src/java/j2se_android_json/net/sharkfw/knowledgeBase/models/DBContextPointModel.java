/**
 *
 *
 */

package net.sharkfw.knowledgeBase.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This model contains all information about context point </br>
 * </br>
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a> </br>
 * </br>
 *
 * @author phl
 *
 */
public class DBContextPointModel {

	private int id;
	private JSONObject properties;
	private DBSemanticTagModel remote;
	private DBSemanticTagModel time;
	private DBSemanticTagModel location;
	private DBSemanticTagModel peer;
	private DBSemanticTagModel originator;
	private DBSemanticTagModel topic;
	private DBSemanticTagModel io;
	private DBInformationModel[] information;

	public void setProperties(final String properties) {
		try {
			this.properties = new JSONObject(properties);
		}
		catch (JSONException e) {
			try {
				this.properties = new JSONObject("{}");
			}
			catch (final JSONException e1) {
			}
		}
	}

	public String getProperties() {
		return properties == null ? "{}" : properties.toString();
	}

	public void setRemote(final DBSemanticTagModel remote) {
		this.remote = remote;
	}

	public DBSemanticTagModel getRemote() {
		return remote;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public DBSemanticTagModel getTime() {
		return time;
	}

	public void setTime(final DBSemanticTagModel time) {
		this.time = time;
	}

	public DBSemanticTagModel getLocation() {
		return location;
	}

	public void setLocation(final DBSemanticTagModel location) {
		this.location = location;
	}

	public DBSemanticTagModel getPeer() {
		return peer;
	}

	public void setPeer(final DBSemanticTagModel peer) {
		this.peer = peer;
	}

	public DBSemanticTagModel getOriginator() {
		return originator;
	}

	public void setOriginator(final DBSemanticTagModel originator) {
		this.originator = originator;
	}

	public DBSemanticTagModel getTopic() {
		return topic;
	}

	public void setTopic(final DBSemanticTagModel topic) {
		this.topic = topic;
	}

	public DBSemanticTagModel getIo() {
		return io;
	}

	public void setIo(final DBSemanticTagModel io) {
		this.io = io;
	}

	// ========================================================================

	/**
	 * @return the information
	 */
	public DBInformationModel[] getInformation() {
		return information;
	}

	// ========================================================================

	/**
	 * @param information the information to set
	 */
	public void setInformation(final DBInformationModel[] information) {
		this.information = information;
	}

	// ========================================================================


	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append(getClass().getName()).append(" [");
		buf.append(" Id: ").append(id);
		buf.append("]");

		return buf.toString();
	}
}
