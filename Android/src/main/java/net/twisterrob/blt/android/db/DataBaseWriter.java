package net.twisterrob.blt.android.db;

import java.util.Map;

import net.twisterrob.android.utils.log.*;
import net.twisterrob.android.utils.tools.DBTools;
import net.twisterrob.blt.model.Station;
import net.twisterrob.java.io.IOTools;
import net.twisterrob.java.model.Location;
import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.*;

@SuppressWarnings("resource")
//TODO fix resource leaks
class DataBaseWriter extends DataBaseAccess {
	private static final Log LOG = LogFactory.getLog(Tag.DB);

	/* Queries at the end */

	private final DataBaseHelper m_dataBaseHelper;
	private SQLiteDatabase m_database;

	public DataBaseWriter(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	// #region Prepared statements
	private SQLiteStatement m_insertStation;
	private SQLiteStatement m_insertStationType;

	private SQLiteDatabase prepareDB() {
		SQLiteDatabase database = m_dataBaseHelper.getWritableDatabase();
		prepareStatements(database);
		return database;
	}

	private void prepareStatements(final SQLiteDatabase database) {
		if (m_database != database) {
			LOG.info("Preparing statements %s -> %s", DBTools.toString(m_database), DBTools.toString(database));
			m_database = database;

			if (m_insertStation != null) {
				m_insertStation.close();
			}
			m_insertStation = database.compileStatement(SQL_INSERT_STATION);

			if (m_insertStationType != null) {
				m_insertStationType.close();
			}
			m_insertStationType = database.compileStatement(SQL_INSERT_STATION_TYPE);
		}
	}

	// #endregion

	// #region insert*
	public void insertStations(final Iterable<Station> stations) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (Station station: stations) {
				insertStation(station);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	public void insertStation(final Station station) {
		SQLiteDatabase database = null;
		try {
			LOG.debug("Inserting station: %d, %s, %s", station.getId(), station.getName(), station.getLocation());
			database = prepareDB();
			int column = 0;
			bindObjectToProgram(m_insertStation, ++column, station.getId());
			bindObjectToProgram(m_insertStation, ++column, station.getName());
			bindObjectToProgram(m_insertStation, ++column, station.getAddress());
			bindObjectToProgram(m_insertStation, ++column, station.getTelephone());
			Location location = station.getLocation();
			if (location != null) {
				bindObjectToProgram(m_insertStation, ++column, location.getLatitude());
				bindObjectToProgram(m_insertStation, ++column, location.getLongitude());
			} else {
				m_insertStation.bindNull(++column);
				m_insertStation.bindNull(++column);
			}
			long stationID;
			try {
				stationID = m_insertStation.executeInsert();
			} catch (SQLiteConstraintException ex) {
				LOG.warn("Cannot insert station, getting existing (%s)", ex, station.getName());
				stationID = getStationID(station.getName());
			}
			station.setId((int)stationID);
		} finally {
			IOTools.ignorantClose(database);
		}
	}
	public void updateStations(final Iterable<Station> stations) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (Station station: stations) {
				updateStation(station);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	public void updateStation(final Station station) {
		LOG.debug("Updating station: %d, %s, %s", station.getId(), station.getName(), station.getLocation());
		SQLiteDatabase database = prepareDB();
		if (m_dataBaseHelper.getReader().getStation(station.getId()) != null) {
			LOG.debug("Already existing, skipping (TODO: implement update)");
			database.close();
			return;
		}
		int column = 0;
		bindObjectToProgram(m_insertStation, ++column, station.getId());
		bindObjectToProgram(m_insertStation, ++column, station.getName());
		bindObjectToProgram(m_insertStation, ++column, station.getType().ordinal());
		bindObjectToProgram(m_insertStation, ++column, station.getAddress());
		bindObjectToProgram(m_insertStation, ++column, station.getTelephone());
		Location location = station.getLocation();
		if (location != null) {
			bindObjectToProgram(m_insertStation, ++column, location.getLatitude());
			bindObjectToProgram(m_insertStation, ++column, location.getLongitude());
		} else {
			m_insertStation.bindNull(++column);
			m_insertStation.bindNull(++column);
		}
		long stationID;
		try {
			stationID = m_insertStation.executeInsert();
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot insert station, getting existing (%s)", ex, station.getName());
			stationID = getStationID(station.getName());
		}
		station.setId((int)stationID);
	}

	private long getStationID(final String stationName) {
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		return DatabaseUtils
				.longForQuery(database, "SELECT _id FROM Station WHERE name = ?", new String[]{stationName});
	}

	public void insertTypes(Map<String, String> styles) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (Map.Entry<String, String> entry: styles.entrySet()) {
				String name = entry.getKey();
				String url = entry.getValue();
				insertType(name, url);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}
	private void insertType(String name, String url) {
		LOG.debug("Inserting type: %s: %s", name, url);

		int column = 0;
		bindObjectToProgram(m_insertStationType, ++column, name);
		bindObjectToProgram(m_insertStationType, ++column, url);
		long typeId;
		try {
			typeId = m_insertStationType.executeInsert();
			LOG.debug("Inserted type: [%d] %s: %s", typeId, name, url);
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot insert station type, getting existing (%s)", ex, name);
			// typeId = getStationTypeID(name);
			updateType(name, url);
		}
	}

	@SuppressWarnings("unused")
	private long getStationTypeID(final String stationTypeName) {
		SQLiteDatabase database = m_dataBaseHelper.getReadableDatabase();
		return longForQuery(database, "SELECT _id FROM StationType WHERE name = ?", new String[]{stationTypeName});
	}

	public void updateTypes(Map<String, String> styles) {
		SQLiteDatabase database = prepareDB();
		try {
			database.beginTransaction();
			for (Map.Entry<String, String> entry: styles.entrySet()) {
				String name = entry.getKey();
				String url = entry.getValue();
				updateType(name, url);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}
	public void updateType(String name, String url) {
		SQLiteDatabase database = prepareDB();
		LOG.debug("Updating type: %s: %s", name, url);
		try {
			ContentValues values = new ContentValues(2);
			values.put("url", url);
			int rows = database.update("StationType", values, "name = ?", new String[]{name});
			if (rows == 0) {
				insertType(name, url);
			}
			LOG.debug("Updated type: %s: %s", name, url);
		} catch (SQLiteConstraintException ex) {
			LOG.warn("Cannot update location, ignoring", ex);
		}
	}

	// #endregion

	// #region Queries

	// #noformat
	private static final String SQL_INSERT_STATION = "INSERT INTO "
			+ "Station(_id, name, type, address, telephone, latitude, longitude) "
			+ "VALUES(   ?,    ?,    ?,       ?,         ?,        ?,         ?);";
	private static final String SQL_INSERT_STATION_TYPE = "INSERT INTO " + "StationType(name, url) "
			+ "VALUES(        ?,   ?);";

	// #endnoformat

	// #endregion
}
