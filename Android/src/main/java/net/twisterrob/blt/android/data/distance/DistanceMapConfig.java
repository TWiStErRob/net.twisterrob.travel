package net.twisterrob.blt.android.data.distance;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.model.*;
import android.graphics.Color;

public class DistanceMapConfig {
	double timePlatformToStreet = 1 /* minutes */;
	double speedOnFoot = 4.5 /* km/h */;

	DistanceStrategy tubingStrategy = new AverageSpeedTubingStrategy();
	//DistanceStrategy tubingStrategy = new SmartTubingStrategy();

	int borderSize = 0;
	int borderColor = Color.BLACK;

	double pixelDensity = 1000;

	boolean dynamicColor = false;
	int distanceColor = Color.RED;
	LineColors colors = App.getInstance().getStaticData().getLineColors();
	boolean blendColors = false;

	public int getColor(Line line) {
		int color = dynamicColor? line.getBackground(colors) : distanceColor;
		return color & 0x00FFFFFF; // remove alpha
	}

	public DistanceMapConfig timePlatformToStreet(double platformToStreet) {
		this.timePlatformToStreet = platformToStreet;
		return this;
	}

	public DistanceMapConfig speedOnFoot(double speed) {
		this.speedOnFoot = speed;
		return this;
	}

	public DistanceMapConfig tubingStrategy(DistanceStrategy distance) {
		this.tubingStrategy = distance;
		return this;
	}

	public DistanceMapConfig borderSize(int width) {
		this.borderSize = width;
		return this;
	}

	public DistanceMapConfig borderColor(int color) {
		this.borderColor = color;
		return this;
	}

	public DistanceMapConfig pixelDensity(double speed) {
		this.pixelDensity = speed;
		return this;
	}

	public DistanceMapConfig dynamicColor(boolean dynamicColor) {
		this.dynamicColor = dynamicColor;
		return this;
	}

	public DistanceMapConfig distanceColor(int distanceColor) {
		this.distanceColor = distanceColor;
		return this;
	}

	public DistanceMapConfig blendColors(boolean blend) {
		blendColors = blend;
		return this;
	}
}