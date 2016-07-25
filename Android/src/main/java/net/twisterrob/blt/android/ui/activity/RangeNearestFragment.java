package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import static java.util.concurrent.TimeUnit.*;

import org.slf4j.*;

import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;

import com.google.android.gms.maps.model.LatLng;

import net.twisterrob.android.utils.concurrent.SimpleSafeAsyncTask;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tostring.stringers.name.AddressNameStringer;
import net.twisterrob.android.view.AutomatedViewSwitcher;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.data.range.RangeMapGeneratorConfig;
import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder.DescriptionFormatter;
import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.Location;
import net.twisterrob.java.utils.tostring.*;

public class RangeNearestFragment extends Fragment {
	private static final Logger LOG = LoggerFactory.getLogger(RangeNearestFragment.class);
	private TextSwitcher droppedPin;
	private AutomatedViewSwitcher droppedPinAutomation;
	private ViewGroup nearestStations;
	private LatLng lastStartPoint;
	private GeocoderTask m_geocoderTask;
	@Override public @Nullable View onCreateView(
			LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_range_nearest, container, false);
	}
	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		droppedPin = (TextSwitcher)view.findViewById(R.id.view$range$dropped_pin);
		droppedPin.setInAnimation(getContext(), android.R.anim.fade_in);
		droppedPin.setOutAnimation(getContext(), android.R.anim.fade_out);
		droppedPinAutomation = new AutomatedViewSwitcher(droppedPin, SECONDS.toMillis(3), SECONDS.toMillis(15));
		nearestStations = (ViewGroup)view.findViewById(R.id.view$range$nearest_stations);
		nearestStations.removeAllViews();
	}
	@Override public void onDestroyView() {
		super.onDestroyView();
		if (m_geocoderTask != null) {
			m_geocoderTask.cancel(true);
			m_geocoderTask = null;
		}
	}
	public void updateLocation(@NonNull LatLng latlng, @Nullable Address address) {
		ToStringer addressString = new ToStringer(StringerRepo.INSTANCE, address, new AddressNameStringer());
		LOG.trace("updateLocation: {}, address: {}, task {}",
				latlng, addressString, AndroidTools.toString(m_geocoderTask));
		if (address == null) {
			if (m_geocoderTask != null) {
				m_geocoderTask.cancel(true);
				m_geocoderTask = null;
			}
			m_geocoderTask = new GeocoderTask(getContext());
			AndroidTools.executePreferParallel(m_geocoderTask, latlng);
		}
		updateLocationInternal(latlng, address);
	}
	private void updateLocationInternal(@NonNull LatLng latlng, @Nullable Address address) {
		lastStartPoint = latlng;
		LOG.trace("updateLocationInternal: {}, address: {}, task: {}",
				latlng, address != null? "<received>" : null, AndroidTools.toString(m_geocoderTask));
		String addressString = LocationUtils.getVagueAddress(address);
		String pin = getString(R.string.range$nearest_location, latlng.latitude, latlng.longitude);
		droppedPinAutomation.stop();
		droppedPin.setCurrentText(pin);
		if (addressString != null) {
			droppedPin.setText(addressString);
			droppedPinAutomation.start();
		}
	}
	public void updateNearestStations(Collection<NetworkNode> startNodes, final RangeMapGeneratorConfig config) {
		nearestStations.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getContext());
		DescriptionFormatter formatter = new DescriptionFormatter() {
			@Override public CharSequence format(Station station) {
				double meters = ((StationWithDistance)station).getDistance();
				double minutes = config.walk(meters);
				double halfMinutes = Math.round(minutes * 2) / 2.0; // round to nearest half
				return String.format(Locale.getDefault(),
						"%s station " + (halfMinutes % 1.0 != .0? "%.1f" : "%.0f") + " min / %d m away",
						station.getType(), halfMinutes, Math.round(meters));
			}
		};
		for (StationWithDistance station : toStations(startNodes)) {
			LOG.trace("Creating nearest station for {}", station);
			View view = inflater.inflate(R.layout.item_station, nearestStations, false);
			new StationAdapter.ViewHolder(view, formatter).bind(station, null);
			nearestStations.addView(view);
		}
		if (nearestStations.getChildCount() == 0) {
			TextView empty = new TextView(getContext());
			empty.setText(R.string.range$nearest_empty);
			nearestStations.addView(empty);
		}
	}
	private Collection<StationWithDistance> toStations(Collection<NetworkNode> nodes) {
		Map<String, StationWithDistance> stations = new HashMap<>();
		Location distanceReference = LocationUtils.fromLatLng(lastStartPoint);
		for (NetworkNode node : nodes) {
			String key = node.getName();
			StationWithDistance station = stations.get(key);
			if (station == null) {
				station = new StationWithDistance();
				station.setName(node.getName());
				station.setLocation(node.getLocation());
				station.setLines(new ArrayList<Line>());
				station.setDistance(LocationUtils.distance(distanceReference, station.getLocation()));
				stations.put(key, station);
			}
			station.getLines().add(node.getLine());
		}
		for (StationWithDistance station : stations.values()) {
			int[] stopCounts = new int[StopType.values().length];
			for (Line line : station.getLines()) {
				stopCounts[line.getDefaultStopType().ordinal()]++;
			}
			int max = 0;
			for (int i = max + 1; i < stopCounts.length; i++) {
				// picks the first if there's a tie
				if (stopCounts[max] < stopCounts[i]) {
					max = i;
				}
			}
			station.setType(StopType.values()[max]);
		}
		List<StationWithDistance> result = new ArrayList<>(stations.values());
		Collections.sort(result, new Comparator<StationWithDistance>() {
			@Override public int compare(StationWithDistance lhs, StationWithDistance rhs) {
				return Double.compare(lhs.getDistance(), rhs.getDistance());
			}
		});
		return result;
	}

	private final class GeocoderTask extends SimpleSafeAsyncTask<LatLng, Void, Address> {
		private final Geocoder geocoder;
		private GeocoderTask(Context context) {
			if (Geocoder.isPresent()) {
				geocoder = new Geocoder(context);
			} else {
				LOG.warn("Geocoding not available.");
				geocoder = null;
			}
		}
		@Override protected @Nullable Address doInBackground(LatLng latlng) throws Exception {
			if (geocoder != null) {
				List<Address> location = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
				if (location != null && !location.isEmpty()) {
					return location.get(0);
				}
			}
			return null;
		}
		@Override protected void onResult(@Nullable Address address, LatLng latlng) {
			updateLocationInternal(latlng, address);
		}
		@Override protected void onError(@NonNull Exception ex, LatLng latlng) {
			LOG.warn("Cannot determine location for {}", latlng, ex);
			updateLocationInternal(latlng, null);
		}
	}

	private static class StationWithDistance extends Station {
		private double distance;
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
	}
}
