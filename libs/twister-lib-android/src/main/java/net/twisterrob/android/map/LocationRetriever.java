package net.twisterrob.android.map;
import static android.location.LocationManager.*;
import android.content.Context;
import android.location.*;
import android.os.Bundle;
public class LocationRetriever {
	private final LocationManager m_manager;
	private final LocationResultListener m_locResultListener;

	// /////////////////////////////////////////////////////
	// Provider location listeners
	private boolean m_isGPSEnabled;
	private final LocationListener m_locationListenerGPS = new LocationListener() {
		public void onLocationChanged(Location location) {
			m_locResultListener.locationRetrieved(LocationRetriever.this, location, LocationType.GPS);
		}
		public void onProviderDisabled(String provider) {
			assert GPS_PROVIDER.equals(provider) : getProviderError(GPS_PROVIDER, provider);
			m_isGPSEnabled = false;
		}
		public void onProviderEnabled(String provider) {
			assert GPS_PROVIDER.equals(provider) : getProviderError(GPS_PROVIDER, provider);
			m_isGPSEnabled = true;
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	private boolean m_isNetworkEnabled = false;
	private final LocationListener m_locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			m_locResultListener.locationRetrieved(LocationRetriever.this, location, LocationType.Network);
		}
		public void onProviderDisabled(String provider) {
			assert NETWORK_PROVIDER.equals(provider) : getProviderError(NETWORK_PROVIDER, provider);
			m_isNetworkEnabled = false;
		}
		public void onProviderEnabled(String provider) {
			assert NETWORK_PROVIDER.equals(provider) : getProviderError(NETWORK_PROVIDER, provider);
			m_isNetworkEnabled = true;
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	};

	private static String getProviderError(String expectedProvider, String provider) {
		return String.format("%s provider didn't get the right provider update, instead it was %s", expectedProvider,
				provider);
	}
	// Provider location listeners
	// /////////////////////////////////////////////////////

	public LocationRetriever(Context context, LocationResultListener result) {
		m_locResultListener = result;
		m_manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		m_isGPSEnabled = m_manager.isProviderEnabled(GPS_PROVIDER);
		m_isNetworkEnabled = m_manager.isProviderEnabled(NETWORK_PROVIDER);
	}

	public boolean start(boolean getLastFirst) {
		if (getLastFirst)
			getLastLocation();
		startImpl();
		return m_isGPSEnabled || m_isNetworkEnabled;
	}

	private void startImpl() {
		if (!m_isGPSEnabled && !m_isNetworkEnabled)
			return;

		if (m_isGPSEnabled)
			m_manager.requestLocationUpdates(GPS_PROVIDER, 0, 0, m_locationListenerGPS);
		if (m_isNetworkEnabled)
			m_manager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, m_locationListenerNetwork);
	}
	public void stop() {
		m_manager.removeUpdates(m_locationListenerGPS);
		m_manager.removeUpdates(m_locationListenerNetwork);
	}

	private void getLastLocation() {
		Location locNetwork = null, locGPS = null;
		if (m_isGPSEnabled)
			locGPS = m_manager.getLastKnownLocation(GPS_PROVIDER);
		if (m_isNetworkEnabled)
			locNetwork = m_manager.getLastKnownLocation(NETWORK_PROVIDER);

		if (locGPS != null && locNetwork != null) {
			if (locGPS.getTime() >= locNetwork.getTime())
				m_locResultListener.locationRetrieved(this, locGPS, LocationType.LastGPS);
			else
				m_locResultListener.locationRetrieved(this, locNetwork, LocationType.LastNetwork);
			return;
		}

		if (locGPS != null) {
			m_locResultListener.locationRetrieved(this, locGPS, LocationType.LastGPS);
			return;
		}
		if (locNetwork != null) {
			m_locResultListener.locationRetrieved(this, locNetwork, LocationType.LastNetwork);
			return;
		}
	}

	public enum LocationType {
		GPS {
			@Override
			public boolean isCurrent() {
				return true;
			}
		},
		Network {
			@Override
			public boolean isCurrent() {
				return true;
			}
		},
		LastGPS {
			@Override
			public boolean isCurrent() {
				return false;
			}
		},
		LastNetwork {
			@Override
			public boolean isCurrent() {
				return false;
			}
		},
		Other {
			@Override
			public boolean isCurrent() {
				return true;
			}
		};

		/**
		 * Gives a clue about whether the fix was retrieved from {@link LocationManager#getLastKnownLocation} or
		 * {@link LocationManager#requestLocationUpdates} registered listeners.
		 * 
		 * @return <code>true</code> if the fix was a location update, <code>false</code> if it last known location.
		 */
		public abstract boolean isCurrent();
	}
	public interface LocationResultListener {
		/**
		 * Called when a new fix arrives from the {@link LocationManager#requestLocationUpdates} registered listeners or
		 * you requested the last known location in {@link LocationRetriever#start(boolean)}.
		 * <p>
		 * None of the parameters will never be <code>null</code>.
		 * 
		 * @param retriever the {@link LocationRetriever} the listener is registered with
		 * @param location new fix location
		 * @param type type of the fix
		 */
		public void locationRetrieved(LocationRetriever retriever, Location location, LocationType type);
	}
}