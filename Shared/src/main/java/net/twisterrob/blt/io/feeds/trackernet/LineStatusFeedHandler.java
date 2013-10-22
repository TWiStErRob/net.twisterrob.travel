package net.twisterrob.blt.io.feeds.trackernet;

import java.io.*;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.io.feeds.BaseFeedHandler;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.Line;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.LineStatus;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.Root;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.Status;
import net.twisterrob.blt.model.DelayType;

import org.xml.sax.*;

import android.sax.*;
import android.util.Xml;

@NotThreadSafe
public class LineStatusFeedHandler extends BaseFeedHandler<LineStatusFeed> {
	LineStatusFeed m_root = new LineStatusFeed();
	net.twisterrob.blt.model.LineStatus m_lineStatus;

	@Override
	public LineStatusFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement(Root.NS, Root.ELEMENT);
		Element lineStatusElement = root.getChild(Root.NS, LineStatus.ELEMENT);
		Element lineStatusLineElement = lineStatusElement.getChild(Root.NS, Line.ELEMENT);
		Element lineStatusStatusElement = lineStatusElement.getChild(Root.NS, Status.ELEMENT);

		root.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				m_root = new LineStatusFeed();
			}
		});
		lineStatusElement.setElementListener(new ElementListener() {
			@Override
			public void start(Attributes attributes) {
				String attrStatusDetails = attributes.getValue(LineStatus.statusDetails);
				m_lineStatus = new net.twisterrob.blt.model.LineStatus();
				m_lineStatus.setDescription(attrStatusDetails);
			}
			@Override
			public void end() {
				m_root.addLineStatus(m_lineStatus);
				m_lineStatus = null;
			}
		});
		lineStatusLineElement.setStartElementListener(new StartElementListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void start(Attributes attributes) {
				String attrName = attributes.getValue(Line.name);

				net.twisterrob.blt.model.Line line = net.twisterrob.blt.model.Line.fromAlias(attrName);
				if (line == net.twisterrob.blt.model.Line.unknown && attrName != null) {
					sendMail(net.twisterrob.blt.model.Line.class + " new alias: " + attrName);
				}
				m_lineStatus.setLine(line);
			}
		});
		lineStatusStatusElement.setStartElementListener(new StartElementListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void start(Attributes attributes) {
				String attrId = attributes.getValue(Status.id);
				DelayType statusType = DelayType.fromID(attrId);
				if (statusType == DelayType.Unknown && attrId != null) {
					String attrDescription = attributes.getValue(Status.description);
					sendMail(DelayType.class + " new code: " + attrDescription + " as " + attrId);
				}
				String attrIsActive = attributes.getValue(Status.id);
				boolean isActive = Boolean.parseBoolean(attrIsActive);
				m_lineStatus.setType(statusType);
				m_lineStatus.setActive(isActive);
			}
		});

		Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		m_root.postProcess();
		return m_root;
	}
}
