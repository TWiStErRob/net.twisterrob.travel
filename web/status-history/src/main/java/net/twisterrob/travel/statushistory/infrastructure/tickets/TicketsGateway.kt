package net.twisterrob.travel.statushistory.infrastructure.tickets

interface TicketsGateway {
	fun searchTicketsWithTitle(title: String): List<Ticket>
	fun createTicket(title: String, body: String): Ticket
}
