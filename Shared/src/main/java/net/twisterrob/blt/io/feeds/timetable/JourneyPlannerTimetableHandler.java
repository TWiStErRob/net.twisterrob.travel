package net.twisterrob.blt.io.feeds.timetable;

import java.io.*;
import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.io.feeds.BaseFeedHandler;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.AnnotatedNptgLocalityRef;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.Operator;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.Root;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.Route;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.RouteLink;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.RouteLink.DirectionEnum;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.RouteSection;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.Service;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.Service.Line;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.StopPoint;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.StopPoint.Descriptor;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.StopPoint.Place;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.StopPoint.Place.Location;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.StopPoint.Place.Location.PrecisionEnum;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.StopPoint.StopClassification;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.StopPoint.StopClassification.StopTypeEnum;
import net.twisterrob.java.model.LocationConverter;

import org.xml.sax.*;

import android.sax.*;
import android.util.Xml;

@NotThreadSafe
public class JourneyPlannerTimetableHandler extends BaseFeedHandler<JourneyPlannerTimetableFeed> {
	protected JourneyPlannerTimetableFeed m_root = new JourneyPlannerTimetableFeed();
	protected Map<String, net.twisterrob.blt.io.feeds.timetable.Locality> m_localities = new HashMap<>();
	protected Map<String, net.twisterrob.blt.io.feeds.timetable.StopPoint> m_stopPoints = new HashMap<>();
	protected Map<String, net.twisterrob.blt.io.feeds.timetable.RouteSection> m_routeSections = new HashMap<>();
	protected Map<String, net.twisterrob.blt.io.feeds.timetable.RouteLink> m_routeLinks = new HashMap<>();
	protected Map<String, net.twisterrob.blt.io.feeds.timetable.Route> m_routes = new HashMap<>();
	protected net.twisterrob.blt.io.feeds.timetable.Locality m_localilty;
	protected net.twisterrob.blt.io.feeds.timetable.StopPoint m_stopPoint;
	protected net.twisterrob.blt.io.feeds.timetable.RouteSection m_routeSection;
	protected net.twisterrob.blt.io.feeds.timetable.RouteLink m_routeLink;
	protected net.twisterrob.blt.io.feeds.timetable.Route m_route;
	protected Integer m_east;
	protected Integer m_north;

