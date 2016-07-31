package net.twisterrob.blt.gapp.viewmodel;

import org.junit.*;

import static com.shazam.gwen.Gwen.*;

import net.twisterrob.blt.io.feeds.trackernet.model.DelayType;
import net.twisterrob.blt.model.Line;

import static net.twisterrob.blt.gapp.viewmodel.ResultChange.*;

public class ResultChangeTest_Delays {
	private GwenStatus status1;
	private GwenStatus status2;
	private GwenChange change;

	@Before public void setUp() {
		status1 = new GwenStatus();
		status2 = new GwenStatus();
		change = new GwenChange();
	}

	@Test public void testDelayWorse() {
		given(status1).contains(Line.Northern, DelayType.GoodService);
		given(status2).contains(Line.Northern, DelayType.Suspended);

		when(change).between(status1, status2);

		then(change)
				.has(Line.Northern, StatusChange.Worse)
				.hasNoErrorChange()
				.hasNoDescriptionChangeFor(Line.Northern)
		;
	}

	@Test public void testDelayNotMixedUp() {
		given(status1)
				.contains(Line.Northern, DelayType.GoodService)
				.contains(Line.Jubilee, DelayType.Suspended)
		;
		given(status2)
				.contains(Line.Jubilee, DelayType.GoodService)
				.contains(Line.Northern, DelayType.Suspended)
		;

		when(change).between(status1, status2);

		then(change)
				.has(Line.Northern, StatusChange.Worse)
				.has(Line.Jubilee, StatusChange.Better)
				.hasNoErrorChange()
				.hasNoDescriptionChangeFor(Line.Northern, Line.Jubilee)
		;
	}

	@Test public void testDelayLineAppeared() {
		given(status1).doesNotContain(Line.Jubilee);
		given(status2).contains(Line.Jubilee, DelayType.GoodService);

		when(change).between(status1, status2);

		then(change)
				.has(Line.Jubilee, StatusChange.Unknown)
				.hasNoErrorChange()
				.hasNoDescriptionChangeFor(Line.Northern, Line.Jubilee)
		;
	}

	@Test public void testDelayLineDisappeared() {
		given(status1).contains(Line.Jubilee, DelayType.GoodService);
		given(status2).doesNotContain(Line.Jubilee);

		when(change).between(status1, status2);

		then(change)
				.has(Line.Jubilee, StatusChange.Unknown)
				.hasNoErrorChange()
				.hasNoDescriptionChangeFor(Line.Northern, Line.Jubilee)
		;
	}
}
