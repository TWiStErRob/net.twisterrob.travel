package net.twisterrob.blt.android.db;

import java.util.*;

import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.Location;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;

@SuppressWarnings("resource")
// TODO fix resource leaks
class DataBaseReader {
	private static final String LAST_UPDATE = "strftime('%s', __last_update) * 1000";

	private static final String[] STATION_DETAILS = {LAST_UPDATE, "_id", "name", "type", "address", "telephone",
			"latitude", "longitude"};
	private static final String[] TYPE_DETAILS = {LAST_UPDATE, "_id", "name", "url"};

	private final DataBaseHelper m_dataBaseHelper;

	DataBaseReader(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	// #region Model::Stations

	public List<Station> getStations() {
		List<Station> stations = new ArrayList<Station>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("Station", STATION_DETAILS, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Station station = readStation(cursor);
			stations.add(station);
		}
		cursor.close();
		return stations;
	}

	private static Station readStation(final Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		Type type = Type.get(cursor.getString(cursor.getColumnIndex("type")));
		String address = cursor.getString(cursor.getColumnIndex("address"));
		String telephone = cursor.getString(cursor.getColumnIndex("telephone"));
		double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
		double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

		Station station = new Station();
		station.setId(id);
		station.setName(name);
		station.setType(type);
		station.setAddress(address);
		station.setTelephone(telephone);
		station.setLocation(new Location(latitude, longitude));
		return station;
	}

	public int getNumberOfStations() {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		int entries = (int)DatabaseUtils.queryNumEntries(db, "Station");
		return entries;
	}

	public Station getStation(final int stationId) {
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("Station", STATION_DETAILS, "_id = ?", new String[]{String.valueOf(stationId)},
				null, null, null);
		Station station = null;
		if (cursor.moveToNext()) {
			station = readStation(cursor);
		}
		cursor.close();
		return station;
	}

	public Map<String, String> getTypes() {
		Map<String, String> types = new HashMap<String, String>();
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query("StationType", TYPE_DETAILS, null, null, null, null, null);
		while (cursor.moveToNext()) {
			@SuppressWarnings("unused")
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String url = cursor.getString(cursor.getColumnIndex("url"));
			types.put(name, url);
		}
		cursor.close();
		return types;
	}

	// #endregion
}
