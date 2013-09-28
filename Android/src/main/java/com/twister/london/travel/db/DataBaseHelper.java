package com.twister.london.travel.db;

import java.util.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.twister.london.travel.model.*;

/**
 * See details in the docs under <SDK_DIR>/docs/guide/topics/data/data- storage.html and I think you'll find the answer
 * You should implement SQLiteDatabase.CursorFactory interface, and create an SQLiteDatabase instance by calling the
 * static method SQLiteDatabase.openOrCreateDatabase. Details info of SQLiteDatabase.CursorFactory is in <SDK_DIR>/docs/
 * reference/android/m_database/sqlite/SQLiteDatabase.CursorFactory.html
 */
public class DataBaseHelper {
	private final DataBaseOpenHelper m_helper;
	private final DataBaseWriter m_writer;
	private final DataBaseReader m_reader;

	public DataBaseHelper(final Context context) {
		m_helper = new DataBaseOpenHelper(context);
		m_reader = new DataBaseReader(this);
		m_writer = new DataBaseWriter(this);
	}

	SQLiteDatabase getReadableDatabase() {
		return m_helper.getReadableDatabase();
	}

	SQLiteDatabase getWritableDatabase() {
		return m_helper.getWritableDatabase();
	}

	DataBaseReader getReader() {
		return m_reader;
	}

	DataBaseWriter getWriter() {
		return m_writer;
	}

	public void openDB() {
		getWritableDatabase();
		Type.reset(getTypes());
	}

	public List<Station> getStations() {
		return m_reader.getStations();
	}

	public void updateStations(Iterable<Station> stations) {
		m_writer.updateStations(stations);
	}

	public Map<String, String> getTypes() {
		return m_reader.getTypes();
	}

	public void updateTypes(Map<String, String> styles) {
		m_writer.updateTypes(styles);
	}
}
