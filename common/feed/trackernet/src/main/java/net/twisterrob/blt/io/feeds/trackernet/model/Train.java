package net.twisterrob.blt.io.feeds.trackernet.model;

import java.util.Date;

public class Train {
	private int setNumber;
	private int tripNumber;
	private String destinationName;
	private int destinationCode;
	private String location;
	private Date timeToStation;

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public int getDestinationCode() {
		return destinationCode;
	}
	public void setDestinationCode(int destinationCode) {
		this.destinationCode = destinationCode;
	}

	public int getSetNumber() {
		return setNumber;
	}
	public void setSetNumber(int setNumber) {
		this.setNumber = setNumber;
	}

	public int getTripNumber() {
		return tripNumber;
	}
	public void setTripNumber(int tripNumber) {
		this.tripNumber = tripNumber;
	}

	public Date getTimeToStation() {
		return timeToStation;
	}
	public void setTimeToStation(Date timeToStation) {
		this.timeToStation = timeToStation;
	}
}
