package net.twisterrob.blt.android.db;

import net.twisterrob.android.utils.log.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public final class LoggingCursorFactory implements CursorFactory {
	private static final Log LOG = LogFactory.getLog(Tag.DB);
	private boolean m_debugQueries = false;

	public LoggingCursorFactory() {}
	public LoggingCursorFactory(boolean debugQueries) {
		m_debugQueries = debugQueries;
	}

	public Cursor newCursor(final SQLiteDatabase db, final SQLiteCursorDriver masterQuery, final String editTable,
			final SQLiteQuery query) {
		if (m_debugQueries) {
			LoggingCursorFactory.LOG.verbose(query.toString());
		}
		return new SQLiteCursor(masterQuery, editTable, query);
	}
}
