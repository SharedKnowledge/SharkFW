/**
 *
 */
package net.sharkfw.knowledgeBase.sql;

import java.util.List;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.models.DBContextPointModel;
import net.sharkfw.knowledgeBase.models.DBInformationModel;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.knowledgeBase.models.DBSemanticTagRelationModel;
import net.sharkfw.knowledgeBase.models.DBSubjectIdentifierModel;

/**
 * This is the interface for database connection.<br/>
 * <br/>
 * All known implementations: {@link DataBaseSQLiteHandlerImpl} <br/>
 * <br/>
 *
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 * @author marko johann
 * @date 29.03.2011
 */
public interface DataBaseHandler {

	/**
	 * This creates a new {@link DBSemanticTagModel} by properties as json, dimension and subject identifiers
	 *
	 * @param properties
	 *          - json object
	 * @param dimension
	 *          - use constants
	 * @param si
	 *          - uri
	 * @return {@link DBSemanticTagModel}
	 * @throws SharkSQLException
	 */
	public DBSemanticTagModel createSemanticTag(final String properties, final int dimension, final String... si) throws SharkSQLException;

	// ========================================================================

	/**
	 * This creates a new {@link DBSubjectIdentifierModel} by {@link DBSemanticTagModel} and an array of uris
	 *
	 * @param semanticTag
	 * @param si
	 *          - uri
	 * @return {@link DBSubjectIdentifierModel}[]
	 * @throws SharkSQLException
	 */
	public DBSubjectIdentifierModel[] createSubjectIdentifier(final DBSemanticTagModel semanticTag, final String... si)
			throws SharkSQLException;

	// ========================================================================

	/**
	 * This creates a new {@link DBInformationModel} by {@link DBContextPointModel}, information data and the properties
	 *
	 * @param contextPoint
	 * @param data
	 * @param properties
	 *
	 * @return {@link DBInformationModel}
	 * @throws SharkSQLException
	 */
	public DBInformationModel createInformation(final DBContextPointModel contextPoint, final byte[] data, final String properties)
			throws SharkSQLException;

	// ========================================================================

	/**
	 * This creates a new {@link DBSemanticTagModel}-Relation by subject, object and predicate
	 *
	 * @param subject
	 * @param object
	 * @param predicate
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 */
	public boolean createSemanticTagRelation(final DBSemanticTagModel subject, final DBSemanticTagModel object, final String predicate)
			throws SharkSQLException;

	// ========================================================================

	/**
	 * This creates a new {@link DBContexPointModel} by properties as json and {@link ContextCoordinates}
	 *
	 * @param properties
	 *          - json object
	 * @param contextCoordinates
	 * @return {@link DBContexPointModel}
	 * @throws SharkSQLException
	 */
	public DBContextPointModel createContextPoint(final String properties, final ContextCoordinates contextCoordinates)
			throws SharkSQLException;

	// ========================================================================

	/**
	 * This creates a new knowledgebase property by key and value
	 *
	 * @param key
	 *          - use constants
	 * @param value
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 */
	public boolean createKbProperty(final int key, final String value) throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns first matched {@link DBSemanticTagModel} by given semantic indentifiers and dimension
	 *
	 * @param si
	 * @param dimension
	 * @return
	 * @throws SharkSQLException
	 */
	public DBSemanticTagModel getSemanticTag(final int dimension, final String... si) throws SharkSQLException;

	// ========================================================================

	/**
	 * This return a list of {@link DBSemanticTagModel} of a dimension
	 *
	 * @param dimension
	 *          - use constants
	 * @return
	 * @throws SharkSQLException
	 */
	public List<DBSemanticTagModel> getSemanticTag(final int dimension) throws SharkSQLException;

	// ========================================================================

	/**
	 * This return a {@link DBSemanticTagModel} by id
	 *
	 * @param id
	 * @return
	 * @throws SharkSQLException
	 */
	public DBSemanticTagModel getSemanticTagById(final int id) throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns a list of subject identifiers of a dimension
	 *
	 * @param dimension
	 *          - use constants
	 * @return
	 * @throws SharkSQLException
	 */
	public List<String> getSubjectIdentifier(final int dimension) throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns the information data as byte[] by information id
	 *
	 * @param id
	 * @return
	 * @throws SharkSQLException
	 */
	public byte[] getInformationContent(final int id) throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns an array of information models without data!!!
	 *
	 * @param cpId
	 *          - id of context point
	 * @return
	 */
	public DBInformationModel[] getInformation(final int cpId);

	// ========================================================================

