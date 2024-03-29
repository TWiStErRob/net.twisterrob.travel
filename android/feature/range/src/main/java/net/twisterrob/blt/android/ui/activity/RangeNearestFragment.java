package net.twisterrob.blt.android.ui.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.twisterrob.android.utils.concurrent.SimpleSafeAsyncTask;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tools.StringerTools;
import net.twisterrob.android.utils.tostring.stringers.name.AddressNameStringer;
import net.twisterrob.android.view.AutomatedViewSwitcher;
import net.twisterrob.blt.android.Injector;
import net.twisterrob.blt.android.data.AndroidStaticData;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.data.range.RangeMapGeneratorConfig;
import net.twisterrob.blt.android.db.model.NetworkNode;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.android.feature.range.R;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder.DescriptionFormatter;
import net.twisterrob.blt.model.Line;
import net.twisterrob.blt.model.StopType;
import net.twisterrob.java.model.Location;
import net.twisterrob.java.utils.tostring.StringerRepo;
import net.twisterrob.java.utils.tostring.ToStringer;

public class RangeNearestFragment extends Fragment {
	private static final Logger LOG = LoggerFactory.getLogger(RangeNearestFragment.class);
	private TextSwitcher droppedPin;
	private AutomatedViewSwitcher droppedPinAutomation;
	private ViewGroup nearestStations;
	private LatLng lastStartPoint;
	private GeocoderTask m_geocoderTask;

	@Inject
	AndroidStaticData staticData;

	@Override public void onAttach(@NonNull Context context) {
		Injector.from(context).inject(this);
		super.onAttach(context);
	}

	@Override public @Nullable View onCreateView(
			LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_range_nearest, container, false);
	}
	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		droppedPin = (TextSwitcher)view.findViewById(R.id.view__range__dropped_pin);
		droppedPin.setInAnimation(getContext(), android.R.anim.fade_in);
		droppedPin.setOutAnimation(getContext(), android.R.anim.fade_out);
		droppedPinAutomation = new AutomatedViewSwitcher(droppedPin, SECONDS.toMillis(3), SECONDS.toMillis(15));
		nearestStations = (ViewGroup)view.findViewById(R.id.view__range__nearest_stations);
		nearestStations.removeAllViews();
	}
	@Override public void onDestroyView() {
		super.onDestroyView();
		if (m_geocoderTask != null) {
			m_geocoderTask.cancel();
			m_geocoderTask = null;
		}
	}
	public void updateLocation(@NonNull LatLng latlng, @Nullable Address address) {
		ToStringer addressString = new ToStringer(StringerRepo.INSTANCE, address, new AddressNameStringer());
		LOG.trace("updateLocation: {}, address: {}, task {}",
				latlng, addressString, StringerTools.toString(m_geocoderTask));
		if (address == null) {
			if (m_geocoderTask != null) {
				m_geocoderTask.cancel();
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
				latlng, address != null? "<received>" : null, StringerTools.toString(m_geocoderTask));
		String addressString = LocationUtils.getVagueAddress(address);
		String pin = getString(R.string.range__nearest_location, latlng.latitude, latlng.longitude);
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
			new StationAdapter.ViewHolder(view, staticData, formatter).bind(station, null);
			nearestStations.addView(view);
		}
		if (nearestStations.getChildCount() == 0) {
			TextView empty = new TextView(getContext());
			empty.setText(R.string.range__nearest_empty);
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

	@SuppressLint("StaticFieldLeak") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	private final class GeocoderTask extends SimpleSafeAsyncTask<LatLng, Void, Address> {
		private final Geocoder geocoder;
		private GeocoderTask(Context context) {
			geocoder = createGeocoder(context);
		}

		private Geocoder createGeocoder(Context context) {
			Geocoder geocoder;
			if (Geocoder.isPresent()) {
				try {
					geocoder = new Geocoder(context);
				} catch (Exception ex) {
					LOG.warn("Geocoding not available.", ex);
					geocoder = null;
				}
			} else {
				LOG.warn("Geocoding not available.");
				geocoder = null;
			}
			return geocoder;
		}

		@Override protected @Nullable Address doInBackground(LatLng latlng) throws Exception {
			if (geocoder != null) {
				@SuppressWarnings("deprecation") // We're already in background. TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
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
		
		@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		public void cancel() {
			super.cancel(true);
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
