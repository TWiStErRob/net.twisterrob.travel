package net.twisterrob.travel.statushistory.viewmodel

import com.shazam.gwen.Gwen.given
import com.shazam.gwen.Gwen.then
import com.shazam.gwen.Gwen.`when`
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus.BranchStatus
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.StatusChange
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val MISSING_DESCRIPTION: String? = null

class ResultChangeUnitTest_Descriptions {

	private lateinit var status1: GwenStatus
	private lateinit var status2: GwenStatus
	private lateinit var change: GwenChange

	@BeforeEach fun setUp() {
		status1 = GwenStatus()
		status2 = GwenStatus()
		change = GwenChange()
	}

	@Test fun testDescriptionChanged() {
		given(status1).contains(Line.Northern, "old description")
		given(status2).contains(Line.Northern, "new description")

		`when`(change).between(status1, status2)

		then(change)
			.has(Line.Northern, StatusChange.SameDescriptionChange)
			.hasNoErrorChange()
			.hasDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDescriptionAdded() {
		given(status1).contains(Line.Northern, MISSING_DESCRIPTION)
		given(status2).contains(Line.Northern, "new description")

		`when`(change).between(status1, status2)

		then(change)
			.has(Line.Northern, StatusChange.SameDescriptionAdd)
			.hasNoErrorChange()
			.hasDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDescriptionRemoved() {
		given(status1).contains(Line.Northern, "old description")
		given(status2).contains(Line.Northern, MISSING_DESCRIPTION)

		`when`(change).between(status1, status2)

		then(change)
			.has(Line.Northern, StatusChange.SameDescriptionDel)
			.hasNoErrorChange()
			.hasDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDescriptionSame() {
		given(status1).contains(Line.Northern, "description")
		given(status2).contains(Line.Northern, "description")

		`when`(change).between(status1, status2)

		then(change)
			.has(Line.Northern, StatusChange.SameDescriptionSame)
			.hasNoErrorChange()
			.hasNoDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDescriptionsMissing() {
		given(status1).contains(Line.Northern, MISSING_DESCRIPTION)
		given(status2).contains(Line.Northern, MISSING_DESCRIPTION)

		`when`(change).between(status1, status2)

		then(change)
			.has(Line.Northern, StatusChange.SameDescriptionSame)
			.hasNoErrorChange()
			.hasNoDescriptionChangeFor(Line.Northern)
	}

	@Test fun testStationsDifferent() {
		given(status1).contains(Line.Northern, "description", BranchStatus("from1", "to1"))
		given(status2).contains(Line.Northern, "description", BranchStatus("from1", "to2"))

		`when`(change).between(status1, status2)

		then(change)
			.has(Line.Northern, StatusChange.BranchesChange)
			.hasNoErrorChange()
			.hasDescriptionChangeFor(Line.Northern)
	}
}