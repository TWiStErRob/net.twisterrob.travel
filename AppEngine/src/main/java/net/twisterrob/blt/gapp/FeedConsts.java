package net.twisterrob.blt.gapp;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.*;

public interface FeedConsts {
	String EMAIL = "papp.robert.s@gmail.com";

	//URLBuilder URL_BUILDER = new LocalhostUrlBuilder(); // debug
	URLBuilder URL_BUILDER = new TFLUrlBuilder(EMAIL);

	LineColors LINE_COLORS = new TubeStatusPresentationLineColors();

	String ENCODING = "UTF-8";

	String DSPROP_RETRIEVED_DATE = "retrievedDate";
	String DSPROP_CONTENT = "content";
	String DSPROP_ERROR = "error";
}
