package org.maptiler;

import java.io.PrintStream;
import java.util.*;

/*
 * globalmaptiles.py
 * 
 * Global Map Tiles as defined in Tile Map Service (TMS) Profiles
 * ==============================================================
 * 
 * Functions necessary for generation of global tiles used on the web.
 * It contains classes implementing coordinate conversions for:
 * 
 *   - GlobalMercator (based on EPSG:900913 = EPSG:3785)
 *        for Google Maps, Yahoo Maps, Microsoft Maps compatible tiles
 *   - GlobalGeodetic (based on EPSG:4326)
 *        for OpenLayers Base Map and Google Earth compatible tiles
 * 
 * More info at:
 * 
 * http://wiki.osgeo.org/wiki/Tile_Map_Service_Specification
 * http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation
 * http://msdn.microsoft.com/en-us/library/bb259689.aspx
 * http://code.google.com/apis/maps/documentation/overlays.html#Google_Maps_Coordinates
 * 
 * Created by Klokan Petr Pridal on 2008-07-03.
 * Google Summer of Code 2008, project GDAL2Tiles for OSGEO.
 * 
 * In case you use this class in your product, translate it to another language
 * or find it usefull for your project please let me know.
 * My email: klokan at klokan dot cz.
 * I would like to know where it was used.
 * 
 * Class is available under the open-source GDAL license (www.gdal.org).
 */
public class globalmaptiles {
	private static void Usage() {
		Usage(null);
	}

	private static void Usage(String s) {
		PrintStream out = System.out;
		out.println("Usage: java globalmaptiles [-profile 'mercator'|'geodetic'] zoomlevel lat lon [latmax lonmax]");
		out.println();
		if (s != null) {
			out.println(s);
			out.println();
		}
		out.println("This utility prints for given WGS84 lat/lon coordinates (or bounding box) the list of tiles");
		out.println("covering specified area. Tiles are in the given 'profile' (default is Google Maps 'mercator')");
		out.println("and in the given pyramid 'zoomlevel'.");
		out.println("For each tile several information is printed including bonding box in EPSG:900913 and WGS84.");
		System.exit(1);
	}

	public static void main(String... argv) {
		String profile = "mercator";
		Integer zoomlevel = null;
		Double lat = null, lon = null, latmax = null, lonmax = null;
		LatLon[] boundingbox = null;

		int i = 0;
		while (i < argv.length) {
			String arg = argv[i];

			if ("-profile".equals(arg)) {
				i = i + 1;
				profile = argv[i];
			}

			if (zoomlevel == null) {
				zoomlevel = Integer.parseInt(argv[i], 10);
			} else if (lat == null) {
				lat = Double.parseDouble(argv[i]);
			} else if (lon == null) {
				lon = Double.parseDouble(argv[i]);
			} else if (latmax == null) {
				latmax = Double.parseDouble(argv[i]);
			} else if (lonmax == null) {
				lonmax = Double.parseDouble(argv[i]);
			} else {
				Usage("ERROR: Too many parameters");
			}

			i = i + 1;
		}

		if (!"mercator".equals(profile)) {
			Usage("ERROR: Sorry, given profile is not implemented yet.");
		}

		if (zoomlevel == null || lat == null || lon == null) {
			Usage("ERROR: Specify at least 'zoomlevel', 'lat' and 'lon'.");
		}
		if (latmax != null && lonmax == null) {
			Usage("ERROR: Both 'latmax' and 'lonmax' must be given.");
		}

		if (latmax != null && lonmax != null) {
			if (latmax < lat) {
				Usage("ERROR: 'latmax' must be bigger than 'lat'");
			}
			if (lonmax < lon) {
				Usage("ERROR: 'lonmax' must be bigger than 'lon'");
			}
			boundingbox = new LatLon[] {new LatLon(lon, lat), new LatLon(lonmax, latmax)};
		}

		int tz = zoomlevel;
		GlobalMercator mercator = new GlobalMercator();

		System.out.printf("WGS84 coordinates (EPSG:4326) coordinates for lat/lon: %f, %f\n", lat, lon);
		Meters m = mercator.LatLonToMeters(lat, lon);
		System.out.printf("Spherical Mercator (ESPG:900913) coordinates for lat/lon: %f, %f\n", m.x, m.y);
		Tile tmin = mercator.MetersToTile(m.x, m.y, tz), tmax;

		if (boundingbox != null) {
			System.out.printf("WGS84 coordinates (EPSG:4326) coordinates for maxlat/maxlon: %f, %f\n", latmax, lonmax);
			Meters max = mercator.LatLonToMeters(latmax, lonmax);
			System.out.printf("Spherical Mercator (ESPG:900913) coordinates for maxlat/maxlon: %f, %f\n", max.x, max.y);
			tmax = mercator.MetersToTile(max.x, max.y, tz);
		} else {
			tmax = tmin;
		}

		for (int ty = tmin.y; ty <= tmax.y; ++ty) {
			for (int tx = tmin.x; tx <= tmax.x; ++tx) {
				String tilefilename = String.format(Locale.ROOT, "%s/%s/%s", tz, tx, ty);
				System.out.printf("%s ( TileMapService: z / x / y )\n", tilefilename);

				Tile g = mercator.GoogleTile(tx, ty, tz);
				System.out.printf("\tGoogle: %d, %d\n", g.x, g.y);
				Pixels[] pixbounds = mercator.TilePixelsBounds(g.x, g.y);
				System.out.printf("\tGoogle's pyramid Extent: %s\n", Arrays.toString(pixbounds));

				String quadkey = mercator.QuadTree(tx, ty, tz);
				System.out.printf("\tQuadkey: %s (%d)\n", quadkey, Integer.parseInt(quadkey, 4));

				System.out.println();

				Meters[] bounds = mercator.TileBounds(tx, ty, tz);
				System.out.printf("\tEPSG:900913 Extent: %s\n", Arrays.toString(bounds));
				LatLon[] wgsbounds = mercator.TileLatLonBounds(tx, ty, tz);
				System.out.printf("\tWGS84 Extent: %s\n", Arrays.toString(wgsbounds));
				System.out.printf("\tgdalwarp -ts 256 256 -te %s %s %s %s %s %s_%s_%s.tif\n",
						bounds[0].x, bounds[0].y, bounds[1].x, bounds[1].y,
						"<your-raster-file-in-epsg900913.ext>", tz, tx, ty);

				System.out.println();
			}
		}
	}
}
