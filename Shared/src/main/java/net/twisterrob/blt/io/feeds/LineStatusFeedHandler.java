package net.twisterrob.blt.io.feeds;

import java.io.*;

import net.twisterrob.blt.model.*;

import org.xml.sax.*;

import android.sax.*;
import android.util.Xml;

public class LineStatusFeedHandler extends BaseFeedHandler<LineStatusFeed> {
	private interface X extends LineStatusFeedXml {} // Shorthand for the XML interface

	private LineStatusFeed m_root;
	private LineStatus m_lineStatus;

	public LineStatusFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement(X.Root.NS, X.Root.ELEMENT);
		Element lineStatusElement = root.getChild(X.LineStatus.NS, X.LineStatus.ELEMENT);
		Element lineStatusLineElement = lineStatusElement.getChild(X.Line.NS, X.Line.ELEMENT);
		Element lineStatusStatusElement = lineStatusElement.getChild(X.Status.NS, X.Status.ELEMENT);

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
				String attrStatusDetails = attributes.getValue(X.LineStatus.statusDetails);
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
				String attrName = attributes.getValue(X.Line.name);

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
				String attrId = attributes.getValue(X.Status.id);
				DelayType statusType = DelayType.fromID(attrId);
				if (statusType == DelayType.Unknown && attrId != null) {
					String attrDescription = attributes.getValue(X.Status.description);
					sendMail(DelayType.class + " new code: " + attrDescription + " as " + attrId);
				}
				String attrIsActive = attributes.getValue(X.Status.id);
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
