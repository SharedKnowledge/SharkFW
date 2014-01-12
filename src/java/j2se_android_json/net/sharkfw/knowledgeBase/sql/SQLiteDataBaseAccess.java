/**
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 */
package net.sharkfw.knowledgeBase.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

/**
 * @author phl
 *
 */
public class SQLiteDataBaseAccess {
	public final static String DB_TABLE_SI = "Subject_Identifier";
	public final static String DB_TABLE_ST = "Semantic_Tag";
	public final static String DB_TABLE_CP = "Context_Point";
	public final static String DB_TABLE_ST_REL = "ST_Relation";
	public final static String DB_TABLE_KB_PROPERTY = "KB_Properties";
	public final static String DB_TABLE_INFO = "Information";
	private static SQLiteDataBaseAccess databaseAccess;
	private static SQLiteDataBaseHelper dbHelper;

	// ========================================================================

	public static SQLiteDataBaseAccess getInstance() throws SQLException {
		if (databaseAccess == null) {

			if (dbHelper == null) {
				throw new SQLException();
			}

			databaseAccess = new SQLiteDataBaseAccess();
		}

		return databaseAccess;
	}

	// ========================================================================

	public static void setDataBaseContext(final Context context) {
		if (context == null) {
			throw new NullPointerException("Context is null!");
		}

		dbHelper = new SQLiteDataBaseHelper(context);
	}

	// ========================================================================

	public void resetDataBase() {
		executeStatement("DROP TABLE IF EXISTS " + DB_TABLE_CP);
		executeStatement("DROP TABLE IF EXISTS " + DB_TABLE_SI);
		executeStatement("DROP TABLE IF EXISTS " + DB_TABLE_ST);
		executeStatement("DROP TABLE IF EXISTS " + DB_TABLE_ST_REL);
		executeStatement("DROP TABLE IF EXISTS " + DB_TABLE_KB_PROPERTY);
		executeStatement("DROP TABLE IF EXISTS " + DB_TABLE_INFO);

		////////////////
		// Creates Tables
		for (final String stmt : getTablesDefinition()) {
			executeStatement(stmt);
		}

		//		closeDatabase();
	}

	// ========================================================================

	private List<String> getTablesDefinition() {
		final StringBuffer buf = new StringBuffer();
		final List<String> tables = new ArrayList<String>();

		/////////////////////
		// Context Point
		buf.append(" CREATE TABLE " + DB_TABLE_CP);
		buf.append("(_id INTEGER PRIMARY KEY AUTOINCREMENT,");
		buf.append(" properties VARCHAR(100) NOT NULL,");
		buf.append(" remote INTEGER NOT NULL,");
		buf.append(" peer INTEGER NOT NULL,");
		buf.append(" time INTEGER NOT NULL,");
		buf.append(" location INTEGER NOT NULL,");
		buf.append(" originator INTEGER NOT NULL,");
		buf.append(" topic INTEGER NOT NULL,");
		buf.append(" io INTEGER NOT NULL);");

		tables.add(buf.toString());
		buf.setLength(0);

		/////////////////////
		// Subject Identifier
		buf.append("CREATE TABLE " + DB_TABLE_SI);
		buf.append("(_id INTEGER PRIMARY KEY AUTOINCREMENT,");
		buf.append(" st_id INTEGER NOT NULL,");
		buf.append(" uri VARCHAR(300) NOT NULL);");
		//		buf.append(" _main_id INTEGER NOT NULL REFERENCES Log_Main (_id));");

		tables.add(buf.toString());
		buf.setLength(0);

		////////////////////
		// Semantic Tag
		buf.append(" CREATE TABLE " + DB_TABLE_ST);
		buf.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT,");
		buf.append(" properties INTEGER NOT NULL,");
		buf.append(" dimension INTEGER NOT NULL);");

		tables.add(buf.toString());
		buf.setLength(0);

		////////////////////
		// Information
		buf.append(" CREATE TABLE " + DB_TABLE_INFO);
		buf.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT,");
		buf.append(" data BLOB,");
		buf.append(" properties INTEGER NOT NULL,");
		buf.append(" cp_id INTEGER NOT NULL);");

		tables.add(buf.toString());
		buf.setLength(0);

		///////////////////
		// Semantic Tag Relations
		buf.append(" CREATE TABLE " + DB_TABLE_ST_REL);
		buf.append(" (subject INTEGER NOT NULL,");
		buf.append(" object INTEGER NOT NULL,");
		buf.append(" predicate VARCHAR(30) NOT NULL);");

		tables.add(buf.toString());
		buf.setLength(0);

		///////////////////
		// KBProperties
		buf.append(" CREATE TABLE " + DB_TABLE_KB_PROPERTY);
		buf.append(" (kb_key INTEGER NOT NULL,");
		buf.append(" kb_value VARCHAR(300) NOT NULL);");

		tables.add(buf.toString());
		buf.setLength(0);

		return tables;
	}

	// ========================================================================

	public void executeStatement(final String stmt) throws SQLException {
		dbHelper.getWritableDatabase().execSQL(stmt);
	}

	// ========================================================================

	public void executeStatement(final String stmt, final Object... objects) throws SQLException {
		dbHelper.getWritableDatabase().execSQL(stmt, objects);
	}

	// ========================================================================

	public Cursor queryStatement(final String stmt) throws SQLException {
		return dbHelper.getReadableDatabase().rawQuery(stmt, null);
	}

	// ========================================================================

	public void closeDatabase() {
		dbHelper.getWritableDatabase().close();
	}
}
