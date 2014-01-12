/**
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 */

package net.sharkfw.knowledgeBase.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author pmj
 *
 */
public class SQLiteDataBaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "shark.db";
	private static final int DATABASE_VERSION = 1;

	public SQLiteDataBaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// ========================================================================

	@Override
	public void onCreate(final SQLiteDatabase db) {
	}

	// ========================================================================

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
	}

}
