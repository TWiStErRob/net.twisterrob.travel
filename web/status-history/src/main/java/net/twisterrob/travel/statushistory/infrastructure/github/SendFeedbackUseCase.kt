package net.twisterrob.travel.statushistory.infrastructure.github

interface SendFeedbackUseCase {
	fun report(title: String, body: String)
}
