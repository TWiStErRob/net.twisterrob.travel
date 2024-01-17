package net.twisterrob.blt.model

/**
 * Possible statuses of the service a tube line.<br></br>
 * The names also represent the `CssClass` in the TrackerNet Line Status feed, e.g.:
 * ```xml
 * <Status ID="GS" CssClass="GoodService" Description="Good Service" IsActive="true">
 * ```
 *
 * See [Current status](http://cloud.tfl.gov.uk/TrackerNet/LineStatus).
 * @see net.twisterrob.blt.io.feeds.Feed.TubeDepartureBoardsLineStatus
 * @see net.twisterrob.blt.io.feeds.Feed.TubeDepartureBoardsLineStatusIncidents
 */
enum class DelayType {

	Unknown,

	/**
	 * Example: N/A, there's no description if everything works.
	 */
	GoodService,

	/**
	 * Example:
	 * > Between Kennington and Camden Town via Bank only, due to a person ill on a train earlier at Angel.
	 * > GOOD SERVICE on the rest of the line.
	 */
	MinorDelays,

	/**
	 * Example: TODO
	 */
	BusService,

	/**
	 * Example: TODO
	 */
	ReducedService,

	/**
	 * Example:
	 * > Between Turnham Green and Richmond only, due to a signal failure at Richmond.
	 * > GOOD SERVICE on the rest of the line.
	 */
	SevereDelays,

	/**
	 * Example (during the 2020 March Lockdown due to COVID-19 outbreak):
	 * > A 15 minute service is operating due to operational restrictions.
	 * > Public transport should only be used for essential journeys.
	 */
	SpecialService,

	/**
	 * Example:
	 * > No service between Wembley Park and Aldgate due to planned engineering work.
	 * > GOOD SERVICE on the rest of the line.
	 */
	PartClosure,

	/**
	 * Example:
	 * > Train service resumes later this morning.
	 */
	PlannedClosure,

	/**
	 * Example:
	 * > No service between Richmond and Stratford and between Willesden Junction and Clapham Junction due to
	 * > a faulty train. London Underground and London Buses are accepting tickets via reasonable routes.
	 */
	PartSuspended,

	/**
	 * Example: TODO
	 */
	Suspended,

	/**
	 * Example:
	 * > The Central line service has closed early to enable us to begin repairs following this evening's disruption.
	 * > We are sorry for the delay to your journey.
	 */
	ServiceClosed,
	;
}
