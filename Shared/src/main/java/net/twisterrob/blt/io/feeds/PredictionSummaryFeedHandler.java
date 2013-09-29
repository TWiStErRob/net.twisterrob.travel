package net.twisterrob.blt.io.feeds;

import java.io.*;
import java.text.*;
import java.util.*;

import net.twisterrob.blt.model.*;

import org.xml.sax.*;

import android.sax.*;
import android.util.Xml;
/**
 * <table>
 * <tr><th>Attribute</th><th>Description</th></tr>
 * <tr><td>Time/@TimeStamp</td><td>The date/time the service was run in the format YYYY/MM/DD HH:MM:SS</td></tr>
 * <tr><td>S(tation)</td><td>A construct representing a station on the line</td></tr>
 * <tr><td>S(tation)/Code</td><td>A code representing the station (see Appendix B for valid values)</td></tr>
 * <tr><td>S(tation)/N(ame)</td><td>The name of the station</td></tr>
 * <tr><td>Children</td><td><table>
	 * <tr><th>Attribute</th><th>Description</th></tr>
	 * <tr><td>P(latform)</td><td>A construct representing a platform on the station</td></tr>
	 * <tr><td>P(latform)/N(ame)</td><td>The name of the platform</td></tr>
	 * <tr><td>P(latform)/C(ode)</td><td>A code representing the platform</td></tr>
	 * <tr><td>Children</td><td><table>
		 * <tr><th>Attribute</th><th>Description</th></tr>
		 * <tr><td>T(rain)</td><td>A construct representing a train in the prediction list</td></tr>
		 * <tr><td>T(rain)/S(et number)</td><td>The set number of the train</td></tr>
		 * <tr><td>T(rain)/T(rip number)</td><td>The trip number of the train</td></tr>
		 * <tr><td>T(rain)/D(estination)</td><td>A code representing the destination of the train</td></tr>
		 * <tr><td>T(rain)/C</td><td>A value in representing the ‘time to station’ for this train in the format MM:SS</td></tr>
		 * <tr><td>T(rain)/L(ocation)</td><td>The current location of the train</td></tr>
		 * <tr><td>T(rain)/DE(stination)</td><td>The name of the destination of the train</td></tr>
	 * </table></td></tr>
 * </table></td></tr>
 * </table>
 * @author TWiStEr
 */
public class PredictionSummaryFeedHandler extends BaseFeedHandler<PredictionSummaryFeed> {
	private static final String STATION = "S";
	private static final String STATION_NAME = "N";
	private static final String STATION_CODE = "Code";
	private static final String PLATFORM = "P";
	private static final String PLATFORM_NAME = "N";
	private static final String PLATFORM_CODE = "Code";
	private static final String TRAIN = "T";
	private static final String TRAIN_SET_NUMBER = "S";
	private static final String TRAIN_TRIP_NUMBER = "T";
	private static final String TRAIN_DESTINATION_CODE = "D";
	private static final String TRAIN_DESTINATION_NAME = "DE";
	private static final String TRAIN_TIME_TO_STATION = "C";
	private static final String TRAIN_LOCATION = "L";

	private PredictionSummaryFeed m_root;
	private Station m_station;
	private Platform m_platform;
	private Train m_train;

	@Override
	public PredictionSummaryFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement("ROOT");
		Element timeElement = root.getChild("Time");
		Element stationElement = root.getChild(STATION);
		Element platformElement = stationElement.getChild(PLATFORM);
		Element trainElement = platformElement.getChild(TRAIN);

		root.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root = new PredictionSummaryFeed();
			}
			@Override
			public void end() {}
		});

		timeElement.setStartElementListener(new StartElementListener() {
			private final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.UK);
			@Override
			public void start(Attributes attributes) {
				String attrTimeStamp = attributes.getValue("TimeStamp");
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
				String attrCode = attributes.getValue(STATION_CODE);
				String attrName = attributes.getValue(STATION_NAME);
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
				String attrCode = attributes.getValue(PLATFORM_CODE);
				int code = Integer.parseInt(attrCode);
				String attrName = attributes.getValue(PLATFORM_NAME);

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
			private final DateFormat TIME_TO_FORMAT = new SimpleDateFormat("mm:ss", Locale.UK);
			@Override
			public void start(Attributes attributes) {
				String attrSetNumber = attributes.getValue(TRAIN_SET_NUMBER);
				int setNumber = Integer.parseInt(attrSetNumber);
				String attrTripNumber = attributes.getValue(TRAIN_TRIP_NUMBER);
				int tripNumber = Integer.parseInt(attrTripNumber);
				String attrLocation = attributes.getValue(TRAIN_LOCATION);
				String attrDestinationCode = attributes.getValue(TRAIN_DESTINATION_CODE);
				int destinationCode = Integer.parseInt(attrDestinationCode);
				String attrDestinationName = attributes.getValue(TRAIN_DESTINATION_NAME);
				String attrTimeToStation = attributes.getValue(TRAIN_TIME_TO_STATION);
				Date timeToStation;
				try {
					if ("-".equals(attrTimeToStation)) {
						timeToStation = new Date(0);
					} else {
						timeToStation = TIME_TO_FORMAT.parse(attrTimeToStation);
					}
				} catch (ParseException e) {
					throw new IllegalArgumentException(attrTimeToStation + " is not in mm:ss format");
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
