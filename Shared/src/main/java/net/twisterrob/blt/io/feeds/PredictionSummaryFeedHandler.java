package net.twisterrob.blt.io.feeds;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.model.*;

import org.xml.sax.*;

import android.sax.*;
import android.util.Xml;

@NotThreadSafe
public class PredictionSummaryFeedHandler extends BaseFeedHandler<PredictionSummaryFeed> {
	private interface X extends PredicitonSummaryFeedXml { /* Shorthand for the XML interface */}

	PredictionSummaryFeed m_root = new PredictionSummaryFeed();
	Station m_station;
	Platform m_platform;
	Train m_train;

	@Override
	public PredictionSummaryFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement(X.Root.NS, X.Root.ELEMENT);
		Element timeElement = root.getChild(X.Time.NS, X.Time.ELEMENT);
		Element stationElement = root.getChild(X.Station.NS, X.Station.ELEMENT);
		Element platformElement = stationElement.getChild(X.Platform.NS, X.Platform.ELEMENT);
		Element trainElement = platformElement.getChild(X.Train.NS, X.Train.ELEMENT);
		root.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root = new PredictionSummaryFeed();
			}
		});

		timeElement.setStartElementListener(new StartElementListener() {
			private final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(X.Time.timeStamp$format, Locale.UK);
			@Override
			public void start(Attributes attributes) {
				String attrTimeStamp = attributes.getValue(X.Time.timeStamp);
				Date date;
				try {
					date = TIMESTAMP_FORMAT.parse(attrTimeStamp);
				} catch (ParseException e) {
					// TODO log
					date = new Date();
				}
				m_root.setTimeStamp(date);
			}
		});

		stationElement.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrCode = attributes.getValue(X.Station.code);
				String attrName = attributes.getValue(X.Station.name);
				String name = attrName.replaceAll("\\.+$", ""); // remove trailing .
				m_station = new Station();
				m_station.setName(name);
				m_station.setTrackerNetCode(attrCode);
			}
			@Override
			public void end() {
				m_root.addStation(m_station);
				m_station = null;
			}
		});
		platformElement.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrCode = attributes.getValue(X.Platform.code);
				int code = Integer.parseInt(attrCode);
				String attrName = attributes.getValue(X.Platform.name);

				m_platform = new Platform();
				m_platform.setName(attrName);
				m_platform.setCode(code);
			}
			@Override
			public void end() {
				m_root.addPlatform(m_station, m_platform);
				m_platform = null;
			}
		});
		trainElement.setElementListener(new ElementListener() {
			private final DateFormat TIME_TO_FORMAT = new SimpleDateFormat(X.Train.timeToStation$format, Locale.UK);
			@Override
			public void start(Attributes attributes) {
				String attrSetNumber = attributes.getValue(X.Train.setNumber);
				int setNumber = Integer.parseInt(attrSetNumber);
				String attrTripNumber = attributes.getValue(X.Train.tripNumber);
				int tripNumber = Integer.parseInt(attrTripNumber);
				String attrLocation = attributes.getValue(X.Train.Location);
				String attrDestinationCode = attributes.getValue(X.Train.destinationCode);
				int destinationCode = Integer.parseInt(attrDestinationCode);
				String attrDestinationName = attributes.getValue(X.Train.destinationName);
				String attrTimeToStation = attributes.getValue(X.Train.timeToStation);
				Date timeToStation;
				try {
					if (X.Train.timeToStation$atPlatform.equals(attrTimeToStation)) {
						timeToStation = new Date(0);
					} else {
						timeToStation = TIME_TO_FORMAT.parse(attrTimeToStation);
					}
				} catch (ParseException e) {
					throw new IllegalArgumentException(attrTimeToStation + " is not in " + X.Train.timeToStation$format
							+ " format");
				}

				m_train = new Train();
				m_train.setLocation(attrLocation);
				m_train.setDestinationCode(destinationCode);
				m_train.setDestinationName(attrDestinationName);
				m_train.setSetNumber(setNumber);
				m_train.setTripNumber(tripNumber);
				m_train.setTimeToStation(timeToStation);
			}
			@Override
			public void end() {
				m_root.addTrain(m_station, m_platform, m_train);
				m_train = null;
			}
		});

		Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		m_root.postProcess();
		return m_root;
	}
}
