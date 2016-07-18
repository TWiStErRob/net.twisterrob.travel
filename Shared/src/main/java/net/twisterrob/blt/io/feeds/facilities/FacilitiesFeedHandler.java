package net.twisterrob.blt.io.feeds.facilities;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.java.utils.PrimitiveTools;
import net.twisterrob.blt.io.feeds.BaseFeedHandler;
import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.Location;

import org.xml.sax.*;

import android.sax.*;
import android.util.Xml;

@NotThreadSafe
public class FacilitiesFeedHandler extends BaseFeedHandler<FacilitiesFeed> {
	FacilitiesFeed m_root = new FacilitiesFeed();
	Station m_station;

	@Override
	public FacilitiesFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement("Root");
		Element styleElement = root.getChild("Style");
		Element styleHref = styleElement.getChild("IconStyle").getChild("Icon").getChild("href");
		Element stationsElement = root.getChild("stations");
		Element stationElement = stationsElement.getChild("station");
		Element stationName = stationElement.getChild("name");
		Element stationContactDetailsElement = stationElement.getChild("contactDetails");
		Element stationAddress = stationContactDetailsElement.getChild("address");
		Element stationPhone = stationContactDetailsElement.getChild("phone");
		Element stationServingLines = stationElement.getChild("servingLines");
		Element stationServingLine = stationServingLines.getChild("servingLine");
		Element stationZones = stationElement.getChild("zones");
		Element stationZone = stationZones.getChild("zone");
		Element stationFacilities = stationElement.getChild("facilities");
		Element stationFacility = stationFacilities.getChild("facility");
		Element stationPlacemarkElement = stationElement.getChild("Placemark");
		Element stationPlacemarkName = stationPlacemarkElement.getChild("name");
		Element stationPlacemarkStyleUrl = stationPlacemarkElement.getChild("styleUrl");
		Element stationPlacemarkPoint = stationPlacemarkElement.getChild("Point");
		Element stationCoordinates = stationPlacemarkPoint.getChild("coordinates");

		root.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root = new FacilitiesFeed();
			}
		});
		class StyleListener implements StartElementListener, EndTextElementListener {
			private String m_id;
			@Override
			public void start(Attributes attributes) {
				m_id = attributes.getValue("id");
			}
			@Override
			public void end(String body) {
				m_root.getStyles().put(m_id, body);
			}
		}
		StyleListener styleListener = new StyleListener();
		styleElement.setStartElementListener(styleListener);
		styleHref.setEndTextElementListener(styleListener);
		stationsElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root.setStations(new ArrayList<Station>());
			}

		});
		stationElement.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_station = new Station();
				String attrId = attributes.getValue("id");
				int id = Integer.parseInt(attrId);
				m_station.setId(id);
			}
			@Override
			public void end() {
				m_root.getStations().add(m_station);
				m_station = null;
			}
		});

		EndTextElementListener stationNameListener = new EndTextElementListener() {
			public void end(String body) {
				String existingName = m_station.getName();
				String newName = body.trim().replaceAll("\\s+(?i:station)$", "");
				if (existingName == null) {
					m_station.setName(newName);
				} else if (!existingName.equals(newName)) {
					LOG.warn("Different station names received: {} VS {}", existingName, newName);
				}
			}
		};
		stationName.setEndTextElementListener(stationNameListener);
		stationPlacemarkName.setEndTextElementListener(stationNameListener);
		stationAddress.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				m_station.setAddress(body);
			}
		});
		stationPhone.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				m_station.setTelephone(body);
			}
		});
		stationCoordinates.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				String[] parts = body.split(",");
				if (parts.length == 3) {
					double lon = Double.parseDouble(parts[0]);
					double lat = Double.parseDouble(parts[1]);
					// double alt = Double.parseDouble(parts[2]);
					m_station.setLocation(new Location(lat, lon));
				}
			}
		});
		stationZones.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_station.setZones(new ArrayList<Zone>());
			}
		});
		stationZone.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				String[] zones = body.split("[^\\d]+");
				for (String zoneString: zones) {
					int zone = Integer.parseInt(zoneString);
					m_station.getZones().add(new Zone(zone));
				}
			}
		});
		stationServingLines.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_station.setLines(new ArrayList<Line>());
			}
		});
		stationServingLine.setEndTextElementListener(new EndTextElementListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void end(String body) {
				Line line = Line.fromAlias(body);
				if (line == Line.unknown && body != null) {
					sendMail(Line.class + " new alias: " + body);
				}
				m_station.getLines().add(line);
			}
		});
		stationFacilities.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_station.setFacilities(new ArrayList<Facility>());
			}
		});
		stationFacility.setTextElementListener(new TextElementListener() {
			private Facility m_facility;
			@Override
			public void start(Attributes attributes) {
				String name = attributes.getValue("name");
				m_facility = new Facility(name); // TODO make Facilities a factory and create subclasses
			}
			@Override
			public void end(String body) {
				m_facility.setValue(body);
				List<Facility> exceptions = handleExceptions();
				if (exceptions != null) {
					for (Facility facility: exceptions) {
						m_station.getFacilities().add(facility);
					}
				} else {
					m_station.getFacilities().add(m_facility);
				}
			}
			private List<Facility> handleExceptions() {
				final String name = m_facility.getName();
				final String value = m_facility.getValue();
				List<Facility> result; // set at every branch, null to read m_facility
				if ("Escalators".equals(name) && "yes (disabled only)".equals(value)) {
					m_facility.setValue("1");
					result = null;
				} else if ("Payphones".equals(name) && PrimitiveTools.parseInteger(value) == null) {
					Matcher m = Pattern.compile("(\\d+) in ticket halls, (\\d+) on platforms").matcher(value);
					if (m.find()) {
						result = Arrays.asList( //
								new Facility("Payphones in ticket halls", m.group(1)), //
								new Facility("Payphones on platforms", m.group(2)) //
								);
					} else {
						result = null;
					}
				} else {
					result = null;
				}

				return result;
			}
		});

		stationPlacemarkStyleUrl.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				StopType type = StopType.unknown;
				if ("tubeStyle".equals(body)) {
					type = StopType.Underground;
				} else if ("overgroundStyle".equals(body)) {
					type = StopType.Overground;
				} else if ("dlrStyle".equals(body)) {
					type = StopType.DLR;
				}
				m_station.setType(type);
			}
		});

		Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		m_root.postProcess();
		return m_root;
	}
}
