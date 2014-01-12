/**
 *
 */
package net.sharkfw.knowledgeBase.models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phl
 *
 */
public class DBSTSetModel{
	private List<DBSemanticTagModel> stModels;
	/**
	 * @param dimension
	 */
	public DBSTSetModel() {
		super();
		stModels=new ArrayList<DBSemanticTagModel>();

	}
	public void addST(final DBSemanticTagModel stModel) {
		stModels.add(stModel);
	}

	public List<DBSemanticTagModel> getStModels() {
		return stModels;
	}
	public void setStModels(final List<DBSemanticTagModel> stModels) {
		this.stModels = stModels;
	}
	public void removeST(final DBSemanticTagModel st) {
		stModels.remove(st);
	}
}
