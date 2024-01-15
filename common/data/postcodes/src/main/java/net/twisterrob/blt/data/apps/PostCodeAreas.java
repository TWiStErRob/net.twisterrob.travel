package net.twisterrob.blt.data.apps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import net.twisterrob.blt.data.algo.postcodes.ConvexHull;
import net.twisterrob.blt.data.algo.postcodes.ConvexHull.ToPos;
import net.twisterrob.blt.data.algo.postcodes.PostCode;
import net.twisterrob.java.model.Location;
import net.twisterrob.java.model.LocationConverter;

public class PostCodeAreas {
	private static final ToPos<PostCode> POSER = new ConvexHull.ToPos<PostCode>() {
		public double getX(PostCode obj) {
			return obj.getLocation().getLongitude();
		}
		public double getY(PostCode obj) {
			return obj.getLocation().getLatitude();
		}
	};

	public static void main(String... args) throws Throwable {
		if (args.length != 2) {
			throw new IllegalArgumentException("Usage: PostCodeAreas <postcodes.csv.gz> <output.sql>");
		}
		File postcodesCsvGz = new File(args[0]);
		File outputSql = new File(args[1]);
		List<PostCode> codes = loadData(postcodesCsvGz);
		Map<String, List<PostCode>> clusters = cluster(codes);
		for (Entry<String, List<PostCode>> cluster : clusters.entrySet()) {
			cluster.setValue(ConvexHull.convexHull(cluster.getValue(), POSER));
		}
		writeData(outputSql, clusters);
	}

	protected static void writeData(File outputSql, Map<String, List<PostCode>> clusters) throws IOException {
		try (PrintWriter out = new PrintWriter(outputSql, "utf-8")) {
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
		out.printf(Locale.ROOT, "insert into AreaHull(area_code, hull_index, latitude, longitude) "
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

	private static List<PostCode> loadData(File postcodesCsvGz) throws IOException {
		List<PostCode> codes = new ArrayList<>();
		try (
				InputStream zip = new GZIPInputStream(new FileInputStream(postcodesCsvGz));
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
