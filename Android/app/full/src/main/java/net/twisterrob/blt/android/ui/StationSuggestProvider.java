package net.twisterrob.blt.android.ui;

import java.util.*;

import org.slf4j.*;

import android.app.SearchManager;
import android.content.*;
import android.database.*;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;

import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.db.model.Station;

public class StationSuggestProvider extends ContentProvider {
	public static class StationSuggestCursor extends AbstractCursor {
		private static final int ID = 0;
		private static final int TEXT1 = 1;
		private static final int TEXT2 = 2;
		private static final int DATA_ID = 3;
		private List<Station> m_stations;
		private String[] m_columns;

		public StationSuggestCursor(String[] columns, List<Station> stations) {
			m_columns = columns;
			m_stations = stations;
		}

		@Override public int getCount() {
			return m_stations.size();
		}

		@Override public String[] getColumnNames() {
			return m_columns;
		}

		@Override public String getString(int column) {
			Station station = m_stations.get(getPosition());
			switch (column) {
				case TEXT1:
					return station.getName();
				case TEXT2:
					return station.getLines().toString();
				case DATA_ID:
					return String.valueOf(station.getName().hashCode());
				default:
					throw new IllegalArgumentException("No such column: " + column);
			}
		}

		@Override public short getShort(int column) {
			throw new UnsupportedOperationException("Cannot get column as short: " + column);
		}

		@Override public int getInt(int column) {
			throw new UnsupportedOperationException("Cannot get column as int: " + column);
		}

		@Override public long getLong(int column) {
			if (column == ID) {
				return getPosition();
			}
			throw new UnsupportedOperationException("Cannot get column as long: " + column);
		}

		@Override public float getFloat(int column) {
			throw new UnsupportedOperationException("Cannot get column as float: " + column);
		}

		@Override public double getDouble(int column) {
			throw new UnsupportedOperationException("Cannot get column as double: " + column);
		}

		@Override public boolean isNull(int column) {
			throw new UnsupportedOperationException("Cannot get column as null: " + column);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(StationSuggestProvider.class);

	public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".suggest_station";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/search");

	// UriMatcher constant for search suggestions
	private static final int SEARCH_SUGGEST = 1;

	private UriMatcher m_uriMatcher;

	private static final String[] SEARCH_SUGGEST_COLUMNS = {
			BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1,
			SearchManager.SUGGEST_COLUMN_TEXT_2,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
	};

	@Override public String getType(@NonNull Uri uri) {
		switch (m_uriMatcher.match(uri)) {
			case SEARCH_SUGGEST:
				return SearchManager.SUGGEST_MIME_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override public boolean onCreate() {
		m_uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		m_uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		m_uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
		return true;
	}

	@Override public Cursor query(@NonNull Uri uri,
			String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		LOG.debug("query: {}", uri);

		// Use the UriMatcher to see what kind of query we have
		switch (m_uriMatcher.match(uri)) {
			case SEARCH_SUGGEST: {
				String query = uri.getLastPathSegment();
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(query)) {
					MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS, 1);
					cursor.addRow(new String[] {null, "Search London transport stations",
							"Search for station name or serving line.", null});
					return cursor;
				}
				query = query.toLowerCase(Locale.UK);
				LOG.debug("Search suggestions requested: {}", query);
				List<Station> stations = App.db().getStations();
				for (Iterator<Station> it = stations.iterator(); it.hasNext(); ) {
					Station station = it.next();
					if (!station.getName().toLowerCase(Locale.UK).contains(query)) {
						it.remove();
					}
				}
				return new StationSuggestCursor(SEARCH_SUGGEST_COLUMNS, stations);
			}
			default:
				throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	@Override public Uri insert(@NonNull Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
}
