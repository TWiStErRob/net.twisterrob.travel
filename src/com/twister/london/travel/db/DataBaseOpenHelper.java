package com.twister.london.travel.db;

import java.io.*;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

import com.twister.android.utils.log.*;
import com.twister.android.utils.tools.*;
import com.twister.london.travel.*;

class DataBaseOpenHelper extends SQLiteOpenHelper {
	private static final String DB_SCHEMA_FILE = "LondonTravel.v1.schema.sql";
	private static final String DB_DATA_FILE = "LondonTravel.v1.data.sql";
	private static final String DB_CLEAN_FILE = "LondonTravel.v1.clean.sql";
	private static final String DB_DEVELOPMENT_FILE = "LondonTravel.v1.development.sql";
	private static final String DB_NAME = "LondonTravel";
	private static final int DB_VERSION = 1;
	private static final Log LOG = LogFactory.getLog(Tag.DB);
	private static final CursorFactory s_factory = new LoggingCursorFactory(BuildConfig.DEBUG);

	public DataBaseOpenHelper(final Context context) {
		super(context, DB_NAME, s_factory, DB_VERSION);
	}

	@Override public void onOpen(final SQLiteDatabase db) {
		super.onOpen(db);
		LOG.debug("Opening database: %s", DBTools.toString(db));
		backupDB(db, DB_NAME + ".onOpen_BeforeDev.sqlite");
		DataBaseOpenHelper.execFile(db, DB_DEVELOPMENT_FILE);
		backupDB(db, DB_NAME + ".onOpen_AfterDev.sqlite");
		// onCreate(db); // FIXME for DB development, always clear and initialize
		LOG.info("Opened database: %s", DBTools.toString(db));
	}

	private void backupDB(final SQLiteDatabase db, final String fileName) {
		if (BuildConfig.DEBUG) {
			try {
				String target = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
				IOTools.copyFile(db.getPath(), target);
				LOG.info("DB backed up to %s", target);
			} catch (IOException ex) {
				LOG.error("Cannot back up DB on open", ex);
			}
		}
	}

	@Override public void onCreate(final SQLiteDatabase db) {
		backupDB(db, DB_NAME + ".onCreate.sqlite");
		LOG.debug("Creating database: %s", DBTools.toString(db));
		DataBaseOpenHelper.execFile(db, DB_CLEAN_FILE);
		DataBaseOpenHelper.execFile(db, DB_SCHEMA_FILE);
		DataBaseOpenHelper.execFile(db, DB_DATA_FILE);
		LOG.info("Created database: %s", DBTools.toString(db));
	}

	private static void execFile(final SQLiteDatabase db, final String dbSchemaFile) {
		LOG.debug("Executing file %s into database: %s", dbSchemaFile, DBTools.toString(db));
		long time = System.nanoTime();

		DataBaseOpenHelper.realExecuteFile(db, dbSchemaFile);

		long end = System.nanoTime();
		LOG.debug("Finished (%3$d ms) executed file %1$s into database: %2$s", dbSchemaFile, DBTools.toString(db),
				(end - time) / 1000 / 1000);
	}

	private static void realExecuteFile(final SQLiteDatabase db, final String dbSchemaFile) {
		InputStream s;
		String statement = null;
		try {
			s = App.getInstance().getAssets().open(dbSchemaFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(s, IOTools.ENCODING));
			while ((statement = DataBaseOpenHelper.getNextStatement(reader)) != null) {
				db.execSQL(statement);
			}
		} catch (SQLException ex) {
			LOG.error("Error creating database from file: %s while executing\n%s", ex, dbSchemaFile, statement);
		} catch (IOException ex) {
			LOG.error("Error creating database from file: ", ex, dbSchemaFile);
		}
	}

	private static String getNextStatement(final BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.matches("\\s*")) {
				continue; // empty lines
			}
			sb.append(line);
			sb.append(IOTools.LINE_SEPARATOR);
			if (line.matches(".*;\\s*$")) {
				return sb.toString(); // ends in a semicolon -> end of statement
			}
		}
		return null;
	}

	@Override public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		backupDB(db, DB_NAME + ".onUpgrade.sqlite");
		onCreate(db);
	}
}
