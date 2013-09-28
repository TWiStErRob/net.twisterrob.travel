package net.twisterrob.blt.io.feeds;

import java.io.*;

import net.twisterrob.blt.model.*;

import org.xml.sax.*;

import android.sax.*;
import android.util.Xml;

/**
 * <table>
 * <tr><th>Attribute</th><th>Description</th></tr>
 * <tr><td>LineStatus/ID</td><td>An identifier for the line</td></tr>
 * <tr><td>LineStatus/@StatusDetails</td><td>A description of the status of the line if the status is not normal otherwise this will be blank</td></tr>
 * <tr><td>Children</td><td><table>
	 * <tr><th>Attribute</th><th>Description</th></tr>
	 * <tr><td>BranchDisruptions</td><td>Not Used</td></tr>
	 * <tr><td>Line/@ID</td><td>A code representing the line</td></tr>
	 * <tr><td>Line/@Name</td><td>The line name</td></tr>
	 * <tr><td>Status/@ID</td><td>A numeric code representing the status of the line</td></tr>
	 * <tr><td>Status/@CssClass</td><td>A text code representing the general status of the line, e.g. GoodService, DisruptedService</td></tr>
	 * <tr><td>Status/@Description</td><td>A description of the status of the line e.g. Part Suspended, Severe Delays</td></tr>
	 * <tr><td>Status/@IsActive</td><td>A Boolean indicating if the status shown is active</td></tr>
	 * <tr><td>Children</td><td><table>
		 * <tr><th>Status/@Attribute</th><th>Description</th></tr>
		 * <tr><td>StatusType/@ID</td><td>A code representing the status type the service is checking. For this call it will always return the value “1”</td></tr>
		 * <tr><td>StatusType/@Description</td><td>A description of the status type the service is checking. For this call it will always return the value “Line”</td></tr>
	 * </table></td></tr>d
 * </table></td></tr>d
 * </table>
 * @author TWiStEr
 */
public class LineStatusFeedHandler extends BaseFeedHandler<LineStatusFeed> {
	private static final String NS = "http://webservices.lul.co.uk/";
	private LineStatusFeed m_root;
	private LineStatus m_lineStatus;

	public LineStatusFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement(NS, "ArrayOfLineStatus");
		Element lineStatusElement = root.getChild(NS, "LineStatus");
		Element lineStatusLineElement = lineStatusElement.getChild(NS, "Line");
		Element lineStatusStatusElement = lineStatusElement.getChild(NS, "Status");

		root.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root = new LineStatusFeed();
			}
			@Override
			public void end() {}
		});
		lineStatusElement.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrStatusDetails = attributes.getValue("StatusDetails");
				m_lineStatus = new LineStatus();
				m_lineStatus.setDescription(attrStatusDetails);
			}
			@Override
			public void end() {
				m_root.addLineStatus(m_lineStatus);
				m_lineStatus = null;
			}
		});
		lineStatusLineElement.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrName = attributes.getValue("Name");

				Line line = Line.fromAlias(attrName);
				if (line == Line.unknown && attrName != null) {
					sendMail(Line.class + " new alias: " + attrName);
				}
				m_lineStatus.setLine(line);
			}
			@Override
			public void end() {}
		});
		lineStatusStatusElement.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrId = attributes.getValue("ID");
				DelayType statusType = DelayType.fromID(attrId);
				if (statusType == DelayType.Unknown && attrId != null) {
					String attrDescription = attributes.getValue("Description");
					sendMail(DelayType.class + " new code: " + attrDescription + " as " + attrId);
				}
				String attrIsActive = attributes.getValue("IsActive");
				boolean isActive = Boolean.parseBoolean(attrIsActive);
				m_lineStatus.setType(statusType);
				m_lineStatus.setActive(isActive);
			}
			@Override
			public void end() {}
		});

		Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		m_root.postProcess();
		return m_root;
	}
}