	/**
	 * This returns the {@link DBContexPointModel} by {@link ContextCoordinates}
	 *
	 * @param contextCoordinates
	 * @return
	 * @throws SharkSQLException
	 */
	public DBContextPointModel getContextPoint(final ContextCoordinates contextCoordinates) throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns the {@link DBContexPointModel} by cp id
	 *
	 * @param id
	 * @return
	 * @throws SharkSQLException
	 */
	public DBContextPointModel getContextPoint(final int id) throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns a list of {@link DBSemanticTagModel} predicates by {@link DBSemanticTagModel}
	 *
	 * @param semanticTagModel
	 *          - as subject
	 * @return
	 * @throws SharkSQLException
	 */
	public List<String> getSemanticTagRelationPredicates(final DBSemanticTagModel semanticTagModel) throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns a list of {@link DBSemanticTagModel}-objects by {@link DBSemanticTagModel} as subject and predicate
	 *
	 * @param semanticTagModel
	 *          - subject
	 * @param predicate
	 * @return
	 * @throws SharkSQLException
	 */
	public List<DBSemanticTagModel> getSemanticTagRelationObjects(final DBSemanticTagModel semanticTagModel, final String predicate)
			throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns the value of knowledgebase property by key. Value is null if key does not exist
	 *
	 * @param key
	 *          - use constants
	 * @return value or null if key does not exist
	 * @throws SharkSQLException
	 */
	public String getKbProperty(final int key) throws SharkSQLException;

	// ========================================================================

	/**
	 * This deletes the {@link DBContexPointModel} with {@link ContextCoordinates} and returns true if the deleting was successful otherwise
	 * false
	 *
	 * @param contextCoordinates
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 */
	public boolean deleteContextPoint(final ContextCoordinates contextCoordinates) throws SharkSQLException;

	// ========================================================================

	/**
	 * This deletes the {@link DBContexPointModel} by id and returns true if the creating was successful otherwise false
	 *
	 * @param id
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 */
	public boolean deleteContextPoint(final int id) throws SharkSQLException;

	// ========================================================================

	/**
	 * This deletes {@link DBSemanticTagModel} and all subject identifiers which belongs to this {@link DBSemanticTagModel}. If one or more
	 * cp-reference exists {@link SharkSQLReferenceExistsException}
	 *
	 * @param model
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 * @throws SharkSQLReferenceExistsException
	 */
	public boolean deleteSemanticTag(final DBSemanticTagModel model) throws SharkSQLException, SharkSQLReferenceExistsException;

	// ========================================================================

	/**
	 * This removes {@link DBSemanticTagModel} from context point. will be thrown
	 *
	 * @param tag
	 * @param cp
	 *
	 * @return
	 * @throws SharkSQLException
	 */
	public boolean removeSemanticTagFromContextPoint(final DBSemanticTagModel tag, final DBContextPointModel cp) throws SharkSQLException;

	// ========================================================================

	/**
	 * This deletes {@link DBSubjectIdentifierModel} and returns true if the deleting was successful otherwise false
	 *
	 * @param si
	 * @return
	 * @throws SharkSQLException
	 */
	public boolean deleteSubjectIdentifier(final DBSubjectIdentifierModel si) throws SharkSQLException;

	// ========================================================================

	/**
	 * Thisd deletes {@link DBSemanticTagModel}-Relation by subject, object and predicate
	 *
	 * @param relation
	 * @return
	 * @throws SharkSQLException
	 */
	public boolean deleteSemanticTagRelation(final DBSemanticTagRelationModel relation) throws SharkSQLException;

	// ========================================================================

	/**
	 * This deletes {@link DBSemanticTagModel} by given {@link DBSemanticTagModel} and returns true if the creating was successful otherwise
	 * false
	 *
	 * @param semanticTagModel
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 */
	public boolean updateDBSemanticTag(final DBSemanticTagModel semanticTagModel) throws SharkSQLException;

	// ========================================================================

	/**
	 * This only updates the information of {@link DBContexPointModel} and data if updateData is true.
	 * After a successfully updating will be returned true.
	 *
	 * @param informationModel
	 * @param updateData
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 */
	public boolean updateInformation(final DBInformationModel informationModel, final boolean updateData) throws SharkSQLException;

	// ========================================================================

	/**
	 * This only updates properties of {@link DBContextPointModel} and returns true if the creating was successful otherwise false
	 *
	 * @param contextPointModel
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 */
	public boolean updateContextPoint(final DBContextPointModel contextPointModel) throws SharkSQLException;

	// ========================================================================

	/**
	 * This returns the last entry id of selected table<br/>
	 * <ul>
	 * <li>SQLiteDataBaseAccess.DB_TABLE_SI</li>
	 * <li>SQLiteDataBaseAccess.DB_TABLE_ST</li>
	 * <li>SQLiteDataBaseAccess.DB_TABLE_ST_REL</li>
	 * <li>SQLiteDataBaseAccess.DB_TABLE_INFO</li>
	 * <li>SQLiteDataBaseAccess.DB_TABLE_CP</li>
	 * <li>SQLiteDataBaseAccess.DB_TABLE_KB_PROPERTY</li>
	 * </ul>
	 *
	 * @param table
	 * @return
	 * @throws SharkSQLException
	 */
	public int getLastId(final String table) throws SharkSQLException;
}
