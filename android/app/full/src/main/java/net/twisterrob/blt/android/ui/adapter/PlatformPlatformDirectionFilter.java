package net.twisterrob.blt.android.ui.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.widget.Filter;

import net.twisterrob.blt.io.feeds.trackernet.model.Platform;
import net.twisterrob.blt.model.PlatformDirection;

/**
 * @deprecated not used, just example
 */
@Deprecated
public abstract class PlatformPlatformDirectionFilter extends Filter {
	private Set<PlatformDirection> m_directions;

	public PlatformPlatformDirectionFilter() {
		this(Collections.<PlatformDirection>emptySet());
	}
	public PlatformPlatformDirectionFilter(Set<PlatformDirection> initialDirections) {
		m_directions = initialDirections;
	}

	@Override protected FilterResults performFiltering(CharSequence constraint) {
		List<Platform> platforms = getDataToFilter();
		List<Platform> result = getDataToFilter();

		for (Platform platform : platforms) {
			if (m_directions.contains(platform.getDirection())) {
				result.add(platform);
			}
		}

		FilterResults results = new FilterResults();
		results.values = result;
		results.count = result.size();
		return results;
	}

	protected abstract List<Platform> getDataToFilter();
	protected abstract void publishFilteredData(List<Platform> values);

	@SuppressWarnings("unchecked")
	@Override protected final void publishResults(CharSequence constraint, FilterResults results) {
		publishFilteredData((List<Platform>)results.values);
	}

	public void addDirection(PlatformDirection dir) {
		m_directions.add(dir);
	}
	public void removeDirection(PlatformDirection dir) {
		m_directions.remove(dir);
	}
}
