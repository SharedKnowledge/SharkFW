/**
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 */
package net.sharkfw.knowledgeBase.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This model contains all information about semantic model.</br></br> Supported by <a href="http://www.webXells.com">webXells
 * GmbH</a></br></br>
 *
 * @author phl
 *
 */
public class DBSemanticTagModel {
	private int id;
	private int dimension;
	private JSONObject properties;
	private final List<DBSubjectIdentifierModel> subjectIdentifierList;

	/**
	 * @param id
	 */
	public DBSemanticTagModel() {
		super();
		subjectIdentifierList = new ArrayList<DBSubjectIdentifierModel>();
	}

	//	public DBSemanticTagModel(final String name, final int dimension) {
	//		super();
	//		this.dimension = dimension;
	//		/*
	//		 * NAME = "name" SI = "si" ID = "id" HIDDEN = "hidden"
	//		 */
	//		addProperty(ROSemanticTag.NAME, name);
	//		semanticIdentifierList = new ArrayList<DBSemanticIdentifierModel>();
	//	}

	public void addProperty(final String key, final String value) {
		try {
			properties.put(key, value);
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setProperty(final String property) {
		try {
			this.properties = new JSONObject(property);
		}
		catch (final JSONException e) {
		}
	}

	public String getProperties() {
		return properties == null ? "{}" : properties.toString();
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	// ========================================================================

	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}

	// ========================================================================

	/**
	 * @param dimension
	 *          the dimension to set
	 */
	public void setDimension(final int dimension) {
		this.dimension = dimension;
	}

	// ========================================================================

	/**
	 * @return the semanticIdentifierList
	 */
	public List<DBSubjectIdentifierModel> getSubjectIdentifierList() {
		return subjectIdentifierList;
	}

	// ========================================================================

	public void addSubjectIdentifier(final DBSubjectIdentifierModel si) {
		subjectIdentifierList.add(si);
	}

	// ========================================================================

	public void addSubjectIdentifier(final List<DBSubjectIdentifierModel> sis) {
		subjectIdentifierList.addAll(sis);
	}

	// ========================================================================


	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append(getClass().getSimpleName()).append(" [");
		buf.append(" Id: ").append(id);
		buf.append(" - Dimension: ").append(dimension);
		buf.append(" - Property: ").append(properties.toString());
		buf.append(" - Si: ").append(Arrays.toString(subjectIdentifierList.toArray()));
		buf.append("]");

		return buf.toString();
	}
}
