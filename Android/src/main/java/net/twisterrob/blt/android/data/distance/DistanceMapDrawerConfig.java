package net.twisterrob.blt.android.data.distance;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.model.*;
import android.graphics.Color;

public class DistanceMapDrawerConfig {
	int borderSize = 0;
	int borderColor = Color.BLACK;

	double pixelDensity = 1000;

	boolean dynamicColor = false;
	int distanceColor = Color.RED;
	LineColors colors = App.getInstance().getStaticData().getLineColors();

	public int getColor(Line line) {
		int color = dynamicColor? line.getBackground(colors) : distanceColor;
		return color & 0x00FFFFFF; // remove alpha
	}

	public DistanceMapDrawerConfig borderSize(int width) {
		this.borderSize = width;
		return this;
	}

	public DistanceMapDrawerConfig borderColor(int color) {
		this.borderColor = color;
		return this;
	}

	public DistanceMapDrawerConfig pixelDensity(double speed) {
		this.pixelDensity = speed;
		return this;
	}

	public DistanceMapDrawerConfig dynamicColor(boolean dynamicColor) {
		this.dynamicColor = dynamicColor;
		return this;
	}

	public DistanceMapDrawerConfig distanceColor(int distanceColor) {
		this.distanceColor = distanceColor;
		return this;
	}
}