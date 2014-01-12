/**
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 */

package net.sharkfw.knowledgeBase.models;

/**
 * @author phl
 *
 */
public class DBSubjectIdentifierModel {
	private int id;
	private int stId;
	private String uri;

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getStId() {
		return stId;
	}

	public void setStId(final int stId) {
		this.stId = stId;
	}

	public void setUri(final String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
}
