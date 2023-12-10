package net.twisterrob.blt.android.data.range;

import android.graphics.Color;

import net.twisterrob.blt.android.app.range.App;
import net.twisterrob.blt.model.*;

public class RangeMapDrawerConfig {
	public static final int BORDER_SIZE_MIN = 0;
	public static final int BORDER_SIZE_MAX = 128;
	public static final float PIXEL_DENSITY_MIN = 0;
	public static final float PIXEL_DENSITY_MAX = 10000;
	int borderSize = 0;
	int borderColor = Color.BLACK;

	float pixelDensity = 1024;

	boolean dynamicColor = true;
	int rangeColor = Color.RED;
	LineColors colors = App.getInstance().getStaticData().getLineColors();
	public RangeMapDrawerConfig() {
	}

	public RangeMapDrawerConfig(RangeMapDrawerConfig config) {
		set(config);
	}

	public void set(RangeMapDrawerConfig other) {
		this.borderSize = other.borderSize;
		this.borderColor = other.borderColor;
		this.pixelDensity = other.pixelDensity;
		this.dynamicColor = other.dynamicColor;
		this.rangeColor = other.rangeColor;
		this.colors = other.colors;
	}

	public int getColor(Line line) {
		int color = dynamicColor? line.getBackground(colors) : rangeColor;
		return color & 0x00FFFFFF; // remove alpha
	}

	public RangeMapDrawerConfig setColors(LineColors colors) {
		this.colors = colors;
		return this;
	}

	public RangeMapDrawerConfig setBorderSize(int width) {
		this.borderSize = width;
		return this;
	}

	public RangeMapDrawerConfig setBorderColor(int color) {
		this.borderColor = color;
		return this;
	}

	public RangeMapDrawerConfig setPixelDensity(float speed) {
		this.pixelDensity = speed;
		return this;
	}

	/**
	 * Whether to use {@link LineColors} or {@link #rangeColor} when drawing range circles. 
	 */
	public RangeMapDrawerConfig setDynamicColor(boolean dynamicColor) {
		this.dynamicColor = dynamicColor;
		return this;
	}

	public RangeMapDrawerConfig setRangeColor(int rangeColor) {
		this.rangeColor = rangeColor;
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
	public int getRangeColor() {
		return rangeColor;
	}
}
