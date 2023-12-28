package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import java.util.Date

class Result private constructor(
	val errorHeader: String?,
	val fullError: String?,
	val content: LineStatusFeed?,
	val `when`: Date,
) {

	constructor(`when`: Date, error: String) : this(
		`when` = `when`,
		content = null,
		errorHeader = error.substringBefore('\n'),
		fullError = error,
	)

	constructor(`when`: Date, content: LineStatusFeed?) : this(
		`when` = `when`,
		content = content,
		errorHeader = null,
		fullError = null,
	)
}
