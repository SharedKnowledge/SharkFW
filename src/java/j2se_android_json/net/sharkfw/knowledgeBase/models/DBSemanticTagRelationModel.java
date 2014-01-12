/**
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 */

package net.sharkfw.knowledgeBase.models;

/**
 * This model contains all information about a relation between semantic models.</br></br> Supported by <a
 * href="http://www.webXells.com">webXells GmbH</a></br></br>
 *
 * @author phl
 *
 */
public class DBSemanticTagRelationModel {
	private String predicate;
	private DBSemanticTagModel subject;
	private DBSemanticTagModel object;

	/**
	 * @param predicate
	 * @param subject
	 * @param object
	 */
	public DBSemanticTagRelationModel() {
		super();
	}

	public boolean isSubject(final DBSemanticTagModel subject, final DBSemanticTagModel object) {
		//TODO equals methode überschreiben
		if (subject == this.subject && object == this.object) {
			return true;
		}
		return false;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(final String predicate) {
		this.predicate = predicate;
	}

	public DBSemanticTagModel getSubject() {
		return subject;
	}

	public void setSubject(final DBSemanticTagModel subject) {
		this.subject = subject;
	}

	public DBSemanticTagModel getObject() {
		return object;
	}

	public void setObject(final DBSemanticTagModel object) {
		this.object = object;
	}
}
