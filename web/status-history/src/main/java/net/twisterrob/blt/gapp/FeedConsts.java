package net.twisterrob.blt.gapp;

import net.twisterrob.blt.data.*;
import net.twisterrob.blt.io.feeds.*;

public interface FeedConsts {
	String EMAIL = "papp.robert.s@gmail.com";

	//URLBuilder URL_BUILDER = new LocalhostUrlBuilder(); // debug
	URLBuilder URL_BUILDER = new TFLUrlBuilder(EMAIL);

	StaticData STATIC_DATA = new SharedStaticData();
}
