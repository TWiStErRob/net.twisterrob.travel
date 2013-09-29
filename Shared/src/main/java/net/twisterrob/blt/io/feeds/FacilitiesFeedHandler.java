package net.twisterrob.blt.io.feeds;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import net.twisterrob.android.utils.model.Location;
import net.twisterrob.android.utils.text.ElementAdapter;
import net.twisterrob.android.utils.tools.PrimitiveTools;
import net.twisterrob.blt.model.*;

import org.xml.sax.*;

import android.sax.*;
import android.util.Xml;

public class FacilitiesFeedHandler extends BaseFeedHandler<FacilitiesFeed> {
	private FacilitiesFeed m_root;
	private Station m_station;

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

		root.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root = new FacilitiesFeed();
			}
			@Override
			public void end() {}

		});
		net.twisterrob.android.utils.text.ElementListener styleListener = new ElementAdapter() {
			private String m_id;
			@Override
			public void start(Attributes attributes) {
				m_id = attributes.getValue("id");
			}
			@Override
			public void end(String body) {
				m_root.getStyles().put(m_id, body);
			}
		};
		styleElement.setStartElementListener(styleListener);
		styleHref.setEndTextElementListener(styleListener);
		stationsElement.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root.setStations(new ArrayList<Station>());
			}
			@Override
			public void end() {}

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
					LOG.warn("Different station names received: %s VS %s", existingName, newName);
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
		ElementAdapter facilitiesListener = new ElementAdapter() {
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
			@Override
			public void end() {
				// m_facility = null; // Don't null, because end is called before end(text)
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

		};
		stationFacility.setElementListener(facilitiesListener);
		stationFacility.setEndTextElementListener(facilitiesListener);

		stationPlacemarkStyleUrl.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				m_station.setType(Type.get(body));
			}
		});

		Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		m_root.postProcess();
		return m_root;
	}
}