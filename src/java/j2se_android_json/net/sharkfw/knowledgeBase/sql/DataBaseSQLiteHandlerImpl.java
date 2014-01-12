/**
 *
 */
package net.sharkfw.knowledgeBase.sql;

import java.util.ArrayList;
import java.util.List;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.models.DBContextPointModel;
import net.sharkfw.knowledgeBase.models.DBInformationModel;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.knowledgeBase.models.DBSemanticTagRelationModel;
import net.sharkfw.knowledgeBase.models.DBSubjectIdentifierModel;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.util.Log;

/**
 * This is an implementation of {@link DataBaseHandler}.<br/>
 * <br/>
 *
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 * @author pmj
 * @date 29.03.2011
 *
 */
public class DataBaseSQLiteHandlerImpl implements DataBaseHandler {
//	private static final String APOSTROPHE = "\"";

	/**
	 * This creates a new {@link DBSemanticTagModel} by properties as json, dimension and semantic identifiers
	 *
	 * @param properties
	 *          - json object
	 * @param dimension
	 *          - use constants
	 * @param si
	 *          - uri
	 * @return {@link DBSemanticTagModel}
	 * @throws SQLException
	 */
	@Override
	public DBSemanticTagModel createSemanticTag(final String properties, final int dimension, final String... sis) throws SharkSQLException {
		try {
			final DBSemanticTagModel existingSemanticTag = getSemanticTag(dimension, sis);

			if (existingSemanticTag != null) {
				return existingSemanticTag;
			}

			final StringBuffer buf = new StringBuffer();
			buf.append("INSERT INTO ");
			buf.append(SQLiteDataBaseAccess.DB_TABLE_ST);
			buf.append(" (dimension, properties)");
			buf.append(" VALUES (");
			buf.append(dimension).append(", ");
			buf.append(DatabaseUtils.sqlEscapeString(properties)).append(")");

			Log.d(getClass().getSimpleName() + " - createSemanticTag", buf.toString());

			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			final int id = getLastId(SQLiteDataBaseAccess.DB_TABLE_ST);

			final DBSemanticTagModel model = new DBSemanticTagModel();
			model.setId(id);
			model.setDimension(dimension);
			model.setProperty(properties);

			for (final String si : sis) {
				model.addSubjectIdentifier(createSubjectIdentifier(id, si));
			}

			return model;
		}
		catch (final Exception e) {
			throw e instanceof SharkSQLException ? (SharkSQLException) e : new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This creates a new {@link DBSubjectIdentifierModel} by semantic tag id and the uri
	 *
	 * @param stId
	 * @param uri
	 * @return
	 */
	private DBSubjectIdentifierModel createSubjectIdentifier(final int stId, final String uri) {
		try {
			final DBSubjectIdentifierModel existingModel = getSubjectIdentifierModel(stId, uri);

			if (existingModel != null) {
				return existingModel;
			}

			final StringBuffer buf = new StringBuffer();
			buf.append("INSERT INTO ");
			buf.append(SQLiteDataBaseAccess.DB_TABLE_SI);
			buf.append(" (st_id, uri)");
			buf.append(" VALUES (");
			buf.append(stId).append(", ");
			buf.append(DatabaseUtils.sqlEscapeString(uri)).append(")");

			Log.d(getClass().getSimpleName() + " - createSemanticIdentifierModel", buf.toString());

			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			final int id = getLastId(SQLiteDataBaseAccess.DB_TABLE_SI);
			final DBSubjectIdentifierModel model = new DBSubjectIdentifierModel();
			model.setId(id);
			model.setStId(stId);
			model.setUri(uri);

			return model;
		}
		catch (final Exception e) {
			throw e instanceof SharkSQLException ? (SharkSQLException) e : new SharkSQLException(e);
		}
	}

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
	@Override
	public DBSubjectIdentifierModel[] createSubjectIdentifier(final DBSemanticTagModel semanticTag, final String... si)
			throws SharkSQLException {
		final DBSubjectIdentifierModel[] modelList = new DBSubjectIdentifierModel[si.length];

		for (int i = 0; i < si.length; ++i) {
			modelList[i] = createSubjectIdentifier(semanticTag.getId(), si[i]);
		}

		return modelList;
	}

	// ========================================================================

	/**
	 * This creates a new {@link DBSemanticTagModel}-Relation by subject, object and predicate
	 *
	 * @param subject
	 * @param object
	 * @param predicate
	 * @return true if the creating was successful otherwise false
	 */
	@Override
	public boolean createSemanticTagRelation(final DBSemanticTagModel subject, final DBSemanticTagModel object, final String predicate) {
		try {
			final DBSemanticTagRelationModel existingModel = getSemanticTagRelation(subject, object, predicate);

			if (existingModel != null) {
				return true;
			}

			final StringBuffer buf = new StringBuffer();
			buf.append("INSERT INTO ");
			buf.append(SQLiteDataBaseAccess.DB_TABLE_ST_REL);
			buf.append(" (subject, object, predicate)");
			buf.append(" VALUES (");
			buf.append(subject.getId()).append(", ");
			buf.append(object.getId()).append(", ");
			buf.append(DatabaseUtils.sqlEscapeString(predicate)).append(")");

			Log.d(getClass().getSimpleName() + " - createSemanticTagRelation", buf.toString());

			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw e instanceof SharkSQLException ? (SharkSQLException) e : new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This creates a new {@link DBContextPointModel} by properties as json and {@link ContextCoordinates}
	 *
	 * @param properties
	 *          - json object
	 * @param contextCoordinates
	 * @return {@link DBContextPointModel}
	 */
	@Override
	public DBContextPointModel createContextPoint(final String properties, final ContextCoordinates contextCoordinates) {
		final DBSemanticTagModel[] semanticTagModels = new DBSemanticTagModel[ContextSpace.MAXDIMENSIONS];
		semanticTagModels[ContextSpace.DIM_DIRECTION] = getSemanticTag(ContextSpace.DIM_DIRECTION,
				contextCoordinates.getSI(ContextSpace.DIM_DIRECTION));
		semanticTagModels[ContextSpace.DIM_LOCATION] = getSemanticTag(ContextSpace.DIM_LOCATION,
				contextCoordinates.getSI(ContextSpace.DIM_LOCATION));
		semanticTagModels[ContextSpace.DIM_ORIGINATOR] = getSemanticTag(ContextSpace.DIM_ORIGINATOR,
				contextCoordinates.getSI(ContextSpace.DIM_ORIGINATOR));
		semanticTagModels[ContextSpace.DIM_PEER] = getSemanticTag(ContextSpace.DIM_PEER, contextCoordinates.getSI(ContextSpace.DIM_PEER));
		semanticTagModels[ContextSpace.DIM_REMOTEPEER] = getSemanticTag(ContextSpace.DIM_REMOTEPEER,
				contextCoordinates.getSI(ContextSpace.DIM_REMOTEPEER));
		semanticTagModels[ContextSpace.DIM_TIME] = getSemanticTag(ContextSpace.DIM_TIME, contextCoordinates.getSI(ContextSpace.DIM_TIME));
		semanticTagModels[ContextSpace.DIM_TOPIC] = getSemanticTag(ContextSpace.DIM_TOPIC, contextCoordinates.getSI(ContextSpace.DIM_TOPIC));

		try {
			final StringBuffer buf = new StringBuffer();
			buf.append("INSERT INTO ");
			buf.append(SQLiteDataBaseAccess.DB_TABLE_CP);
			buf.append(" (properties, io, location, originator, peer, remote, time, topic)");
			buf.append(" VALUES (");
			buf.append(DatabaseUtils.sqlEscapeString(properties)).append(", ");
			buf.append(semanticTagModels[ContextSpace.DIM_DIRECTION] == null ? -1 : semanticTagModels[ContextSpace.DIM_DIRECTION].getId())
					.append(", ");
			buf.append(semanticTagModels[ContextSpace.DIM_LOCATION] == null ? -1 : semanticTagModels[ContextSpace.DIM_LOCATION].getId())
			.append(", ");
			buf.append(semanticTagModels[ContextSpace.DIM_ORIGINATOR] == null ? -1 : semanticTagModels[ContextSpace.DIM_ORIGINATOR].getId())
					.append(", ");
			buf.append(semanticTagModels[ContextSpace.DIM_PEER] == null ? -1 : semanticTagModels[ContextSpace.DIM_PEER].getId()).append(", ");
			buf.append(semanticTagModels[ContextSpace.DIM_REMOTEPEER] == null ? -1 : semanticTagModels[ContextSpace.DIM_REMOTEPEER].getId())
					.append(", ");
			buf.append(semanticTagModels[ContextSpace.DIM_TIME] == null ? -1 : semanticTagModels[ContextSpace.DIM_TIME].getId()).append(", ");
			buf.append(semanticTagModels[ContextSpace.DIM_TOPIC] == null ? -1 : semanticTagModels[ContextSpace.DIM_TOPIC].getId()).append(")");

			Log.d(getClass().getSimpleName() + " - createContextPoint", buf.toString());

			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			final int id = getLastId(SQLiteDataBaseAccess.DB_TABLE_CP);
			final DBContextPointModel model = new DBContextPointModel();
			model.setId(id);
			model.setIo(semanticTagModels[ContextSpace.DIM_DIRECTION]);
			model.setLocation(semanticTagModels[ContextSpace.DIM_LOCATION]);
			model.setOriginator(semanticTagModels[ContextSpace.DIM_ORIGINATOR]);
			model.setPeer(semanticTagModels[ContextSpace.DIM_PEER]);
			model.setRemote(semanticTagModels[ContextSpace.DIM_REMOTEPEER]);
			model.setTime(semanticTagModels[ContextSpace.DIM_TIME]);
			model.setTopic(semanticTagModels[ContextSpace.DIM_TOPIC]);

			return model;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

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
	@Override
	public DBInformationModel createInformation(final DBContextPointModel contextPoint, final byte[] data, final String properties)
			throws SharkSQLException {
		try {
			final StringBuffer buf = new StringBuffer();
			buf.append("INSERT INTO ");
			buf.append(SQLiteDataBaseAccess.DB_TABLE_INFO);
			buf.append(" (data, properties, cp_id)");
			buf.append(" VALUES (");
//			buf.append(data).append(", ");
			buf.append("?, ");
			buf.append(DatabaseUtils.sqlEscapeString(properties)).append(", ");
			buf.append(contextPoint.getId()).append(")");

			Log.d(getClass().getSimpleName() + " - createInformation", buf.toString());

			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString(), data);

			final DBInformationModel model = new DBInformationModel();
			model.setId(getLastId(SQLiteDataBaseAccess.DB_TABLE_INFO));
			model.setContextPointId(contextPoint.getId());
			model.setProperty(properties);

			return model;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This creates a new knowledgebase property by key and value or updates the existing entry
	 *
	 * @param key
	 *          - use constants
	 * @param value
	 * @return true if the creating was successful otherwise false
	 */
	@Override
	public boolean createKbProperty(final int key, final String value) {
		final StringBuffer buf = new StringBuffer();
		final String existingValue = getKbProperty(key);

		if (existingValue != null) {
			buf.append("UPDATE ");
			buf.append(SQLiteDataBaseAccess.DB_TABLE_KB_PROPERTY);
			buf.append(" SET kb_value = ").append(DatabaseUtils.sqlEscapeString(value));
			buf.append(" WHERE kb_key = ").append(key);
		}
		else {
			buf.append("INSERT INTO ");
			buf.append(SQLiteDataBaseAccess.DB_TABLE_KB_PROPERTY);
			buf.append(" (kb_key, kb_value)");
			buf.append(" VALUES (");
			buf.append(key).append(", ");
			buf.append(DatabaseUtils.sqlEscapeString(value)).append(")");
		}

		Log.d(getClass().getSimpleName() + " - createKbProperty", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This returns an existing {@link DBSemanticTagModel}
	 *
	 * @param dimension
	 * @param si
	 * @return
	 * @throws SQLException
	 */
	@Override
	public DBSemanticTagModel getSemanticTag(final int dimension, final String... si) throws SQLException {
		if (si == null || si.length == 0) {
			return null;
		}

		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT st.dimension, st._id as stId, properties FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_SI).append(" as si, ").append(SQLiteDataBaseAccess.DB_TABLE_ST).append(" as st");
		buf.append(" WHERE");

		for (int i = 0; i < si.length; ++i) {
			if (i != 0) {
				buf.append(" OR");
			}

			buf.append(" si.uri = ").append(DatabaseUtils.sqlEscapeString(si[i]));
		}

		buf.append(" AND st.dimension = ").append(dimension);
		buf.append(" AND st._id = si.st_id");

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();

					final DBSemanticTagModel model = new DBSemanticTagModel();
					model.setDimension(dimension);
					model.setProperty(cursor.getString(cursor.getColumnIndexOrThrow("properties")));
					model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("stId")));
					model.addSubjectIdentifier(getSubjectIdentifierModels(model.getId()));

					return model;
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}

		return null;
	}

	// ========================================================================

	//	/**
	//	 * This returns first matched {@link DBSemanticTagModel} by given semantic indentifiers
	//	 *
	//	 * @param dimension
	//	 * @param si
	//	 * @return
	//	 */
	//	@Override
	//	public DBSemanticTagModel getSemanticTagModel(final int dimension, final String... si) {
	//		final StringBuffer buf = new StringBuffer();
	//		buf.append("SELECT * FROM ");
	//		buf.append(SQLiteDataBaseAccess.DB_TABLE_SI).append(" as si, ").append(SQLiteDataBaseAccess.DB_TABLE_ST).append(" as st");
	//		buf.append(" WHERE");
	//
	//		for (int i = 0; i < si.length; ++i) {
	//			if (i != 0) {
	//				buf.append(" OR");
	//			}
	//
	//			buf.append(" si.uri = ").append(APOSTROPHE).append(si[i]).append(APOSTROPHE);
	//		}
	//
	//		try {
	//			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());
	//
	//			try {
	//				if (cursor.getCount() > 0) {
	//					cursor.moveToFirst();
	//
	//					final DBSemanticTagModel model = new DBSemanticTagModel();
	//					model.setDimension(cursor.getInt(cursor.getColumnIndexOrThrow("dimension")));
	//					model.setProperty(cursor.getString(cursor.getColumnIndexOrThrow("properties")));
	//					model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("st._id")));
	//					model.addSubjectIdentifier(getSubjectIdentifierModels(model.getId()));
	//
	//					return model;
	//				}
	//			}
	//			finally {
	//				cursor.close();
	//			}
	//		}
	//		catch (final Exception e) {
	//			throw new SharkSQLException(e);
	//		}
	//
	//		return null;
	//	}

	// ========================================================================

	/**
	 * This return a list of {@link DBSemanticTagModel} of a dimension
	 *
	 * @param dimension
	 *          - use constants
	 * @return
	 */
	@Override
	public List<DBSemanticTagModel> getSemanticTag(final int dimension) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT * FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST);
		buf.append(" WHERE dimension = ").append(dimension);

		Log.d(getClass().getSimpleName() + " - getSemanticTagModel", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());
			final List<DBSemanticTagModel> modelList = new ArrayList<DBSemanticTagModel>(cursor.getCount());

			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();

					while (!cursor.isAfterLast()) {

						final DBSemanticTagModel model = new DBSemanticTagModel();
						model.setDimension(cursor.getInt(cursor.getColumnIndexOrThrow("dimension")));
						model.setProperty(cursor.getString(cursor.getColumnIndexOrThrow("properties")));
						model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("st._id")));
						model.addSubjectIdentifier(getSubjectIdentifierModels(model.getId()));

						modelList.add(model);

						cursor.moveToNext();
					}
				}

				return modelList;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This return a {@link DBSemanticTagModel} by id
	 *
	 * @param id
	 * @return
	 * @throws SharkSQLException
	 */
	@Override
	public DBSemanticTagModel getSemanticTagById(final int id) throws SharkSQLException {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT * FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST);
		buf.append(" WHERE _id = ").append(id);

		Log.d(getClass().getSimpleName() + " - getSemanticTagModelById", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();

					final DBSemanticTagModel model = new DBSemanticTagModel();
					model.setDimension(cursor.getInt(cursor.getColumnIndexOrThrow("dimension")));
					model.setProperty(cursor.getString(cursor.getColumnIndexOrThrow("properties")));
					model.setId(id);
					model.addSubjectIdentifier(getSubjectIdentifierModels(id));

					return model;
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}

		return null;
	}

