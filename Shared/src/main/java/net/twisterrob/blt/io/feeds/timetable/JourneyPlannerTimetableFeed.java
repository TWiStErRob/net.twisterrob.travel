package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

import javax.annotation.Nonnull;

import net.twisterrob.blt.io.feeds.BaseFeed;
import net.twisterrob.blt.model.*;

public class JourneyPlannerTimetableFeed extends BaseFeed {
	Line line;
	Operator operator;
	@Nonnull List<Route> routes = new ArrayList<Route>();
	private static final RouteFixer[] FIXERS = {new DLRWestferry2CanaryWharfFixer(),
			new DLRPuddingMillLane2StratfordFixer(), new ReverseLinkDistanceFixer(),
			new MostSimilarLinkDistanceFixer(), new CollapseRoutesFixer()};

	public Line getLine() {
		return line;
	}
	void setLine(Line line) {
		this.line = line;
	}
	public Operator getOperator() {
		return operator;
	}
	void setOperator(Operator operator) {
		this.operator = operator;
	}

	@SuppressWarnings("null")
	@Nonnull
	public List<Route> getRoutes() {
		return Collections.unmodifiableList(routes);
	}
	protected void addRoute(@Nonnull Route route) {
		routes.add(route);
	}

	@Nonnull
	public static Set<StopPoint> getStopPoints(@Nonnull List<Route> routes) {
		Set<StopPoint> stopPoints = new TreeSet<StopPoint>(new Comparator<StopPoint>() {
			@Override
			public int compare(@Nonnull StopPoint o1, @Nonnull StopPoint o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		for (Route route: routes) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					stopPoints.add(link.getFrom());
					stopPoints.add(link.getTo());
				}
			}
		}
		return stopPoints;
	}

	@Override
	protected void postProcess() {
		super.postProcess();
		for (RouteFixer fixer: FIXERS) {
			fix(fixer);
		}
	}

	static interface RouteFixer {
		public boolean matches(JourneyPlannerTimetableFeed feed);
		public void fix(JourneyPlannerTimetableFeed feed);
	}

	public void fix(RouteFixer fixer) {
		if (fixer.matches(this)) {
			fixer.fix(this);
		}
	}
}
