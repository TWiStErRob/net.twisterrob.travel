package net.twisterrob.blt.android.db;

import java.util.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.twisterrob.android.db.DatabaseOpenHelper;
import net.twisterrob.android.utils.tools.DatabaseTools;
import net.twisterrob.blt.android.BuildConfig;
import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.model.*;

/**
 * See details in the docs under <SDK_DIR>/docs/guide/topics/data/data- storage.html and I think you'll find the answer
 * You should implement SQLiteDatabase.CursorFactory interface, and create an SQLiteDatabase instance by calling the
 * static method SQLiteDatabase.openOrCreateDatabase. Details info of SQLiteDatabase.CursorFactory is in <SDK_DIR>/docs/
 * reference/android/m_database/sqlite/SQLiteDatabase.CursorFactory.html
 */
public class DataBaseHelper {
	private final DatabaseOpenHelper m_helper;
	private final DataBaseWriter m_writer;
	private final DataBaseReader m_reader;
	private Context m_context;

	public DataBaseHelper(final Context context) {
		m_context = context;
		m_helper = new DatabaseOpenHelper(context, "LondonTravel", 1, BuildConfig.DEBUG) {
			@Override protected String[] getDataFiles() {
				return new String[] {
						"LondonTravel.data.StopType.sql",
						"LondonTravel.data.Line.sql",
						"LondonTravel.data.Stop.sql",
						"LondonTravel.data.Line_Stop.sql",
						"LondonTravel.data.AreaHull.sql",
						"LondonTravel.data.Route.sql",
						"LondonTravel.data.Section.sql",
						"LondonTravel.data.Route_Section.sql",
						"LondonTravel.data.Link.sql",
						"LondonTravel.data.Section_Link.sql"
				};
			}
			@Override public void onOpen(SQLiteDatabase db) {
				super.onOpen(db);
				verifyEnum(db, StopType.class, "StopType");
				verifyEnum(db, Line.class, "Line");
			}
			private <T extends Enum<T>> void verifyEnum(SQLiteDatabase db, Class<T> enumClass, String enumName) {
				T[] values = enumClass.getEnumConstants();
				Cursor cursor = db.query(enumName, new String[] {"_id", "name"}, null, null, null, null, "_id");
				try {
					int index = 0;
					while (cursor.moveToNext()) {
						if (index == values.length) {
							StringBuilder sb = new StringBuilder(
									"DB has more " + enumName + " values starting at " + index + " :");
							do {
								int _id = DatabaseTools.getInt(cursor, "_id");
								String name = DatabaseTools.getString(cursor, "name");
								sb.append(" ").append(_id).append("/").append(name);
							} while (cursor.moveToNext());
							throw new IllegalStateException(sb.toString());
						}

						int _id = DatabaseTools.getInt(cursor, "_id");
						String name = DatabaseTools.getString(cursor, "name");
						T value = values[index++];
						if (_id != value.ordinal()) {
							throw new IllegalStateException(String.format(Locale.ROOT,
									"Ordinal mismatch between DB %d/%s and %s[%d].%s",
									_id, name, enumName, value.ordinal(), value.name()));
						}
						if (!value.name().equals(name)) {
							throw new IllegalStateException(String.format(Locale.ROOT,
									"Name mismatch between DB %d/%s and %s[%d].%s",
									_id, name, enumName, value.ordinal(), value.name()));
						}
					}
					if (index != values.length) {
						StringBuilder sb = new StringBuilder(
								"Code has more " + enumName + " values starting at " + index + " :");
						do {
							sb.append(" ").append(values[index].ordinal()).append("/").append(values[index].name());
						} while (++index < values.length);
						throw new IllegalStateException(sb.toString());
					}
				} finally {
					cursor.close();
				}
			}
		};
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
