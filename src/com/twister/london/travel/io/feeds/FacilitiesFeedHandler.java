package com.twister.london.travel.io.feeds;

import java.io.*;
import java.util.ArrayList;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import android.sax.*;
import android.util.Xml;

import com.twister.android.utils.log.*;
import com.twister.android.utils.model.Location;
import com.twister.android.utils.text.ElementAdapter;
import com.twister.london.travel.model.*;

public class FacilitiesFeedHandler extends DefaultHandler {
	private static final Log LOG = LogFactory.getLog(Tag.IO);
	private FacilitiesFeed m_root;
	private Station m_station;

	public FacilitiesFeed parse(InputStream is) {
		RootElement root = new RootElement("Root");
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
		Element stationPlacemarkDescription = stationPlacemarkElement.getChild("description");
		Element stationPlacemarkStyleUrl = stationPlacemarkElement.getChild("styleUrl");
		Element stationPlacemarkPoint = stationPlacemarkElement.getChild("Point");
		Element stationCoordinates = stationPlacemarkPoint.getChild("coordinates");

		root.setElementListener(new ElementListener() {
			@Override public void start(Attributes attributes) {
				m_root = new FacilitiesFeed();
			}
			@Override public void end() {}

		});
		stationsElement.setElementListener(new ElementListener() {
			@Override public void start(Attributes attributes) {
				m_root.setStations(new ArrayList<Station>());
			}
			@Override public void end() {}

		});
		stationElement.setElementListener(new ElementListener() {
			@Override public void start(Attributes attributes) {
				m_station = new Station();
				String attrId = attributes.getValue("id");
				int id = Integer.parseInt(attrId);
				m_station.setId(id);
			}
			@Override public void end() {
				m_root.getStations().add(m_station);
				m_station = null;
			}
		});

		stationName.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				m_station.setName(body);
			}
		});
		stationAddress.setEndTextElementListener(new EndTextElementListener() {
			@Override public void end(String body) {
				m_station.setAddress(body);
			}
		});
		stationPhone.setEndTextElementListener(new EndTextElementListener() {
			@Override public void end(String body) {
				m_station.setTelephone(body);
			}
		});
		stationCoordinates.setEndTextElementListener(new EndTextElementListener() {
			@Override public void end(String body) {
				String[] parts = body.split(",");
				if (parts.length == 3) {
					double lat = Double.parseDouble(parts[0]);
					double lon = Double.parseDouble(parts[1]);
					double alt = Double.parseDouble(parts[2]);
					m_station.setLocation(new Location(lat, lon));
				}
			}
		});
		stationZones.setStartElementListener(new StartElementListener() {
			@Override public void start(Attributes attributes) {
				m_station.setZones(new ArrayList<Zone>());
			}
		});
		stationZone.setEndTextElementListener(new EndTextElementListener() {
			@Override public void end(String body) {
				int zone = Integer.parseInt(body);
				m_station.getZones().add(new Zone(zone));
			}
		});
		stationServingLines.setStartElementListener(new StartElementListener() {
			@Override public void start(Attributes attributes) {
				m_station.setLines(new ArrayList<Line>());
			}
		});
		stationServingLine.setEndTextElementListener(new EndTextElementListener() {
			@Override public void end(String body) {
				m_station.getLines().add(new Line(body));
			}
		});
		stationFacilities.setStartElementListener(new StartElementListener() {
			@Override public void start(Attributes attributes) {
				m_station.setFacilities(new ArrayList<Facility>());
			}
		});
		ElementAdapter facilitiesListener = new ElementAdapter() {
			private Facility m_facility;
			@Override public void start(Attributes attributes) {
				String name = attributes.getValue("name");
				m_facility = new Facility(name);
			}
			@Override public void end(String body) {
				m_facility.setValue(body);
			}
			@Override public void end() {
				m_station.getFacilities().add(m_facility);
				// m_facility = null; // Don't null, because end is called before end(text)
			}
		};
		stationFacility.setElementListener(facilitiesListener);
		stationFacility.setEndTextElementListener(facilitiesListener);
		try {
			Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
			m_root.postProcess();
			return m_root;
		} catch (SAXException ex) {
			LOG.error("XML", ex);
		} catch (IOException ex) {
			LOG.error("XML", ex);
		}

		return null;
	}
}