	@Override
	public JourneyPlannerTimetableFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement(Root.NS, Root.ELEMENT);
		root.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root = new JourneyPlannerTimetableFeed();
			}
		});

		Element localities = root.getChild(Root.NS, Root.NptgLocalities);
		Element locality = localities.getChild(Root.NS, AnnotatedNptgLocalityRef.ELEMENT);
		setupLocalityParsing(locality);

		Element stopPoints = root.getChild(Root.NS, Root.StopPoints);
		Element stopPoint = stopPoints.getChild(Root.NS, StopPoint.ELEMENT);
		setupStopPointParsing(stopPoint);

		Element routeSections = root.getChild(Root.NS, Root.RouteSections);
		Element routeSection = routeSections.getChild(Root.NS, RouteSection.ELEMENT);
		setupRouteSectionParsing(routeSection);

		Element routes = root.getChild(Root.NS, Root.Routes);
		Element route = routes.getChild(Root.NS, Route.ELEMENT);
		setupRouteParsing(route);

		Element services = root.getChild(Root.NS, Root.Services);
		Element service = services.getChild(Root.NS, Service.ELEMENT);
		Element lines = service.getChild(Root.NS, Service.Lines);
		Element line = lines.getChild(Root.NS, Line.ELEMENT);
		Element lineName = line.getChild(Root.NS, Line.LineName);
		lineName.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				net.twisterrob.blt.model.Line line = net.twisterrob.blt.model.Line.fromAlias(body);
				m_root.setLine(line);
			}
		});

		Element operators = root.getChild(Root.NS, Root.Operators);
		Element operator = operators.getChild(Root.NS, Operator.ELEMENT);
		Element operatorCode = operator.getChild(Root.NS, Operator.OperatorCode);
		operatorCode.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				net.twisterrob.blt.model.Operator op = net.twisterrob.blt.model.Operator.valueOf(body);
				m_root.setOperator(op);
			}
		});

		Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		m_root.postProcess();
		return m_root;
	}
	protected void setupLocalityParsing(Element locality) {
		locality.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_localilty = new Locality();
			}
			@Override
			public void end() {
				m_localities.put(m_localilty.getId(), m_localilty);
				m_localilty = null;
			}
		});
		locality.getChild(Root.NS, AnnotatedNptgLocalityRef.ID) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						m_localilty.setId(body);
					}
				});
		locality.getChild(Root.NS, AnnotatedNptgLocalityRef.Name) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						m_localilty.setName(body);
					}
				});
	}

	protected void setupStopPointParsing(Element stopPoint) {
		stopPoint.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_stopPoint = new net.twisterrob.blt.io.feeds.timetable.StopPoint();
			}
			@Override
			public void end() {
				m_stopPoints.put(m_stopPoint.getId(), m_stopPoint);
				m_stopPoint = null;
			}
		});
		stopPoint.getChild(Root.NS, StopPoint.AtcoCode) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						m_stopPoint.setId(body);
					}
				});
		stopPoint.getChild(Root.NS, Descriptor.ELEMENT) //
				.getChild(Root.NS, Descriptor.CommonName) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						m_stopPoint.setName(body);
					}
				});
		Element place = stopPoint.getChild(Root.NS, Place.ELEMENT);
		place.getChild(Root.NS, Place.NptgLocalityRef) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						net.twisterrob.blt.io.feeds.timetable.Locality locality = m_localities.get(body);
						m_stopPoint.setLocality(locality);
					}
				});
		Element location = place.getChild(Root.NS, Location.ELEMENT);
		location.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrPrecision = attributes.getValue(Location.precision);
				PrecisionEnum precision = PrecisionEnum.valueOfXml(attrPrecision);
				m_stopPoint.setPrecision(precision.getMeters());
			}
			@Override
			public void end() {
				m_stopPoint.setLocation(LocationConverter.gridRef2LatLon(m_east, m_north));
				m_east = null;
				m_north = null;
			}
		});
		location.getChild(Root.NS, Location.Easting) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						m_east = Integer.parseInt(body);
					}
				});

		location.getChild(Root.NS, Location.Northing) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						m_north = Integer.parseInt(body);
					}
				});
		Element classification = stopPoint.getChild(Root.NS, StopClassification.ELEMENT);
		classification.getChild(Root.NS, StopClassification.StopType) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						StopTypeEnum type = StopTypeEnum.valueOf(body);
						m_stopPoint.setType(type);
					}
				});
	}

	protected void setupRouteSectionParsing(Element routeSection) {
		routeSection.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrId = attributes.getValue(RouteSection.id);
				m_routeSection = new net.twisterrob.blt.io.feeds.timetable.RouteSection();
				m_routeSection.setId(attrId);
			}
			@Override
			public void end() {
				m_routeSections.put(m_routeSection.getId(), m_routeSection);
				m_routeSection = null;
			}
		});
		Element routeLink = routeSection.getChild(Root.NS, RouteLink.ELEMENT);
		setupRouteLinkParsing(routeLink);
	}

	protected void setupRouteLinkParsing(Element routeLink) {
		routeLink.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrId = attributes.getValue(RouteLink.id);
				m_routeLink = new net.twisterrob.blt.io.feeds.timetable.RouteLink();
				m_routeLink.setId(attrId);
			}
			@Override
			public void end() {
				m_routeLinks.put(m_routeLink.getId(), m_routeLink);
				m_routeSection.addLink(m_routeLink);
				m_routeLink = null;
			}
		});
		routeLink.getChild(Root.NS, RouteLink.From.ELEMENT) //
				.getChild(Root.NS, RouteLink.From.StopPointRef) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						net.twisterrob.blt.io.feeds.timetable.StopPoint from = m_stopPoints.get(body);
						m_routeLink.setFrom(from);
					}
				});
		routeLink.getChild(Root.NS, RouteLink.To.ELEMENT) //
				.getChild(Root.NS, RouteLink.To.StopPointRef) //
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						net.twisterrob.blt.io.feeds.timetable.StopPoint to = m_stopPoints.get(body);
						m_routeLink.setTo(to);
					}
				});
		routeLink.getChild(Root.NS, RouteLink.Distance).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				int distance = Integer.parseInt(body);
				m_routeLink.setDistance(distance);
			}
		});
		routeLink.getChild(Root.NS, RouteLink.Direction).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				DirectionEnum direction = DirectionEnum.valueOf(body);
				m_routeLink.setDirection(direction);
			}
		});
	}

	protected void setupRouteParsing(Element route) {
		route.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrId = attributes.getValue(Route.id);
				m_route = new net.twisterrob.blt.io.feeds.timetable.Route();
				m_route.setId(attrId);
			}
			@Override
			public void end() {
				m_routes.put(m_route.getId(), m_route);
				m_root.addRoute(m_route);
				m_route = null;
			}
		});
		route.getChild(Root.NS, Route.Description).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				m_route.setDescription(body);
			}
		});
		route.getChild(Root.NS, Route.RouteSectionRef).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				net.twisterrob.blt.io.feeds.timetable.RouteSection section = m_routeSections.get(body);
				m_route.addSection(section);
			}
		});
	}
}
