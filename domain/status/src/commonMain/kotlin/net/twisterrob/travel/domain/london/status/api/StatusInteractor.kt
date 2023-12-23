package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.StatusItem

interface StatusInteractor {

	fun getCurrent(): StatusItem
}
