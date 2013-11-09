package net.twisterrob.blt.data.algo.postcodes;

import net.twisterrob.java.model.Location;

/**
 * BR1 1AB,50,540194,169201,E09000006,E05000109
 * 
 * @author TWiStEr
 */
public class PostCode {
	private final String code;
	private final int quality;
	private final Location loc;
	private final String district_code;
	private final String district;
	private final String ward_code;
	private final String ward;

	public PostCode(final String code, final int quality, final Location loc, final String district_code,
			final String district, final String ward_code, final String ward) {
		super();
		this.code = code;
		this.quality = quality;
		this.loc = loc;
		this.district_code = district_code;
		this.district = district;
		this.ward_code = ward_code;
		this.ward = ward;
	}

	public String getCode() {
		return code;
	}

	public int getQuality() {
		return quality;
	}

	public Location getLocation() {
		return loc;
	}

	public String getDistrict_code() {
		return district_code;
	}

	public String getDistrict() {
		return district;
	}

	public String getWard_code() {
		return ward_code;
	}

	public String getWard() {
		return ward;
	}
}
