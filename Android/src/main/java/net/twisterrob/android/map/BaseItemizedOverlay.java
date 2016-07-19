package net.twisterrob.android.map;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.*;

import net.twisterrob.android.graphics.DrawableBinder;

/**
 * Binders constraints:<br>
 * <ul>
 * <li>width = right - left</li>
 * <li>height = bottom - top</li>
 * </ul>
 */
public abstract class BaseItemizedOverlay<T extends OverlayItem> extends ItemizedOverlay<T> {
	private final Drawable m_defaultMarker;
	private final Drawable m_focusedMarker;
	private Boolean m_wantShadow;

	/**
	 * Constructor for BaseItemizedOverlay
	 *
	 * @param defaultMarker Default marker to show for the items. Don't forget to bind it with
	 *            {@link DrawableBinder#bind(Drawable, DrawableBinder.VBind, DrawableBinder.HBind)} or related methods.
	 */
	public BaseItemizedOverlay(Drawable defaultMarker) {
		this(defaultMarker, null);
	}

	/**
	 * Constructor for BaseItemizedOverlay
	 *
	 * @param defaultMarker Default marker to show for the items. Don't forget to bind it with
	 *            {@link DrawableBinder#bind(Drawable, DrawableBinder.VBind, DrawableBinder.HBind)} or related methods.
	 * @param focusedMarker Marker to show for the items when they're focused/selected. Don't forget to bind it with
	 *            {@link DrawableBinder#bind(Drawable, DrawableBinder.VBind, DrawableBinder.HBind)} or related methods.
	 */
	public BaseItemizedOverlay(Drawable defaultMarker, Drawable focusedMarker) {
		super(defaultMarker);
		m_defaultMarker = defaultMarker;
		m_focusedMarker = focusedMarker;
		setOnFocusChangeListener((OnFocusChangeListener<T>)null);
	}

	public Drawable getDefaultMarker() {
		return m_defaultMarker;
	}
	public Drawable getFocusedMarker() {
		return m_focusedMarker;
	}

	@Override public void setOnFocusChangeListener(final ItemizedOverlay.OnFocusChangeListener l) {
		super.setOnFocusChangeListener(new com.google.android.maps.ItemizedOverlay.OnFocusChangeListener() {
			@SuppressWarnings({"rawtypes", "unchecked"})
			public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
				BaseItemizedOverlay.this.onFocusChanged((T)newFocus);
				if (l != null) {
					l.onFocusChanged(overlay, newFocus);
				}
			}
		});
	}
	public void setOnFocusChangeListener(final OnFocusChangeListener<T> l) {
		super.setOnFocusChangeListener(new com.google.android.maps.ItemizedOverlay.OnFocusChangeListener() {
			@SuppressWarnings({"rawtypes", "unchecked"})
			public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
				BaseItemizedOverlay.this.onFocusChanged((T)newFocus);
				if (l != null) {
					l.onFocusChanged((BaseItemizedOverlay<T>)overlay, (T)newFocus);
				}
			}
		});
	}

	public interface OnFocusChangeListener<T extends OverlayItem> {
		void onFocusChanged(BaseItemizedOverlay<T> overlay, T newFocus);
	}

	private T m_lastFocus;
	protected void onFocusChanged(T newFocus) {
		if (getFocusedMarker() != null) {
			if (getLastFocus() != null) {
				getLastFocus().setMarker(getDefaultMarker());
			}
			if (newFocus != null) {
				newFocus.setMarker(getFocusedMarker());
			}
		}
		m_lastFocus = newFocus;
	}
	public T getLastFocus() {
		return m_lastFocus;
	}

	public void setShadow(Boolean wantShadow) {
		m_wantShadow = wantShadow;
	}
	public Boolean getShadow() {
		return m_wantShadow;
	}
	@Override public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, m_wantShadow == null? shadow : m_wantShadow);
	}
	@Override public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		return super.draw(canvas, mapView, m_wantShadow == null? shadow : m_wantShadow, when);
	}
}
