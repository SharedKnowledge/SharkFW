package net.sharkfw.knowledgeBase.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This model contains key and value of a property</br></br> Supported by <a href="http://www.webXells.com">webXells GmbH</a></br></br>
 *
 * @author phl
 *
 */
public class DBKBPropertyModel {
	private int key;
	private JSONObject value;

	public void setKey(final int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}

	public void setValue(final String value) {
		try {
			this.value = new JSONObject(value);
		}
		catch (final JSONException e) {
			try {
				this.value = new JSONObject("{}");
			}
			catch (final JSONException e1) {
			}
		}
	}

	public JSONObject getValue() {
		return value;
	}

	// ========================================================================

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append(getClass().getSimpleName()).append(" [");
		buf.append("Key: ").append(key);
		buf.append(" - Value: ").append(value).append("]");

		return buf.toString();
	}
}
