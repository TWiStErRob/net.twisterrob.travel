package net.twisterrob.blt.android.data.range;

import org.slf4j.*;

public class RenderedGeoSize {
	private static final Logger LOG = LoggerFactory.getLogger(RenderedGeoSize.class);

	private double geoLonSpan;
	private double geoLatSpan;

	private int pixelWidth;
	private int pixelHeight;

	public double getGeoLonSpan() {
		return geoLonSpan;
	}
	public double getGeoLatSpan() {
		return geoLatSpan;
	}

	public int getPixelWidth() {
		return pixelWidth;
	}
	public int getPixelHeight() {
		return pixelHeight;
	}

	public void setGeoSize(double geoLonSpan, double getLatSpan) {
		this.geoLonSpan = geoLonSpan;
		this.geoLatSpan = getLatSpan;
	}

	/**
	 * Sets the size of a 1° × 1° are of the geo-coordinate system in pixels.
	 * Geo-size has to be set first for this method to work correctly.
	 * @see #setGeoSize(double, double)
	 */
	public void setGeoPixelSize(double geoLonPixelWidth, double getLatPixelHeight) {
		// *2 makes the pixels square, because the geo coordinate system is twice as wide as tall
		int pixelWidth = (int)(geoLonSpan * geoLonPixelWidth);
		int pixelHeight = (int)(geoLatSpan * getLatPixelHeight * 2);
		setPixelSize(pixelWidth, pixelHeight);
	}

	/**
	 * Sets the size of a 1° × 1° are of the geo-coordinate system in pixels.
	 * Geo-size has to be set first for this method to work correctly.
	 * This version makes sure that the size set is allowed by the maximum size.
	 * @see #setGeoSize(double, double)
	 */
	public void setSafeGeoPixelSize(double geoLonPixelWidth, double getLatPixelHeight) {
		// *2 makes the pixels square, because the geo coordinate system is twice as wide as tall
		int pixelWidth = (int)(geoLonSpan * geoLonPixelWidth);
		int pixelHeight = (int)(geoLatSpan * getLatPixelHeight * 2);
		setSafePixelSize(pixelWidth, pixelHeight);
	}

	/**
	 * Sets the size of the resulting image in pixels.
	 */
	public void setPixelSize(int imageWidth, int imageHeight) {
		this.pixelWidth = imageWidth;
		this.pixelHeight = imageHeight;
	}

	public void setSafePixelSize(int pixelWidth, int pixelHeight) {
		MaxSize max = getSafeMaximum();
		if (pixelWidth <= max.width && pixelHeight <= max.height) {
			setPixelSize(pixelWidth, pixelHeight);
		} else {
			final float widthPercentage = max.width / (float)pixelWidth;
			final float heightPercentage = max.height / (float)pixelHeight;
			final float minPercentage = Math.min(widthPercentage, heightPercentage);
			setPixelSize((int)(minPercentage * pixelWidth), (int)(minPercentage * pixelHeight));
			LOG.debug("{}: Bitmap size ({}x{}) exceeded maximum allowed size ({}x{}), scaling down to {}x{}.",
					getClass(), pixelWidth, pixelHeight, max.width, max.height, this.pixelWidth, this.pixelHeight);
		}
	}

	protected MaxSize getSafeMaximum() {
		MaxSize size = new MaxSize();
		size.width = size.height = Integer.MAX_VALUE;
		return size;
	}

	protected static class MaxSize {
		public int width;
		public int height;
	}
}
