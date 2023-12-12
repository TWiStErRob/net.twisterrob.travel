package net.twisterrob.blt.io.feeds.timetable;

import java.io.*;
import java.util.Locale;

import javax.xml.stream.*;

import net.twisterrob.java.io.IndentingXMLStreamWriter;

public class JourneyPlannerTimetableFeedKmlOut {
	public static void writeKml(OutputStream outputStream, JourneyPlannerTimetableFeed feed)
			throws UnsupportedEncodingException, XMLStreamException, FactoryConfigurationError {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter out = factory.createXMLStreamWriter(new OutputStreamWriter(outputStream, "utf-8"));
		out = new IndentingXMLStreamWriter(out);

		out.writeStartDocument();
		out.writeStartElement("kml");
		out.writeDefaultNamespace("http://earth.google.com/kml/2.2");
		out.writeStartElement("Document");

		{
			out.writeStartElement("name");
			out.writeCharacters(feed.getLine().getTitle());
			out.writeEndElement();
		}
		{
			out.writeStartElement("description");
			StringBuilder desc = new StringBuilder(String.format(Locale.getDefault(),
					"All stops for the following routes on the %s line:",
					feed.getLine()));
			for (Route route : feed.getRoutes()) {
				desc.append(String.format(Locale.getDefault(), "\n\t%s", route.getDescription()));
			}
			out.writeCData(desc.toString());
			out.writeEndElement();
		}
		{
			out.writeStartElement("Style");
			out.writeAttribute("id", "subway");
			{
				out.writeStartElement("IconStyle");
				out.writeStartElement("Icon");
				out.writeStartElement("href");
				out.writeCharacters("http://maps.gstatic.com/intl/en_au/mapfiles/ms/micons/subway.png");
				out.writeEndElement();
				out.writeEndElement();
				out.writeEndElement();
			}
			out.writeEndElement();
		}
		for (StopPoint stop : JourneyPlannerTimetableFeed.getStopPoints(feed.getRoutes())) {
			out.writeStartElement("Placemark");
			{
				out.writeStartElement("name");
				out.writeCharacters(stop.getName());
				out.writeEndElement();
			}
			{
				out.writeStartElement("description");
				out.writeCharacters(String.format(Locale.getDefault(), "%s in %s",
						stop.getId(), stop.getLocality().getName()));
				out.writeEndElement();
			}
			{
				out.writeStartElement("styleUrl");
				out.writeCharacters("#subway");
				out.writeEndElement();
			}
			{
				out.writeStartElement("Point");
				out.writeStartElement("coordinates");
				out.writeCharacters(String.format(Locale.ROOT, "%f, %f",
						stop.getLocation().getLongitude(), stop.getLocation().getLatitude()));
				out.writeEndElement();
				out.writeEndElement();
			}
			out.writeEndElement();
		}

		out.writeEndElement();
		out.writeEndElement();
		out.writeEndDocument();
		out.close();
	}
}
