package net.twisterrob.blt.gapp.viewmodel;

import org.junit.*;

import static com.shazam.gwen.Gwen.*;

import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus.BranchStatus;
import net.twisterrob.blt.model.Line;

import static net.twisterrob.blt.gapp.viewmodel.ResultChange.*;

public class ResultChangeTest_Descriptions {
	public static final String MISSING_DESCRIPTION = null;

	private GwenStatus status1;
	private GwenStatus status2;
	private GwenChange change;

	@Before public void setUp() {
		status1 = new GwenStatus();
		status2 = new GwenStatus();
		change = new GwenChange();
	}

	@Test public void testDescriptionChanged() {
		given(status1).contains(Line.Northern, "old description");
		given(status2).contains(Line.Northern, "new description");

		when(change).between(status1, status2);

		then(change)
				.has(Line.Northern, StatusChange.SameDescriptionChange)
				.hasNoErrorChange()
				.hasDescriptionChangeFor(Line.Northern)
		;
	}

	@Test public void testDescriptionAdded() {
		given(status1).contains(Line.Northern, MISSING_DESCRIPTION);
		given(status2).contains(Line.Northern, "new description");

		when(change).between(status1, status2);

		then(change)
				.has(Line.Northern, StatusChange.SameDescriptionAdd)
				.hasNoErrorChange()
				.hasNoDescriptionChangeFor(Line.Northern)
		;
	}

	@Test public void testDescriptionRemoved() {
		given(status1).contains(Line.Northern, "old description");
		given(status2).contains(Line.Northern, MISSING_DESCRIPTION);

		when(change).between(status1, status2);

		then(change)
				.has(Line.Northern, StatusChange.SameDescriptionDel)
				.hasNoErrorChange()
				.hasNoDescriptionChangeFor(Line.Northern)
		;
	}

	@Test public void testDescriptionSame() {
		given(status1).contains(Line.Northern, "description");
		given(status2).contains(Line.Northern, "description");

		when(change).between(status1, status2);

		then(change)
				.has(Line.Northern, StatusChange.SameDescriptionSame)
				.hasNoErrorChange()
				.hasNoDescriptionChangeFor(Line.Northern)
		;
	}

	@Test public void testDescriptionsMissing() {
		given(status1).contains(Line.Northern, MISSING_DESCRIPTION);
		given(status2).contains(Line.Northern, MISSING_DESCRIPTION);

		when(change).between(status1, status2);

		then(change)
				.has(Line.Northern, StatusChange.SameDescriptionSame)
				.hasNoErrorChange()
				.hasNoDescriptionChangeFor(Line.Northern)
		;
	}

	@Test public void testStationsDifferent() {
		given(status1).contains(Line.Northern, "description", new BranchStatus("from1", "to1"));
		given(status2).contains(Line.Northern, "description", new BranchStatus("from1", "to2"));

		when(change).between(status1, status2);

		then(change)
				.has(Line.Northern, StatusChange.BranchesChange)
				.hasNoErrorChange()
				.hasDescriptionChangeFor(Line.Northern)
		;
	}
}
