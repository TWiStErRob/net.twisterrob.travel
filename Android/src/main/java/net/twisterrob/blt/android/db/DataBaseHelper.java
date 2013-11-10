package net.twisterrob.blt.android.db;

import java.util.*;

import net.twisterrob.blt.android.db.model.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
	private Context m_context;

	public DataBaseHelper(final Context context) {
		m_context = context;
		m_helper = new DataBaseOpenHelper(context);
		m_reader = new DataBaseReader(this);
		m_writer = new DataBaseWriter(this);
	}

	public Context getContext() {
		return m_context;
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
	}

	public List<Station> getStations() {
		return m_reader.getStations();
	}

	public void updateStations(Iterable<Station> stations) {
		m_writer.updateStations(stations);
	}

	public void updateTypes(Map<String, String> styles) {
		m_writer.updateTypes(styles);
	}

	public Station getStation(String name) {
		return m_reader.getStation(name);
	}

	public Map<String, List<AreaHullPoint>> getAreas() {
		return m_reader.getAreas();
	}

	public Set<NetworkNode> getTubeNetwork() {
		return m_reader.getTubeNetwork();
	}
}
