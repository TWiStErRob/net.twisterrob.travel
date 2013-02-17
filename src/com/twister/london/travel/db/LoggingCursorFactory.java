package com.twister.london.travel.db;

import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.twister.android.utils.log.*;

public final class LoggingCursorFactory implements CursorFactory {
	private static final Log LOG = LogFactory.getLog(Tag.DB);

	public Cursor newCursor(final SQLiteDatabase db, final SQLiteCursorDriver masterQuery, final String editTable,
			final SQLiteQuery query) {
		LoggingCursorFactory.LOG.verbose(query.toString());
		return new SQLiteCursor(masterQuery, editTable, query);
	}
}
