package net.twisterrob.blt.data.algo.postcodes;

import java.util.*;

import net.twisterrob.java.model.Location;

public class ConvexHull {
	private static final double DELTA = 1e-8;
	/**
	 * http://en.wikipedia.org/wiki/Gift_wrapping_algorithm#Pseudocode
	 */
	public static <T> List<T> convexHull(final Iterable<? extends T> S, final ToPos<T> poser) {
		T pointOnHull = getLeftMost(S, poser);
		ArrayList<T> P = new ArrayList<>();
		T first = S.iterator().next();
		T endpoint;
		do {
			endpoint = first; // initial endpoint for a candidate edge on the hull
			for (T SgetJ : S) {
				double endPointX = poser.getX(endpoint);
				double endPointY = poser.getY(endpoint);
				double pointOnHullX = poser.getX(pointOnHull);
				double pointOnHullY = poser.getY(pointOnHull);
				if (eq(endPointX, endPointY, pointOnHullX, pointOnHullY)
						|| isLeftOf(
						pointOnHullX, pointOnHullY,
						endPointX, endPointY,
						poser.getX(SgetJ), poser.getY(SgetJ))) {
					endpoint = SgetJ; // found greater left turn, update endpoint
				}
			}
			P.add(pointOnHull);
			pointOnHull = endpoint;
			// repeat while not wrapped around to first hull point
		} while (!eq(poser.getX(endpoint), poser.getY(endpoint), poser.getX(P.get(0)), poser.getY(P.get(0))));
		return P;
	}

	private static boolean eq(double aX, double aY, double bX, double bY) {
		return eq(aX, bX) && eq(aY, bY);
	}

	private static boolean eq(double a, double b) {
		return Math.abs(a - b) < DELTA;
	}

	public static <T> Location center(final Iterable<? extends T> S, final ToPos<T> poser) {
		int size = 0;
		double xSum = 0;
		double ySum = 0;
		for (T l : S) {
			size++;
			xSum += poser.getX(l);
			ySum += poser.getY(l);
		}
		xSum /= size;
		ySum /= size;
		return new Location(ySum, xSum);
	}

	/**
	 * @param a start
	 * @param b end
	 * @param c point
	 */
	public static boolean isLeftOf(double aX, double aY, double bX, double bY, double cX, double cY) {
		return ((bX - aX) * (cY - aY) - (bY - aY) * (cX - aX)) > 0;
	}

	public static <T> T getLeftMost(final Iterable<? extends T> S, ToPos<T> poser) {
		Iterator<? extends T> iterator = S.iterator();
		T leftMost = iterator.next();
		while (iterator.hasNext()) {
			T postCoord = iterator.next();
			if (poser.getX(postCoord) < poser.getX(leftMost)) {
				leftMost = postCoord;
			} else if (eq(poser.getX(postCoord), poser.getX(leftMost))) {
				if (poser.getY(postCoord) > poser.getY(leftMost)) {
					leftMost = postCoord;
				}
			}
		}
		return leftMost;
	}

	public static interface ToPos<T> {
		double getX(T obj);
		double getY(T obj);
	}
}
