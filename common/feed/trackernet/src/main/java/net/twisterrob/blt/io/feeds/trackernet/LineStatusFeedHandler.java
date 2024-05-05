package net.twisterrob.blt.io.feeds.trackernet;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.ElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import net.twisterrob.blt.io.feeds.BaseFeedHandler;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.BranchDisruption;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.BranchDisruption.StationFrom;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.BranchDisruption.StationTo;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.BranchDisruptions;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.Line;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.LineStatus;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.Root;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeedXml.Status;
import net.twisterrob.blt.model.DelayType;

@NotThreadSafe
public class LineStatusFeedHandler extends BaseFeedHandler<LineStatusFeed> {

	private final @Nonnull TrackerNetData trackerNetData = new TrackerNetData();

	LineStatusFeed m_root = new LineStatusFeed();
	net.twisterrob.blt.io.feeds.trackernet.model.LineStatus m_lineStatus;
	net.twisterrob.blt.io.feeds.trackernet.model.LineStatus.BranchStatus m_branch;

	@Override public LineStatusFeed parse(InputStream is) throws IOException, SAXException {
		RootElement root = new RootElement(Root.NS, Root.ELEMENT);
		Element lineStatusElement = root.getChild(Root.NS, LineStatus.ELEMENT);
		Element lineStatusLineElement = lineStatusElement.getChild(Root.NS, Line.ELEMENT);
		Element lineStatusStatusElement = lineStatusElement.getChild(Root.NS, Status.ELEMENT);
		Element branchesElement = lineStatusElement.getChild(Root.NS, BranchDisruptions.ELEMENT);
		Element branchElement = branchesElement.getChild(Root.NS, BranchDisruption.ELEMENT);
		Element branchFromElement = branchElement.getChild(Root.NS, BranchDisruption.StationFrom.ELEMENT);
		Element branchToElement = branchElement.getChild(Root.NS, BranchDisruption.StationTo.ELEMENT);

		root.setStartElementListener(new StartElementListener() {
			@Override public void start(Attributes attributes) {
				m_root = new LineStatusFeed();
			}
		});
		lineStatusElement.setElementListener(new ElementListener() {
			@Override public void start(Attributes attributes) {
				String attrStatusDetails = attributes.getValue(LineStatus.statusDetails);
				m_lineStatus = new net.twisterrob.blt.io.feeds.trackernet.model.LineStatus();
				m_lineStatus.setDescription(attrStatusDetails);
			}
			@Override public void end() {
				m_root.addLineStatus(m_lineStatus);
				m_lineStatus = null;
			}
		});
		lineStatusLineElement.setStartElementListener(new StartElementListener() {
			@SuppressWarnings("synthetic-access")
			@Override public void start(Attributes attributes) {
				String attrName = attributes.getValue(Line.name);

				net.twisterrob.blt.model.Line line = net.twisterrob.blt.model.Line.unknown;
				if (attrName != null) {
					line = trackerNetData.fromAlias(attrName);
					if (line == net.twisterrob.blt.model.Line.unknown) {
						sendFeedback(
								"New line alias: " + attrName,
								net.twisterrob.blt.model.Line.class + " new alias: " + attrName
						);
					}
				}
				m_lineStatus.setLine(line);
			}
		});
		lineStatusStatusElement.setStartElementListener(new StartElementListener() {
			@SuppressWarnings("synthetic-access")
			@Override public void start(Attributes attributes) {
				String attrId = attributes.getValue(Status.id);
				DelayType statusType = DelayType.Unknown;
				if (attrId != null) {
					statusType = trackerNetData.delayFromTrackerNetCode(attrId);
					if (statusType == DelayType.Unknown) {
						String attrDescription = attributes.getValue(Status.description);
						sendFeedback(
								"New delay type:" + attrId,
								DelayType.class + " new code: " + attrDescription + " as " + attrId
						);
					}
				}
				String attrIsActive = attributes.getValue(Status.id);
				boolean isActive = Boolean.parseBoolean(attrIsActive);
				m_lineStatus.setType(statusType);
				m_lineStatus.setActive(isActive);
			}
		});
		branchElement.setElementListener(new ElementListener() {
			@Override public void start(Attributes attributes) {
				m_branch = new net.twisterrob.blt.io.feeds.trackernet.model.LineStatus.BranchStatus();
			}
			@Override public void end() {
				m_lineStatus.addBranchStatus(m_branch);
				m_branch = null;
			}
		});
		branchFromElement.setStartElementListener(new StartElementListener() {
			@Override public void start(Attributes attributes) {
				m_branch.setFromStation(attributes.getValue(StationFrom.name));
			}
		});
		branchToElement.setStartElementListener(new StartElementListener() {
			@Override public void start(Attributes attributes) {
				m_branch.setToStation(attributes.getValue(StationTo.name));
			}
		});

		Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		m_root.postProcess();
		return m_root;
	}
}
