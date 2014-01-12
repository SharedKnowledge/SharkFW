/**
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 */
package net.sharkfw.knowledgeBase.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This model contains information and binary data</br></br> Supported by <a href="http://www.webXells.com">webXells GmbH</a></br></br>
 *
 * @author phl
 *
 */
public class DBInformationModel {
	private int id;
	private byte[] data;
	private JSONObject property;
	private int contextPointId;

	public void setId(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setData(final byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setProperty(final String property) {
		try {
			this.property = new JSONObject(property);
		}
		catch (JSONException e) {
			try {
				this.property = new JSONObject("{}");
			}
			catch (JSONException e1) {
			}
		}
	}

	public String getProperty() {
		return property == null ? "{}" : property.toString();
	}

	public void setContextPointId(final int contextPointId) {
		this.contextPointId = contextPointId;
	}

	public int getContextPointId() {
		return contextPointId;
	}
}
