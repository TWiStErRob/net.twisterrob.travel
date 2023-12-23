package net.twisterrob.travel.domain.london.status

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusInteractor

@Suppress("unused") // Used by mockative KSP.
object Mocks {

	@Mock
	private val statusHistoryRepository = mock(classOf<StatusHistoryRepository>())

	@Mock
	private val statusInteractor = mock(classOf<StatusInteractor>())

	@Mock
	private val feedParser = mock(classOf<FeedParser>())
}
