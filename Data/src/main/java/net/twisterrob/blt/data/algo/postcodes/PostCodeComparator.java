package net.twisterrob.blt.data.algo.postcodes;

import java.util.Comparator;

public class PostCodeComparator implements Comparator<PostCode> {
	public int compare(final PostCode lhs, final PostCode rhs) {
		double x1 = lhs.getLocation().getLongitude();
		double x2 = rhs.getLocation().getLongitude();
		if (x1 < x2) {
			return -1;
		} else if (x1 > x2) {
			return 1;
		} else {
			double y1 = lhs.getLocation().getLatitude();
			double y2 = rhs.getLocation().getLatitude();
			if (y1 < y2) {
				return -1;
			} else if (y1 > y2) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
