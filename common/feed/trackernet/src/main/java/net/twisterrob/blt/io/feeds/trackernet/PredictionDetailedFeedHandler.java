package net.twisterrob.blt.io.feeds.trackernet;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import net.twisterrob.blt.io.feeds.BaseFeedHandler;
import net.twisterrob.blt.io.feeds.trackernet.PredictionDetailedFeedXml.Platform;
import net.twisterrob.blt.io.feeds.trackernet.PredictionDetailedFeedXml.Root;
import net.twisterrob.blt.io.feeds.trackernet.PredictionDetailedFeedXml.Station;
import net.twisterrob.blt.io.feeds.trackernet.PredictionDetailedFeedXml.Train;
import net.twisterrob.blt.model.Line;

/**
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsPredictionDetailed
 */
@NotThreadSafe
public class PredictionDetailedFeedHandler extends BaseFeedHandler<PredictionSummaryFeed> {
	
	private final @Nonnull TrackerNetData trackerNetData = new TrackerNetData();
	
	PredictionSummaryFeed m_root;
	net.twisterrob.blt.io.feeds.trackernet.model.Station m_station;
	net.twisterrob.blt.io.feeds.trackernet.model.Platform m_platform;
	net.twisterrob.blt.io.feeds.trackernet.model.Train m_train;

	@Override public PredictionSummaryFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement(Root.NS, Root.ELEMENT);
		Element timeElement = root.getChild(Root.NS, Root.WhenCreated);
		Element lineElement = root.getChild(Root.NS, Root.Line);
		Element stationElement = root.getChild(Root.NS, Station.ELEMENT);
		Element platformElement = stationElement.getChild(Root.NS, Platform.ELEMENT);
		Element trainElement = platformElement.getChild(Root.NS, Train.ELEMENT);
		root.setStartElementListener(new StartElementListener() {
			@Override public void start(Attributes attributes) {
				m_root = new PredictionSummaryFeed();
			}
		});

		timeElement.setEndTextElementListener(new EndTextElementListener() {
			private final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(Root.WhenCreated$format, Locale.UK);
			public void end(String body) {
				Date date;
				try {
					date = TIMESTAMP_FORMAT.parse(body);
				} catch (ParseException e) {
					// TODO log
					date = new Date();
				}
				m_root.setTimeStamp(date);
			}
		});

		lineElement.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				Line line = trackerNetData.lineFromTrackerNetCode(body.charAt(0));
				m_root.setLine(line);
			}
		});

		stationElement.setElementListener(new ElementListener() {
			@Override public void start(Attributes attributes) {
				String attrCode = attributes.getValue(Station.code);
				String attrName = attributes.getValue(Station.name);
				String name = attrName.replaceAll("\\.+$", ""); // remove trailing .
				m_station = new net.twisterrob.blt.io.feeds.trackernet.model.Station();
				m_station.setName(name);
				m_station.setTrackerNetCode(attrCode);
			}
			@Override public void end() {
				m_root.addStation(m_station);
				m_station = null;
			}
		});
		platformElement.setElementListener(new ElementListener() {
			@Override public void start(Attributes attributes) {
				String attrCode = attributes.getValue(Platform.number);
				int code = Integer.parseInt(attrCode);
				String attrName = attributes.getValue(Platform.name);

				m_platform = new net.twisterrob.blt.io.feeds.trackernet.model.Platform();
				m_platform.setName(attrName);
				m_platform.setCode(code);
			}
			@Override public void end() {
				m_root.addPlatform(m_station, m_platform);
				m_platform = null;
			}
		});
		trainElement.setElementListener(new ElementListener() {
			private final DateFormat TIME_TO_FORMAT = new SimpleDateFormat(Train.timeTo$format, Locale.UK);
			@Override public void start(Attributes attributes) {
				String attrSetNumber = attributes.getValue(Train.setNumber);
				int setNumber = Integer.parseInt(attrSetNumber);
				String attrTripNumber = attributes.getValue(Train.tripNumber);
				int tripNumber = Integer.parseInt(attrTripNumber);
				String attrLocation = attributes.getValue(Train.Location);
				String attrDestinationCode = attributes.getValue(Train.destinationCode);
				int destinationCode = Integer.parseInt(attrDestinationCode);
				String attrDestinationName = attributes.getValue(Train.destinationName);
				String attrTimeToStation = attributes.getValue(Train.timeTo);
				Date timeToStation;
				try {
					if (Train.timeTo$atPlatform.equals(attrTimeToStation)) {
						timeToStation = new Date(0);
					} else {
						timeToStation = TIME_TO_FORMAT.parse(attrTimeToStation);
					}
				} catch (ParseException e) {
					throw new IllegalArgumentException(attrTimeToStation + " is not in " + Train.timeTo$format
							+ " format");
				}

				m_train = new net.twisterrob.blt.io.feeds.trackernet.model.Train();
				m_train.setLocation(attrLocation);
				m_train.setDestinationCode(destinationCode);
				m_train.setDestinationName(attrDestinationName);
				m_train.setSetNumber(setNumber);
				m_train.setTripNumber(tripNumber);
				m_train.setTimeToStation(timeToStation);
			}
			@Override public void end() {
				m_root.addTrain(m_station, m_platform, m_train);
				m_train = null;
			}
		});

		Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		m_root.postProcess();
		return m_root;
	}
}
