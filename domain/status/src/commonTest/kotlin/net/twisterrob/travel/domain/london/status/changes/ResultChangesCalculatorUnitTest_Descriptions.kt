package net.twisterrob.travel.domain.london.status.changes

import com.shazam.gwen.Gwen.given
import com.shazam.gwen.Gwen.then
import com.shazam.gwen.Gwen.`when`
import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineStatus
import kotlin.test.BeforeTest
import kotlin.test.Test

private val MISSING_DESCRIPTION: String? = null

/**
 * @see ResultChangesCalculator
 */
class ResultChangesCalculatorUnitTest_Descriptions {

	private lateinit var status1: GwenStatus
	private lateinit var status2: GwenStatus
	private lateinit var change: GwenChange

	@BeforeTest fun setUp() {
		status1 = GwenStatus()
		status2 = GwenStatus()
		change = GwenChange()
	}

	@Test fun testDescriptionChanged() {
		given(status1).contains(Line.Northern, "old description")
		given(status2).contains(Line.Northern, "new description")

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Northern, DescriptionChange.Changed("old description", "new description"))
			.hasDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDescriptionAdded() {
		given(status1).contains(Line.Northern, MISSING_DESCRIPTION)
		given(status2).contains(Line.Northern, "new description")

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Northern, DescriptionChange.Added("new description"))
			.hasDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDescriptionRemoved() {
		given(status1).contains(Line.Northern, "old description")
		given(status2).contains(Line.Northern, MISSING_DESCRIPTION)

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Northern, DescriptionChange.Removed("old description"))
			.hasDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDescriptionSame() {
		given(status1).contains(Line.Northern, "description")
		given(status2).contains(Line.Northern, "description")

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Northern, DescriptionChange.Same("description"))
			.hasDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDescriptionsMissing() {
		given(status1).contains(Line.Northern, MISSING_DESCRIPTION)
		given(status2).contains(Line.Northern, MISSING_DESCRIPTION)

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Northern, DescriptionChange.Missing)
			.hasDescriptionChangeFor(Line.Northern)
	}

	@Test fun testStationsDifferent() {
		val branch1 = LineStatus.BranchStatus("from1", "to1")
		val branch2 = LineStatus.BranchStatus("from2", "to2")
		given(status1).contains(Line.Northern, "description", branch1)
		given(status2).contains(Line.Northern, "description", branch2)

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Northern, DescriptionChange.Branches(listOf(branch1), listOf(branch2)))
			.hasDescriptionChangeFor(Line.Northern)
	}
}
