package net.twisterrob.blt.android.data.distance;

import android.graphics.Color;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.model.*;

public class DistanceMapDrawerConfig {
	public static final int BORDER_SIZE_MIN = 0;
	public static final int BORDER_SIZE_MAX = 128;
	public static final float PIXEL_DENSITY_MIN = 0;
	public static final float PIXEL_DENSITY_MAX = 10000;
	int borderSize = 0;
	int borderColor = Color.BLACK;

	float pixelDensity = 1000;

	boolean dynamicColor = false;
	int distanceColor = Color.RED;
	LineColors colors = App.getInstance().getStaticData().getLineColors();
	public DistanceMapDrawerConfig() {
	}
	public DistanceMapDrawerConfig(DistanceMapDrawerConfig other) {
		this.borderSize = other.borderSize;
		this.borderColor = other.borderColor;
		this.pixelDensity = other.pixelDensity;
		this.dynamicColor = other.dynamicColor;
		this.distanceColor = other.distanceColor;
		this.colors = other.colors;
	}

	public int getColor(Line line) {
		int color = dynamicColor? line.getBackground(colors) : distanceColor;
		return color & 0x00FFFFFF; // remove alpha
	}

	public DistanceMapDrawerConfig setColors(LineColors colors) {
		this.colors = colors;
		return this;
	}

	public DistanceMapDrawerConfig setBorderSize(int width) {
		this.borderSize = width;
		return this;
	}

	public DistanceMapDrawerConfig setBorderColor(int color) {
		this.borderColor = color;
		return this;
	}

	public DistanceMapDrawerConfig setPixelDensity(float speed) {
		this.pixelDensity = speed;
		return this;
	}

	/**
	 * Whether to use {@link LineColors} or {@link #distanceColor} when drawing range circles. 
	 */
	public DistanceMapDrawerConfig setDynamicColor(boolean dynamicColor) {
		this.dynamicColor = dynamicColor;
		return this;
	}

	public DistanceMapDrawerConfig setDistanceColor(int distanceColor) {
		this.distanceColor = distanceColor;
		return this;
	}

	public LineColors getColors() {
		return colors;
	}
	public boolean isDynamicColor() {
		return dynamicColor;
	}
	public int getBorderSize() {
		return borderSize;
	}
	public int getBorderColor() {
		return borderColor;
	}
	public float getPixelDensity() {
		return pixelDensity;
	}
	public int getDistanceColor() {
		return distanceColor;
	}
}