	// ========================================================================

	/**
	 * This returns a list of subject identifiers of a dimension
	 *
	 * @param dimension
	 *          - use constants
	 * @return
	 */
	@Override
	public List<String> getSubjectIdentifier(final int dimension) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT si.uri FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_SI).append(" as si, ").append(SQLiteDataBaseAccess.DB_TABLE_ST).append(" as st");
		buf.append(" WHERE st.dimension = ").append(dimension);
		buf.append(" AND st._id = si.st_id");

		Log.d(getClass().getSimpleName() + " - getSubjectIdentifier", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());
			final List<String> siList = new ArrayList<String>(cursor.getCount());

			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();

					while (!cursor.isAfterLast()) {
						siList.add(cursor.getString(cursor.getColumnIndexOrThrow("uri")));
					}

					cursor.moveToNext();
				}

				return siList;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This returns a {@link DBSubjectIdentifierModel} by semantic tag id and the uri
	 *
	 * @param stId
	 * @param uri
	 *
	 * @return
	 */
	private DBSubjectIdentifierModel getSubjectIdentifierModel(final int stId, final String uri) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT _id FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_SI);
		buf.append(" WHERE uri = ").append(DatabaseUtils.sqlEscapeString(uri));
		buf.append(" AND st_id = ").append(stId);

		Log.d(getClass().getSimpleName() + " - getSemanticIdentifierModel", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				if (cursor.getCount() == 1) {
					cursor.moveToFirst();

					final DBSubjectIdentifierModel model = new DBSubjectIdentifierModel();
					model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
					model.setStId(stId);
					model.setUri(uri);

					return model;
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}

		return null;
	}

	// ========================================================================

	/**
	 * This returns a lsit of {@link DBSubjectIdentifierModel} by semantic tag id
	 *
	 * @param stId
	 * @param uri
	 *
	 * @return
	 */
	private List<DBSubjectIdentifierModel> getSubjectIdentifierModels(final int stId) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT _id, uri FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_SI);
		buf.append(" WHERE st_id = ").append(stId);

		Log.d(getClass().getSimpleName() + " - getSemanticIdentifierModels", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());
			final List<DBSubjectIdentifierModel> subjectList = new ArrayList<DBSubjectIdentifierModel>();

			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();

					while (!cursor.isAfterLast()) {

						final DBSubjectIdentifierModel model = new DBSubjectIdentifierModel();
						model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
						model.setUri(cursor.getString(cursor.getColumnIndexOrThrow("uri")));
						model.setStId(stId);

						subjectList.add(model);
						
						cursor.moveToNext();
					}
				}

				return subjectList;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This returns the information data as byte[] by information id
	 *
	 * @param id
	 * @return
	 */
	@Override
	public byte[] getInformationContent(final int id) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT data FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_INFO);
		buf.append(" WHERE _id = ").append(id);

		Log.d(getClass().getSimpleName() + " - getInformationContent", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				if (cursor.getCount() == 1) {
					cursor.moveToFirst();

					return cursor.getBlob(cursor.getColumnIndexOrThrow("data"));
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}

		return null;
	}

	// ========================================================================

	/**
	 * This returns an array of information models without data!!!
	 *
	 * @param cpId
	 *          - id of context point
	 * @return
	 */
	@Override
	public DBInformationModel[] getInformation(final int cpId) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT _id, properties FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_INFO);
		buf.append(" WHERE _id = ").append(cpId);

		Log.d(getClass().getSimpleName() + " - getInformationContent", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());
			final DBInformationModel[] models = new DBInformationModel[cursor.getCount()];
			int i = -1;

			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();

					while (!cursor.isAfterLast()) {
						final DBInformationModel model = new DBInformationModel();
						model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
						model.setProperty(cursor.getString(cursor.getColumnIndexOrThrow("properties")));
						model.setContextPointId(cpId);

						i++;
						models[i] = model;
						cursor.moveToNext();
					}
				}

				return models;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This returns the {@link DBContextPointModel} by {@link ContextCoordinates}
	 *
	 * @param contextCoordinates
	 * @return
	 */
	@Override
	public DBContextPointModel getContextPoint(final ContextCoordinates contextCoordinates) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT * FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_CP);
		buf.append(" WHERE ");

		final DBSemanticTagModel[] semanticTagModels = new DBSemanticTagModel[ContextSpace.MAXDIMENSIONS];
		semanticTagModels[ContextSpace.DIM_DIRECTION] = getSemanticTag(ContextSpace.DIM_DIRECTION,
				contextCoordinates.getSI(ContextSpace.DIM_DIRECTION));
		semanticTagModels[ContextSpace.DIM_LOCATION] = getSemanticTag(ContextSpace.DIM_LOCATION,
				contextCoordinates.getSI(ContextSpace.DIM_LOCATION));
		semanticTagModels[ContextSpace.DIM_ORIGINATOR] = getSemanticTag(ContextSpace.DIM_ORIGINATOR,
				contextCoordinates.getSI(ContextSpace.DIM_ORIGINATOR));
		semanticTagModels[ContextSpace.DIM_PEER] = getSemanticTag(ContextSpace.DIM_PEER, contextCoordinates.getSI(ContextSpace.DIM_PEER));
		semanticTagModels[ContextSpace.DIM_REMOTEPEER] = getSemanticTag(ContextSpace.DIM_REMOTEPEER,
				contextCoordinates.getSI(ContextSpace.DIM_REMOTEPEER));
		semanticTagModels[ContextSpace.DIM_TIME] = getSemanticTag(ContextSpace.DIM_TIME, contextCoordinates.getSI(ContextSpace.DIM_TIME));
		semanticTagModels[ContextSpace.DIM_TOPIC] = getSemanticTag(ContextSpace.DIM_TOPIC, contextCoordinates.getSI(ContextSpace.DIM_TOPIC));
		boolean moreTagsAvailable = false;

		if (semanticTagModels[ContextSpace.DIM_DIRECTION] != null) {
			buf.append(" io = ").append(semanticTagModels[ContextSpace.DIM_DIRECTION]);
			moreTagsAvailable = true;
		}
		else if (semanticTagModels[ContextSpace.DIM_LOCATION] != null) {
			if (moreTagsAvailable) {
				buf.append(" AND");
			}

			buf.append(" location = ").append(semanticTagModels[ContextSpace.DIM_LOCATION].getId());
			moreTagsAvailable = true;
		}
		else if (semanticTagModels[ContextSpace.DIM_ORIGINATOR] != null) {
			if (moreTagsAvailable) {
				buf.append(" AND");
			}

			buf.append(" originator = ").append(semanticTagModels[ContextSpace.DIM_ORIGINATOR].getId());
			moreTagsAvailable = true;
		}
		else if (semanticTagModels[ContextSpace.DIM_PEER] != null) {
			if (moreTagsAvailable) {
				buf.append(" AND");
			}

			buf.append(" peer = ").append(semanticTagModels[ContextSpace.DIM_PEER].getId());
			moreTagsAvailable = true;
		}
		else if (semanticTagModels[ContextSpace.DIM_REMOTEPEER] != null) {
			if (moreTagsAvailable) {
				buf.append(" AND");
			}

			buf.append(" remote = ").append(semanticTagModels[ContextSpace.DIM_REMOTEPEER].getId());
			moreTagsAvailable = true;
		}
		else if (semanticTagModels[ContextSpace.DIM_TIME] != null) {
			if (moreTagsAvailable) {
				buf.append(" AND");
			}

			buf.append(" time = ").append(semanticTagModels[ContextSpace.DIM_TIME].getId());
			moreTagsAvailable = true;
		}
		else if (semanticTagModels[ContextSpace.DIM_TOPIC] != null) {
			if (moreTagsAvailable) {
				buf.append(" AND");
			}

			buf.append(" topic = ").append(semanticTagModels[ContextSpace.DIM_TOPIC].getId());
			moreTagsAvailable = true;
		}

		Log.d(getClass().getSimpleName() + " - getContextPoint", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				if (cursor.getCount() == 1) {
					cursor.moveToNext();

					final DBContextPointModel model = new DBContextPointModel();
					model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
					model.setIo(semanticTagModels[ContextSpace.DIM_DIRECTION]);
					model.setLocation(semanticTagModels[ContextSpace.DIM_LOCATION]);
					model.setOriginator(semanticTagModels[ContextSpace.DIM_ORIGINATOR]);
					model.setPeer(semanticTagModels[ContextSpace.DIM_PEER]);
					model.setRemote(semanticTagModels[ContextSpace.DIM_REMOTEPEER]);
					model.setTime(semanticTagModels[ContextSpace.DIM_TIME]);
					model.setTopic(semanticTagModels[ContextSpace.DIM_TOPIC]);
					model.setProperties(cursor.getString(cursor.getColumnIndexOrThrow("properties")));
					model.setInformation(getInformation(model.getId()));

					return model;
				}

				return null;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This returns the {@link DBContextPointModel} by cp id
	 *
	 * @param id
	 * @return
	 */
	@Override
	public DBContextPointModel getContextPoint(final int id) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT * FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_CP);
		buf.append(" WHERE _id = ").append(id);

		Log.d(getClass().getSimpleName() + " - getContextPoint", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				if (cursor.getCount() == 1) {
					cursor.moveToNext();

					final DBContextPointModel model = new DBContextPointModel();
					model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
					model.setProperties(cursor.getString(cursor.getColumnIndexOrThrow("properties")));
					model.setInformation(getInformation(model.getId()));

					final int ioId = cursor.getInt(cursor.getColumnIndexOrThrow("io"));
					final int locationId = cursor.getInt(cursor.getColumnIndexOrThrow("location"));
					final int originatorId = cursor.getInt(cursor.getColumnIndexOrThrow("originator"));
					final int peerId = cursor.getInt(cursor.getColumnIndexOrThrow("peer"));
					final int remoteId = cursor.getInt(cursor.getColumnIndexOrThrow("remote"));
					final int timeId = cursor.getInt(cursor.getColumnIndexOrThrow("time"));
					final int topicId = cursor.getInt(cursor.getColumnIndexOrThrow("topic"));

					model.setIo(ioId > 0 ? getSemanticTagById(ioId) : null);
					model.setLocation(locationId > 0 ? getSemanticTagById(locationId) : null);
					model.setOriginator(originatorId > 0 ? getSemanticTagById(originatorId) : null);
					model.setPeer(peerId > 0 ? getSemanticTagById(peerId) : null);
					model.setRemote(remoteId > 0 ? getSemanticTagById(remoteId) : null);
					model.setTime(timeId > 0 ? getSemanticTagById(timeId) : null);
					model.setTopic(topicId > 0 ? getSemanticTagById(topicId) : null);

					return model;
				}

				return null;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This returns a list of {@link DBSemanticTagModel} predicates by {@link DBSemanticTagModel}
	 *
	 * @param semanticTagModel
	 *          - as subject
	 * @return
	 */
	@Override
	public List<String> getSemanticTagRelationPredicates(final DBSemanticTagModel semanticTagModel) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT DISTINCT predicate FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST_REL);
		buf.append(" WHERE subject = ").append(semanticTagModel.getId());

		Log.d(getClass().getSimpleName() + " - getSemanticTagModelRelationPredicates", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());
			final List<String> predicateList = new ArrayList<String>(cursor.getCount());

			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();

					while (!cursor.isAfterLast()) {
						predicateList.add(cursor.getString(cursor.getColumnIndexOrThrow("predicate")));

						cursor.moveToNext();
					}
				}

				return predicateList;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This returns a list of {@link DBSemanticTagModel}-objects by {@link DBSemanticTagModel} as subject and predicate
	 *
	 * @param semanticTagModel
	 *          - subject
	 * @param predicate
	 * @return
	 */
	@Override
	public List<DBSemanticTagModel> getSemanticTagRelationObjects(final DBSemanticTagModel semanticTagModel, final String predicate) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT DISTINCT object FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST_REL);
		buf.append(" WHERE subject = ").append(semanticTagModel.getId());
		buf.append(" AND predicate = ").append(DatabaseUtils.sqlEscapeString(predicate));

		Log.d(getClass().getSimpleName() + " - getSemanticTagModelRelationObjects", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());
			final List<DBSemanticTagModel> objectList = new ArrayList<DBSemanticTagModel>(cursor.getCount());

			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();

					while (!cursor.isAfterLast()) {
						final int objectId = cursor.getInt(cursor.getColumnIndexOrThrow("object"));
						final DBSemanticTagModel object = getSemanticTagById(objectId);

						if (object != null) {
							objectList.add(object);
						}

						cursor.moveToNext();
					}
				}

				return objectList;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This returns a {@link DBSemanticTagModel} by {@link DBSemanticTagModel} as subject, {@link DBSemanticTagModel} as object and predicate
	 *
	 * @param subject
	 * @param object
	 * @param predicate
	 * @return
	 */
	public DBSemanticTagRelationModel getSemanticTagRelation(final DBSemanticTagModel subject, final DBSemanticTagModel object,
			final String predicate) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT * FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST_REL);
		buf.append(" WHERE subject = ").append(subject.getId());
		buf.append(" AND object = ").append(object.getId());
		buf.append(" AND predicate = ").append(DatabaseUtils.sqlEscapeString(predicate));

		Log.d(getClass().getSimpleName() + " - getInformationContent", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				if (cursor.getCount() == 1) {
					cursor.moveToFirst();

					final DBSemanticTagRelationModel model = new DBSemanticTagRelationModel();
					model.setObject(object);
					model.setSubject(subject);
					model.setPredicate(predicate);

					return model;
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}

		return null;
	}

	// ========================================================================

	/**
	 * This returns the value of knowledgebase property by key. Value is null if key does not exist
	 *
	 * @param key
	 *          - use constants
	 * @return value or null if key does not exist
	 * @throws SharkSQLException
	 */
	@Override
	public String getKbProperty(final int key) throws SharkSQLException {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT kb_value FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_KB_PROPERTY);
		buf.append(" WHERE kb_key = ").append(key);

		Log.d(getClass().getSimpleName() + " - getKbProperty", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				if (cursor.getCount() == 1) {
					cursor.moveToFirst();

					return cursor.getString(cursor.getColumnIndexOrThrow("kb_value"));
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}

		return null;
	}

	// ========================================================================

	private boolean isReferenceExists(final DBSemanticTagModel tag) {
		final StringBuffer buf = new StringBuffer();
		buf.append("SELECT * FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_CP);

		if (tag.getDimension() == ContextSpace.DIM_DIRECTION) {
			buf.append(" WHERE io = ").append(tag.getId());
		}
		else if (tag.getDimension() == ContextSpace.DIM_LOCATION) {
			buf.append(" WHERE location = ").append(tag.getId());
		}
		else if (tag.getDimension() == ContextSpace.DIM_ORIGINATOR) {
			buf.append(" WHERE originator = ").append(tag.getId());
		}
		else if (tag.getDimension() == ContextSpace.DIM_PEER) {
			buf.append(" WHERE peer = ").append(tag.getId());
		}
		else if (tag.getDimension() == ContextSpace.DIM_REMOTEPEER) {
			buf.append(" WHERE remote = ").append(tag.getId());
		}
		else if (tag.getDimension() == ContextSpace.DIM_TIME) {
			buf.append(" WHERE time = ").append(tag.getId());
		}
		else if (tag.getDimension() == ContextSpace.DIM_TOPIC) {
			buf.append(" WHERE topic = ").append(tag.getId());
		}

		Log.d(getClass().getSimpleName() + " - isReferenceExists", buf.toString());

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(buf.toString());

			try {
				return cursor.getCount() > 0;
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This deletes the {@link DBContextPointModel} with {@link ContextCoordinates} and returns true if the deleting was successful otherwise
	 * false
	 *
	 * @param contextCoordinates
	 * @return true if the creating was successful otherwise false
	 */
	@Override
	public boolean deleteContextPoint(final ContextCoordinates contextCoordinates) {
		final DBContextPointModel model = getContextPoint(contextCoordinates);

		return model == null ? false : deleteContextPoint(model.getId());
	}

	// ========================================================================

	/**
	 * This deletes the {@link DBContextPointModel} by id and returns true if the creating was successful otherwise false
	 *
	 * @param id
	 * @return true if the creating was successful otherwise false
	 */
	@Override
	public boolean deleteContextPoint(final int id) {
		final StringBuffer buf = new StringBuffer();
		buf.append("DELETE FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_CP);
		buf.append(" WHERE _id = ").append(id);

		Log.d(getClass().getSimpleName() + " - deleteContextPoint", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			deleteInformation(id);

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This deletes all information of context point by context point id
	 *
	 * @param cpId
	 * @return
	 */
	private boolean deleteInformation(final int cpId) {
		final StringBuffer buf = new StringBuffer();
		buf.append("DELETE FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_INFO);
		buf.append(" WHERE cp_id = ").append(cpId);

		Log.d(getClass().getSimpleName() + " - deleteInformation", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This deletes {@link DBSemanticTagModel} and all subject identifiers which belongs to this {@link DBSemanticTagModel}
	 *
	 * @param model
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 * @throws SharkSQLReferenceExistsException
	 */
	@Override
	public boolean deleteSemanticTag(final DBSemanticTagModel model) throws SharkSQLException, SharkSQLReferenceExistsException {

		if (isReferenceExists(model)) {
			throw new SharkSQLReferenceExistsException();
		}

		/////////////////////////////
		// deletes si at first
		for (final DBSubjectIdentifierModel si : model.getSubjectIdentifierList()) {
			deleteSubjectIdentifier(si);
		}

		////////////////////////////
		// deletes all relations
		deleteSemanticTagRelation(model);

		final StringBuffer buf = new StringBuffer();
		buf.append("DELETE FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST);
		buf.append(" WHERE _id = ").append(model.getId());

		Log.d(getClass().getSimpleName() + " - deleteSemanticTagModel", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This removes {@link DBSemanticTagModel} from context point.
	 *
	 * @param tag
	 * @param cp
	 *
	 * @return
	 * @throws SharkSQLException
	 */
	@Override
	public boolean removeSemanticTagFromContextPoint(final DBSemanticTagModel tag, final DBContextPointModel cp) throws SharkSQLException {
		final StringBuffer buf = new StringBuffer();
		buf.append("UPDATE ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_CP);

		if (tag.getDimension() == ContextSpace.DIM_DIRECTION) {
			buf.append(" SET io = -1");
		}
		else if (tag.getDimension() == ContextSpace.DIM_LOCATION) {
			buf.append(" SET location = -1");
		}
		else if (tag.getDimension() == ContextSpace.DIM_ORIGINATOR) {
			buf.append(" SET originator = -1");
		}
		else if (tag.getDimension() == ContextSpace.DIM_PEER) {
			buf.append(" SET peer = -1");
		}
		else if (tag.getDimension() == ContextSpace.DIM_REMOTEPEER) {
			buf.append(" SET remote = -1");
		}
		else if (tag.getDimension() == ContextSpace.DIM_TIME) {
			buf.append(" SET time = -1");
		}
		else if (tag.getDimension() == ContextSpace.DIM_TOPIC) {
			buf.append(" SET topic = -1");
		}
		buf.append(" WHERE _id = ").append(cp.getId());

		Log.d(getClass().getSimpleName() + " - removeSemanticTagModelFromContextPoint", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	//	/**
	//	 * This deletes {@link DBSemanticTagModel} by id and returns true if the creating was successful otherwise false
	//	 *
	//	 * @param id
	//	 * @return
	//	 */
	//	@Override
	//	public boolean deleteSemanticTagModel(final int id) {
	//		return false;
	//	}

	// ========================================================================

	/**
	 * This deletes {@link DBSubjectIdentifierModel} and returns true if the deleting was successful otherwise false
	 *
	 * @param si
	 * @return
	 * @throws SharkSQLException
	 */
	@Override
	public boolean deleteSubjectIdentifier(final DBSubjectIdentifierModel si) throws SharkSQLException {
		final StringBuffer buf = new StringBuffer();
		buf.append("DELETE FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_SI);
		buf.append(" WHERE _id = ").append(si.getId());

		Log.d(getClass().getSimpleName() + " - deleteSubjectIdentifier", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This deletes {@link DBSemanticTagModel}-Relation by subject, object and predicate
	 *
	 * @param relation
	 * @return
	 */
	@Override
	public boolean deleteSemanticTagRelation(final DBSemanticTagRelationModel relation) {
		final StringBuffer buf = new StringBuffer();
		buf.append("DELETE FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST_REL);
		buf.append(" WHERE subject = ").append(relation.getSubject().getId());
		buf.append(" AND object = ").append(relation.getObject().getId());
		buf.append(" AND predicate = ").append(DatabaseUtils.sqlEscapeString(relation.getPredicate()));

		Log.d(getClass().getSimpleName() + " - deleteSemanticTagModelRelation", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This deletes {@link DBSemanticTagModel}-Relation by {@link DBSemanticTagModel} as subject or object
	 *
	 * @param model
	 * @return
	 */
	private boolean deleteSemanticTagRelation(final DBSemanticTagModel model) {
		final StringBuffer buf = new StringBuffer();
		buf.append("DELETE FROM ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST_REL);
		buf.append(" WHERE subject = ").append(model.getId());
		buf.append(" OR object = ").append(model.getId());

		Log.d(getClass().getSimpleName() + " - deleteSemanticTagRelation", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This deletes {@link DBSemanticTagModel} by given {@link DBSemanticTagModel} and returns true if the creating was successful otherwise
	 * false
	 *
	 * @param semanticTagModel
	 * @return true if the creating was successful otherwise false
	 */
	@Override
	public boolean updateDBSemanticTag(final DBSemanticTagModel semanticTagModel) {
		final StringBuffer buf = new StringBuffer();
		buf.append("UPDATE ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_ST);
		buf.append(" SET properties = ").append(DatabaseUtils.sqlEscapeString(semanticTagModel.getProperties()));
		buf.append(" WHERE _id = ").append(semanticTagModel.getId());

		Log.d(getClass().getSimpleName() + " - updateDBSemanticTagModel", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

	// ========================================================================

	/**
	 * This only updates properties of {@link DBContextPointModel} and returns true if the creating was successful otherwise false
	 *
	 * @param contextPointModel
	 * @return true if the creating was successful otherwise false
	 * @throws SharkSQLException
	 */
	public boolean updateContextPoint(final DBContextPointModel contextPointModel) throws SharkSQLException {
		final StringBuffer buf = new StringBuffer();
		buf.append("UPDATE ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_CP);
		buf.append(" SET properties = ").append(DatabaseUtils.sqlEscapeString(contextPointModel.getProperties()));
		buf.append(" WHERE _id = ").append(contextPointModel.getId());

		Log.d(getClass().getSimpleName() + " - updateContextPoint", buf.toString());

		try {
			SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

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
	public boolean updateInformation(final DBInformationModel informationModel, final boolean updateData) {
		final StringBuffer buf = new StringBuffer();
		buf.append("UPDATE ");
		buf.append(SQLiteDataBaseAccess.DB_TABLE_INFO);
		buf.append(" SET properties = ").append(DatabaseUtils.sqlEscapeString(informationModel.getProperty()));
		if(updateData) {
			buf.append(", data = ?");
		}
		buf.append(" WHERE _id = ").append(informationModel.getId());

		Log.d(getClass().getSimpleName() + " - updateInformation", buf.toString());

		try {
			if(updateData) {
				SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString(), informationModel.getData());
			}
			else {
				SQLiteDataBaseAccess.getInstance().executeStatement(buf.toString());
			}

			return true;
		}
		catch (final Exception e) {
			throw new SharkSQLException(e);
		}
	}

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
	@Override
	public int getLastId(final String table) throws SharkSQLException {
		final String stmt = "SELECT _id FROM " + table + " ORDER BY _id DESC LIMIT 1";

		try {
			final Cursor cursor = SQLiteDataBaseAccess.getInstance().queryStatement(stmt);

			try {
				if (cursor.getCount() == 1) {
					cursor.moveToFirst();

					return cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				}
			}
			finally {
				cursor.close();
			}
		}
		catch (final Exception e) {
			throw e instanceof SharkSQLException ? (SharkSQLException) e : new SharkSQLException(e);
		}

		return -1;
	}
}
