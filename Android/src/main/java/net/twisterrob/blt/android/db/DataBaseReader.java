package net.twisterrob.blt.android.db;

import java.util.*;

import net.twisterrob.android.utils.tools.IOTools;
import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.Location;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;

@SuppressWarnings("resource")
// TODO fix resource leaks
class DataBaseReader {
	// private static final String LAST_UPDATE = "strftime('%s', __last_update) * 1000";

	private static final String[] STATION_DETAILS = {"_id", "name", "type", "address", "telephone", "latitude",
			"longitude"};
	private static final String[] TYPE_DETAILS = {"_id", "name", "url"};

	private final DataBaseHelper m_dataBaseHelper;

	DataBaseReader(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	// #region Model::Stations

	public List<Station> getStations() {
		List<Station> stations = new ArrayList<Station>();
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = db.query("Stop", STATION_DETAILS, null, null, null, null, "name ASC");
		Map<Integer, List<Line>> stopLines = getLines();
		while (cursor.moveToNext()) {
			Station station = readStation(cursor, false);
			station.setLines(stopLines.get(station.getId()));
			stations.add(station);
		}
		cursor.close();
		return stations;
	}

	private Station readStation(final Cursor cursor, boolean readLines) {
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		StopType type = StopType.values()[cursor.getInt(cursor.getColumnIndex("type"))];
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

		if (readLines) {
			Map<Line, String> codes = getCodes(station.getId());
			station.setLines(new ArrayList<Line>(codes.keySet()));
			station.setTrackerNetCodes(codes);
		}
		return station;
	}

	public int getNumberOfStations() {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		int entries = (int)DatabaseUtils.queryNumEntries(db, "Stop");
		return entries;
	}

	public Station getStation(final int stationId) {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = db.query("Stop", STATION_DETAILS, "_id = ?", new String[]{String.valueOf(stationId)}, null,
				null, null);
		Station station = null;
		if (cursor.moveToNext()) {
			station = readStation(cursor, true);
		}
		cursor.close();
		return station;
	}

	public Station getStation(String name) {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = db.query("Stop", STATION_DETAILS, "name = ?", new String[]{String.valueOf(name)}, null, null,
				null);
		Station station = null;
		if (cursor.moveToNext()) {
			station = readStation(cursor, true);
		}
		cursor.close();
		return station;
	}

	@SuppressWarnings("unused")
	private Map<String, String> getTypes() {
		Map<String, String> types = new HashMap<String, String>();
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = db.query("StationType", TYPE_DETAILS, null, null, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String url = cursor.getString(cursor.getColumnIndex("url"));
			types.put(name, url);
		}
		cursor.close();
		return types;
	}

	public Map<Integer, List<Line>> getLines() {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select ls.stop as stopID, l.name as lineName from line_stop ls join line l on ls.line = l._id;",
				new String[0]);
		Map<Integer, List<Line>> stopLines = new TreeMap<Integer, List<Line>>();
		while (cursor.moveToNext()) {
			int stopID = cursor.getInt(cursor.getColumnIndex("stopID"));
			String lineString = cursor.getString(cursor.getColumnIndex("lineName"));
			Line line = Line.valueOf(lineString);
			List<Line> lines = stopLines.get(stopID);
			if (lines == null) {
				lines = new LinkedList<Line>();
				stopLines.put(stopID, lines);
			}
			lines.add(line);
		}
		cursor.close();
		return stopLines;
	}

	public Map<Line, String> getCodes(int stopID) {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select ls.line as lineID, ls.code as code from line_stop ls where ls.stop = ?;",
				new String[]{String.valueOf(stopID)});
		Map<Line, String> lines = new EnumMap<Line, String>(Line.class);
		while (cursor.moveToNext()) {
			int lineID = cursor.getInt(cursor.getColumnIndex("lineID"));
			String code = cursor.getString(cursor.getColumnIndex("code"));
			Line line = Line.values()[lineID];
			lines.put(line, code);
		}
		cursor.close();
		return lines;
	}

	public Map<String, List<AreaHullPoint>> getAreas() {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select area_code, latitude, longitude from AreaHull order by area_code, hull_index;", new String[0]);
		Map<String, List<AreaHullPoint>> areas = new TreeMap<String, List<AreaHullPoint>>();
		while (cursor.moveToNext()) {
			String area = cursor.getString(cursor.getColumnIndex("area_code"));
			double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
			double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
			List<AreaHullPoint> list = areas.get(area);
			if (list == null) {
				list = new ArrayList<AreaHullPoint>();
				areas.put(area, list);
			}
			list.add(new AreaHullPoint(latitude, longitude));
		}
		cursor.close();
		return areas;
	}
	// #endregion

	public Map<Integer, NetworkNode> getTubeNetwork() {
		SQLiteDatabase db = m_dataBaseHelper.getReadableDatabase();
		String query = IOTools.getAssetAsString(m_dataBaseHelper.getContext(), "getNetwork.sql");
		Cursor cursor = db.rawQuery(query, new String[0]);
		Map<Integer, NetworkNode> nodes = new TreeMap<Integer, NetworkNode>();
		while (cursor.moveToNext()) {
			int fromID = cursor.getInt(cursor.getColumnIndex("fromID"));
			String fromName = cursor.getString(cursor.getColumnIndex("fromName"));
			double fromLat = cursor.getDouble(cursor.getColumnIndex("fromLat"));
			double fromLon = cursor.getDouble(cursor.getColumnIndex("fromLon"));
			int toID = cursor.getInt(cursor.getColumnIndex("toID"));
			String toName = cursor.getString(cursor.getColumnIndex("toName"));
			double toLat = cursor.getDouble(cursor.getColumnIndex("toLat"));
			double toLon = cursor.getDouble(cursor.getColumnIndex("toLon"));
			int distance = cursor.getInt(cursor.getColumnIndex("distance"));
			String lineString = cursor.getString(cursor.getColumnIndex("line"));
			NetworkNode fromNode = nodes.get(fromID);
			if (fromNode == null) {
				fromNode = new NetworkNode(fromID, fromName, new Location(fromLat, fromLon));
				nodes.put(fromID, fromNode);
			}
			NetworkNode toNode = nodes.get(toID);
			if (toNode == null) {
				toNode = new NetworkNode(toID, toName, new Location(toLat, toLon));
				nodes.put(toID, toNode);
			}
			Line line = Line.valueOf(lineString);
			fromNode.out.add(new NetworkLink(toNode, line, distance));
			toNode.in.add(new NetworkLink(fromNode, line, distance));
		}
		return nodes;
	}
}
