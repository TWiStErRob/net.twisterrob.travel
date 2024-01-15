package net.twisterrob.travel.statushistory.viewmodel

import com.shazam.gwen.Gwen.given
import com.shazam.gwen.Gwen.then
import com.shazam.gwen.Gwen.`when`
import net.twisterrob.blt.model.DelayType
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.DescriptionChange.Missing
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResultChangeCalculatorUnitTest_Delays {

	private lateinit var status1: GwenStatus
	private lateinit var status2: GwenStatus
	private lateinit var change: GwenChange

	@BeforeEach fun setUp() {
		status1 = GwenStatus()
		status2 = GwenStatus()
		change = GwenChange()
	}

	@Test fun testDelayWorse() {
		given(status1).contains(Line.Northern, DelayType.GoodService)
		given(status2).contains(Line.Northern, DelayType.Suspended)

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Northern, StatusChange.Worse(DelayType.GoodService, DelayType.Suspended, Missing))
			.hasNoDescriptionChangeFor(Line.Northern)
	}

	@Test fun testDelayNotMixedUp() {
		given(status1)
			.contains(Line.Northern, DelayType.GoodService)
			.contains(Line.Jubilee, DelayType.Suspended)

		given(status2)
			.contains(Line.Jubilee, DelayType.GoodService)
			.contains(Line.Northern, DelayType.Suspended)

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Northern, StatusChange.Worse(DelayType.GoodService, DelayType.Suspended, Missing))
			.has(Line.Jubilee, StatusChange.Better(DelayType.Suspended, DelayType.GoodService, Missing))
			.hasNoDescriptionChangeFor(Line.Northern, Line.Jubilee)
	}

	@Test fun testDelayLineAppeared() {
		given(status1).doesNotContain(Line.Jubilee)
		given(status2).contains(Line.Jubilee, DelayType.GoodService)

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Jubilee, StatusChange.Appeared(DelayType.GoodService))
			.hasNoDescriptionChangeFor(Line.Northern, Line.Jubilee)
	}

	@Test fun testDelayLineDisappeared() {
		given(status1).contains(Line.Jubilee, DelayType.GoodService)
		given(status2).doesNotContain(Line.Jubilee)

		`when`(change).between(status1, status2)

		then(change)
			.hasNoErrorChange()
			.has(Line.Jubilee, StatusChange.Disappeared(DelayType.GoodService))
			.hasNoDescriptionChangeFor(Line.Northern, Line.Jubilee)
	}
}
