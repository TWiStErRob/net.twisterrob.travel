package net.twisterrob.blt.data.apps;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import net.twisterrob.blt.data.algo.postcodes.*;
import net.twisterrob.blt.data.algo.postcodes.ConvexHull.ToPos;
import net.twisterrob.java.model.*;

public class PostCodeAreas {
	private static final DesktopStaticData STATIC_DATA = DesktopStaticData.INSTANCE;
	private static final ToPos<PostCode> POSER = new ConvexHull.ToPos<PostCode>() {
		public double getX(PostCode obj) {
			return obj.getLocation().getLongitude();
		}
		public double getY(PostCode obj) {
			return obj.getLocation().getLatitude();
		}
	};

	public static void main(String[] args) throws Throwable {
		List<PostCode> codes = loadData();
		Map<String, List<PostCode>> clusters = cluster(codes);
		for (Entry<String, List<PostCode>> cluster : clusters.entrySet()) {
			cluster.setValue(ConvexHull.convexHull(cluster.getValue(), POSER));
		}
		writeData(clusters);
	}

	protected static void writeData(Map<String, List<PostCode>> clusters) throws FileNotFoundException,
			UnsupportedEncodingException {
		try (PrintWriter out = new PrintWriter(STATIC_DATA.getOut("LondonTravel.v1.data-AreaHull.sql"), "utf-8")) {
			for (Entry<String, List<PostCode>> cluster : clusters.entrySet()) {
				String area = cluster.getKey();
				Location center = ConvexHull.center(cluster.getValue(), POSER);
				writeHullPoint(out, area, -1, center);
				int index = 0;
				for (PostCode code : cluster.getValue()) {
					writeHullPoint(out, area, index++, code.getLocation());
				}
			}
		}
	}

	private static void writeHullPoint(PrintWriter out, String area, int i, Location loc) {
		out.printf("insert into AreaHull(area_code, hull_index, latitude, longitude) "
						+ "values('%1$s', %2$d, %3$.6f, %4$.6f);\n",
				area, i, loc.getLatitude(), loc.getLongitude());
	}

	private static Map<String, List<PostCode>> cluster(List<PostCode> codes) {
		Map<String, List<PostCode>> clusters = new TreeMap<>();
		for (PostCode code : codes) {
			String area = code.getCode().substring(0, 4).trim();
			area = area.replaceAll("\\D+$", "?");
			List<PostCode> list = clusters.get(area);
			if (list == null) {
				list = new ArrayList<>();
				clusters.put(area, list);
			}
			list.add(code);
		}
		return clusters;
	}

	private static List<PostCode> loadData() throws IOException {
		List<PostCode> codes = new ArrayList<>();
		try (
				InputStream zip = new GZIPInputStream(new FileInputStream("src/data/london.csv.gz"));
				BufferedReader reader = new BufferedReader(new InputStreamReader(zip))
		) {
			String line; // BR1 1AB,50,540194,169201,E09000006,E05000109
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				String postCode = values[0];
				int quality = Integer.parseInt(values[1]);
				int easting = Integer.parseInt(values[2]);
				int northing = Integer.parseInt(values[3]);
				String district_code = values[4];
				String ward_code = values[5];
				Location loc = LocationConverter.gridRef2LatLon(easting, northing);
				PostCode code =
						new PostCode(postCode, quality, loc, district_code, district_code, ward_code, ward_code);
				codes.add(code);
			}
		}
		return codes;
	}
}
