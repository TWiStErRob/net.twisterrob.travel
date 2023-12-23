package net.twisterrob.travel.domain.london.status

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusInteractor

@Suppress("unused") // Used by mockative KSP.
object Mocks {

	@Mock
	private val repo = mock(classOf<StatusHistoryRepository>())

	@Mock
	private val interactor = mock(classOf<StatusInteractor>())
}
