package com.twister.london.travel.db;

import android.database.DatabaseUtils;
import android.database.sqlite.*;

import com.twister.android.utils.log.*;
import com.twister.android.utils.model.Location;
import com.twister.android.utils.tools.DBTools;
import com.twister.london.travel.model.Station;

@SuppressWarnings("unused") class DataBaseWriter {
	private static final Log LOG = LogFactory.getLog(Tag.DB);

	/* Queries at the end */

	private final DataBaseHelper m_dataBaseHelper;
	private SQLiteDatabase m_database;

	public DataBaseWriter(final DataBaseHelper dataBaseHelper) {
		m_dataBaseHelper = dataBaseHelper;
	}

	// #region Prepared statements
	private SQLiteStatement m_insertStation;

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
			m_insertStation = database.compileStatement(SQL_INSERT_CINEMA);
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
		}
	}

	public void insertStation(final Station station) {
		LOG.debug("Inserting station: %d, %s, %s", station.getId(), station.getName(), station.getLocation());
		SQLiteDatabase database = prepareDB();
		int column = 0;
		DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, station.getId());
		DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, station.getName());
		DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, station.getAddress());
		DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, station.getTelephone());
		Location location = station.getLocation();
		if (location != null) {
			DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, location.getLatitude());
			DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, location.getLongitude());
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
		}
	}

	public void updateStation(final Station station) {
		LOG.debug("Updating station: %d, %s, %s", station.getId(), station.getName(), station.getLocation());
		SQLiteDatabase database = prepareDB();
		if (m_dataBaseHelper.getReader().getStation(station.getId()) != null) {
			LOG.debug("Already existing, skipping (TODO: implement update)");
			return;
		}
		int column = 0;
		DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, station.getId());
		DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, station.getName());
		DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, station.getAddress());
		DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, station.getTelephone());
		Location location = station.getLocation();
		if (location != null) {
			DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, location.getLatitude());
			DatabaseUtils.bindObjectToProgram(m_insertStation, ++column, location.getLongitude());
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

	// #endregion

	// #region Queries

	// #noformat
	private static final String SQL_INSERT_CINEMA = "INSERT INTO "
			+ "Station(_id, name, address, telephone, latitude, longitude) "
			+ "VALUES(  ?,    ?,       ?,         ?,        ?,         ?);";

	// #endnoformat

	// #endregion
}
