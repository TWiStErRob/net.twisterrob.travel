package net.twisterrob.blt.android.data;

import net.twisterrob.blt.android.db.DataBaseHelper;

public class AndroidDBStaticData extends AndroidHardcodedStaticData {
	@SuppressWarnings("unused") private DataBaseHelper m_dbAccess;

	public AndroidDBStaticData(DataBaseHelper dbAccess) {
		m_dbAccess = dbAccess;
	}
}
