package net.twisterrob.travel.domain.london.status.changes

import com.shazam.gwen.collaborators.Actor
import com.shazam.gwen.collaborators.Asserter
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.compose.any
import io.kotest.matchers.maps.contain
import io.kotest.matchers.maps.haveKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.should
import io.kotest.matchers.shouldHave
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.instanceOf
import net.twisterrob.blt.model.DelayType
import net.twisterrob.blt.model.Line

internal class GwenChange : Actor, Asserter {

	private lateinit var changes: Changes

	fun between(status1: GwenStatus, status2: GwenStatus) {
		changes = ResultChangesCalculator().diff(status1.createResult(), status2.createResult())
	}

	fun has(line: Line, desc: DescriptionChange): GwenChange = apply {
		changes.status should contain(line, StatusChange.Same(DelayType.Unknown, desc))
	}

	fun has(line: Line, status: StatusChange): GwenChange = apply {
		changes.status should contain(line, status)
	}

	fun hasNoStatusChangeFor(vararg lines: Line): GwenChange = apply {
		lines.forNone { line ->
			changes.status shouldNotContainKey line
		}
	}

	fun hasNoDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		lines.forAll { line ->
			changes.status shouldHave Matcher.any(
				haveKey(line).invert(),
				containMatching(line, instanceOf<HasDescriptionChange>().invert())
			)
		}
	}

	fun hasDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			changes.status should containMatching(line, instanceOf<HasDescriptionChange>())
		}
	}

	fun hasNoErrorChange(): GwenChange = apply {
		changes should beInstanceOf<Changes.Status>()
	}
}

private val Changes.status: Map<Line, StatusChange>
	get() = (this as Changes.Status).changes

/**
 * Smarter version of `contain`.
 * @see io.kotest.matchers.maps.contain
 */
private fun <K, V> containMatching(key: K, v: Matcher<V>): Matcher<Map<K, V>> =
	object : Matcher<Map<K, V>> {
		override fun test(value: Map<K, V>) = MatcherResult(
			value[key]?.let { v.test(it).passed() } ?: false,
			{ "Map should contain mapping $key=$v but was ${buildActualValue(value)}" },
			{ "Map should not contain mapping $key=$v but was $value" }
		)

		private fun buildActualValue(map: Map<K, V>) = map[key]?.let { "$key=$it" } ?: map
	}
