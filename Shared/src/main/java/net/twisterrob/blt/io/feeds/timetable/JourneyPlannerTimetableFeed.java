package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

import net.twisterrob.blt.io.feeds.BaseFeed;
import net.twisterrob.blt.model.*;

public class JourneyPlannerTimetableFeed extends BaseFeed<JourneyPlannerTimetableFeed> {
	Line line;
	Operator operator;
	List<Route> routes = new ArrayList<>();
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

	@Override
	public JourneyPlannerTimetableFeed merge(JourneyPlannerTimetableFeed other) {
		if (this.line != other.line) {
			throw new IllegalArgumentException("Can't merge different lines.");
		}
		JourneyPlannerTimetableFeed newFeed = new JourneyPlannerTimetableFeed();
		newFeed.setLine(line);
		newFeed.setOperator(operator);
		newFeed.routes.addAll(this.routes);
		newFeed.routes.addAll(other.routes);
		newFeed.postProcess();
		return newFeed;
	}

	public List<Route> getRoutes() {
		return Collections.unmodifiableList(routes);
	}
	protected void addRoute(Route route) {
		routes.add(route);
	}

	public static Set<StopPoint> getStopPoints(List<Route> routes) {
		Set<StopPoint> stopPoints = new TreeSet<>(new Comparator<StopPoint>() {
			@Override
			public int compare(StopPoint o1, StopPoint o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		for (Route route: routes) {
			for (RouteSection section: route.getSections()) {
				for (RouteLink link: section.getLinks()) {
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
