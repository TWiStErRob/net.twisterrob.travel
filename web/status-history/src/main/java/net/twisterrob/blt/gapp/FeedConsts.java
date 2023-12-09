package net.twisterrob.blt.gapp;

import net.twisterrob.blt.data.*;
import net.twisterrob.blt.io.feeds.*;

public interface FeedConsts {
	String EMAIL = "papp.robert.s@gmail.com";

	//URLBuilder URL_BUILDER = new LocalhostUrlBuilder(); // debug
	URLBuilder URL_BUILDER = new TFLUrlBuilder(EMAIL);

	StaticData STATIC_DATA = new SharedStaticData();

	String ENCODING = "UTF-8";

	String DS_PROP_RETRIEVED_DATE = "retrievedDate";
	String DS_PROP_CONTENT = "content";
	String DS_PROP_ERROR = "error";
}
