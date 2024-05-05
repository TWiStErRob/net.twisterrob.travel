package net.twisterrob.travel.statushistory.infrastructure.feedback

interface SendFeedbackUseCase {
	fun report(title: String, body: String)
}
