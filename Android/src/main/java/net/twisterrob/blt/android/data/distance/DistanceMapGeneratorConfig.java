package net.twisterrob.blt.android.data.distance;

public class DistanceMapGeneratorConfig {
	double timeTransfer = 5 /* minutes */;
	double timePlatformToStreet = 1 /* minutes */;
	double speedOnFoot = 4.5 /* km/h */;
	double minutes;

	DistanceStrategy tubingStrategy = new AverageSpeedTubingStrategy();
	//DistanceStrategy tubingStrategy = new SmartTubingStrategy();

	public DistanceMapGeneratorConfig timeTransfer(double timeTransfer) {
		this.timeTransfer = timeTransfer;
		return this;
	}

	public DistanceMapGeneratorConfig timePlatformToStreet(double platformToStreet) {
		this.timePlatformToStreet = platformToStreet;
		return this;
	}

	public DistanceMapGeneratorConfig speedOnFoot(double speed) {
		this.speedOnFoot = speed;
		return this;
	}

	public DistanceMapGeneratorConfig tubingStrategy(DistanceStrategy distance) {
		this.tubingStrategy = distance;
		return this;
	}

	public DistanceMapGeneratorConfig minutes(double minutes) {
		this.minutes = minutes;
		return this;
	}
}