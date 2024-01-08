package net.twisterrob.travel.domain.london.status

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusDataSource

@Suppress("unused") // Used by mockative KSP.
object Mocks {

	@Mock
	private val statusHistoryDataSource = mock(classOf<StatusHistoryDataSource>())

	@Mock
	private val statusDataSource = mock(classOf<StatusDataSource>())

	@Mock
	private val feedParser = mock(classOf<FeedParser>())
}
