package net.twisterrob.travel.statushistory.infrastructure.feedback

import net.twisterrob.travel.statushistory.infrastructure.tickets.Ticket
import net.twisterrob.travel.statushistory.infrastructure.tickets.TicketsGateway
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class EnsureGitHubIssueInteractorUnitTest {
	private val gateway: TicketsGateway = mock()
	private val subject = EnsureGitHubIssueInteractor(gateway)

	@Test fun `issue is only created when there are no existing issues`() {
		val fixtTitle = "My Title"
		val fixtBody = "Body contents."
		whenever(gateway.searchTicketsWithTitle(fixtTitle))
			.thenReturn(emptyList())
		whenever(gateway.createTicket(fixtTitle, fixtBody))
			.thenReturn(Ticket("http://example.com/issue"))

		subject.report(fixtTitle, fixtBody)

		verify(gateway).createTicket(fixtTitle, fixtBody)
		verify(gateway).searchTicketsWithTitle(fixtTitle)
		verifyNoMoreInteractions(gateway)
	}

	@Test fun `issue is not created when there are existing issues`() {
		val fixtTitle = "My Title"
		val fixtBody = "Body contents."
		whenever(gateway.searchTicketsWithTitle(fixtTitle))
			.thenReturn(listOf(Ticket("http://example.com/issue")))

		subject.report(fixtTitle, fixtBody)

		verify(gateway).searchTicketsWithTitle(fixtTitle)
		verifyNoMoreInteractions(gateway)
	}

	@Test fun `issue is not created when it was already created`() {
		whenever(gateway.searchTicketsWithTitle("My Title 1"))
			.thenReturn(emptyList())
		whenever(gateway.createTicket("My Title 1", "Body contents 1."))
			.thenReturn(Ticket("http://example.com/issue"))

		whenever(gateway.searchTicketsWithTitle("My Title 2"))
			.thenReturn(emptyList())
		whenever(gateway.createTicket("My Title 2", "Body contents 2."))
			.thenReturn(Ticket("http://example.com/issue"))

		subject.report("My Title 1", "Body contents 1.")
		subject.report("My Title 2", "Body contents 2.")
		subject.report("My Title 2", "Body contents 3.")
		subject.report("My Title 1", "Body contents 4.")

		verify(gateway).createTicket("My Title 1", "Body contents 1.")
		verify(gateway).createTicket("My Title 2", "Body contents 2.")
		verify(gateway).searchTicketsWithTitle("My Title 1")
		verify(gateway).searchTicketsWithTitle("My Title 2")
		verifyNoMoreInteractions(gateway)
	}
}
