package net.twisterrob.blt.io.feeds.trackernet;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus;
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus.BranchStatus;
import net.twisterrob.blt.model.DelayType;
import net.twisterrob.blt.model.Line;

public class LineStatusFeedHandlerTest {

	private final LineStatusFeedHandler sut = new LineStatusFeedHandler();

	@Test public void test() throws IOException, SAXException {
		LineStatusFeed result = sut.parse(input("LineStatusFeedHandlerTest.xml"));

		assertThat(result, notNullValue());
		assertThat(result.getLineStatuses(), hasSize(3));

		LineStatus status1 = result.getLineStatuses().get(0);
		assertThat(status1, notNullValue());
		assertThat(status1.getDescription(), equalTo("Desc1"));
		assertThat(status1.getLine(), equalTo(Line.Bakerloo));
		assertThat(status1.getType(), equalTo(DelayType.SpecialService));
		assertThat(status1.getBranchStatuses(), hasSize(0));

		LineStatus status2 = result.getLineStatuses().get(1);
		assertThat(status2.getDescription(), equalTo("Desc2"));
		assertThat(status2.getLine(), equalTo(Line.Central));
		assertThat(status2.getType(), equalTo(DelayType.SpecialService));
		assertThat(status2.getBranchStatuses(), hasSize(1));
		BranchStatus status2Branch1 = status2.getBranchStatuses().get(0);
		assertThat(status2Branch1.getFromStation(), equalTo("Stratford"));
		assertThat(status2Branch1.getToStation(), equalTo("Uxbridge"));

		LineStatus status3 = result.getLineStatuses().get(2);
		assertThat(status3.getDescription(), equalTo("Desc3"));
		assertThat(status3.getLine(), equalTo(Line.District));
		assertThat(status3.getType(), equalTo(DelayType.SpecialService));
		assertThat(status3.getBranchStatuses(), hasSize(3));
		BranchStatus status3Branch1 = status3.getBranchStatuses().get(0);
		assertThat(status3Branch1.getFromStation(), equalTo("Tower Hill"));
		assertThat(status3Branch1.getToStation(), equalTo("Richmond"));
		BranchStatus status3Branch2 = status3.getBranchStatuses().get(1);
		assertThat(status3Branch2.getFromStation(), equalTo("Baker Street"));
		assertThat(status3Branch2.getToStation(), equalTo("Ealing Broadway"));
		BranchStatus status3Branch3 = status3.getBranchStatuses().get(2);
		assertThat(status3Branch3.getFromStation(), equalTo("Upminster"));
		assertThat(status3Branch3.getToStation(), equalTo("Cheshunt"));
	}

	private static InputStream input(String fileName) {
		return LineStatusFeedHandlerTest.class.getResourceAsStream(fileName);
	}
}
