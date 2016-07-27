package org.maptiler;

/**
 * TMS Global Mercator Profile
 * ---------------------------
 *
 * Functions necessary for generation of tiles in Spherical Mercator projection,
 * EPSG:900913 (EPSG:gOOglE, Google Maps Global Mercator), EPSG:3785, OSGEO:41001.
 *
 * Such tiles are compatible with Google Maps, Microsoft Virtual Earth, Yahoo Maps,
 * UK Ordnance Survey OpenSpace API, ...
 * and you can overlay them on top of base maps of those web mapping applications.
 *
 * Pixel and tile coordinates are in TMS notation (origin [0,0] in bottom-left).
 *
 * What coordinate conversions do we need for TMS Global Mercator tiles::
 *
 *      LatLon      <->       Meters      <->     Pixels    <->       Tile     
 *
 *  WGS84 coordinates   Spherical Mercator  Pixels in pyramid  Tiles in pyramid
 *      lat/lon            XY in metres     XY pixels Z zoom      XYZ from TMS 
 *     EPSG:4326           EPSG:900913                                         
 *      .----.              ---------               --                TMS      
 *     /      \     <->     |       |     <->     /----/    <->      Google    
 *     \      /             |       |           /--------/          QuadTree   
 *      -----               ---------         /------------/                   
 *    KML, public         WebMapService         Web Clients      TileMapService
 *
 * What is the coordinate extent of Earth in EPSG:900913?
 *
 *   [-20037508.342789244, -20037508.342789244, 20037508.342789244, 20037508.342789244]
 *   Constant 20037508.342789244 comes from the circumference of the Earth in meters,
 *   which is 40 thousand kilometers, the coordinate origin is in the middle of extent.
 *   In fact you can calculate the constant as: 2 * PI * 6378137 / 2.0
 *   $ echo 180 85 | gdaltransform -s_srs EPSG:4326 -t_srs EPSG:900913
 *   Polar areas with abs(latitude) bigger than 85.05112878 are clipped off.
 *
 * What are zoom level constants (pixels/meter) for pyramid with EPSG:900913?
 *
 *   whole region is on top of pyramid (zoom=0) covered by 256x256 pixels tile,
 *   every lower zoom level resolution is always divided by two
 *   initialResolution = 20037508.342789244 * 2 / 256 = 156543.03392804062
 *
 * What is the difference between TMS and Google Maps/QuadTree tile name convention?
 *
 *   The tile raster itself is the same (equal extent, projection, pixel size),
 *   there is just different identification of the same raster tile.
 *   Tiles in TMS are counted from [0,0] in the bottom-left corner, id is XYZ.
 *   Google placed the origin [0,0] to the top-left corner, reference is XYZ.
 *   Microsoft is referencing tiles by a QuadTree name, defined on the website:
 *   http://msdn2.microsoft.com/en-us/library/bb259689.aspx
 *
 * The lat/lon coordinates are using WGS84 datum, yeh?
 *
 *   Yes, all lat/lon we are mentioning should use WGS84 Geodetic Datum.
 *   Well, the web clients like Google Maps are projecting those coordinates by
 *   Spherical Mercator, so in fact lat/lon coordinates on sphere are treated as if
 *   the were on the WGS84 ellipsoid.
 *
 *   From MSDN documentation:
 *   To simplify the calculations, we use the spherical form of projection, not
 *   the ellipsoidal form. Since the projection is used only for map display,
 *   and not for displaying numeric coordinates, we don't need the extra precision
 *   of an ellipsoidal projection. The spherical projection causes approximately
 *   0.33 percent scale distortion in the Y direction, which is not visually noticeable.
 *
 * How do I create a raster in EPSG:900913 and convert coordinates with PROJ.4?
 *
 *   You can use standard GIS tools like gdalwarp, cs2cs or gdaltransform.
 *   All of the tools supports -t_srs 'epsg:900913'.
 *
 *   For other GIS programs check the exact definition of the projection:
 *   More info at http://spatialreference.org/ref/user/google-projection/
 *   The same projection is defined as EPSG:3785. WKT definition is in the official
 *   EPSG database.
 *
 *   Proj4 Text:
 *     +proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0
 *     +k=1.0 +units=m +nadgrids=@null +no_defs
 *
 *   Human readable WKT format of EPGS:900913:
 *      PROJCS["Google Maps Global Mercator",
 *          GEOGCS["WGS 84",
 *              DATUM["WGS_1984",
 *                  SPHEROID["WGS 84",6378137,298.2572235630016,
 *                      AUTHORITY["EPSG","7030"]],
 *                  AUTHORITY["EPSG","6326"]],
 *              PRIMEM["Greenwich",0],
 *              UNIT["degree",0.0174532925199433],
 *              AUTHORITY["EPSG","4326"]],
 *          PROJECTION["Mercator_1SP"],
 *          PARAMETER["central_meridian",0],
 *          PARAMETER["scale_factor",1],
 *          PARAMETER["false_easting",0],
 *          PARAMETER["false_northing",0],
 *          UNIT["metre",1,
 *              AUTHORITY["EPSG","9001"]]]
 */
public class GlobalMercator {
	private final int tileSize;
	/** 156543.03392804062 for tileSize 256 pixels */
	private final double initialResolution;
	/** 20037508.342789244 */
	private final double originShift;

	/** Initialize the TMS Global Mercator pyramid */
	public GlobalMercator() {
		this(256);
	}

	/** Initialize the TMS Global Mercator pyramid */
	public GlobalMercator(int tileSize) {
		this.tileSize = tileSize;
		this.initialResolution = 2 * Math.PI * 6378137 / this.tileSize;
		this.originShift = 2 * Math.PI * 6378137 / 2.0;
	}

	/** Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator EPSG:900913 */
	public Meters LatLonToMeters(double lat, double lon) {
		double mx = lon * this.originShift / 180.0;
		double my = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);
		my = my * this.originShift / 180.0;
		return new Meters(mx, my);
	}

	/** Converts XY point from Spherical Mercator EPSG:900913 to lat/lon in WGS84 Datum */
	public LatLon MetersToLatLon(double mx, double my) {
		double lon = (mx / this.originShift) * 180.0;
		double lat = (my / this.originShift) * 180.0;
		lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
		return new LatLon(lat, lon);
	}

	/** Converts pixel coordinates in given zoom level of pyramid to EPSG:900913 */
	public Meters PixelsToMeters(double px, double py, int zoom) {
		double res = this.Resolution(zoom);
		double mx = px * res - this.originShift;
		double my = py * res - this.originShift;
		return new Meters(mx, my);
	}

	/** Converts EPSG:900913 to pyramid pixel coordinates in given zoom level */
	public Pixels MetersToPixels(double mx, double my, int zoom) {
		double res = this.Resolution(zoom);
		long px = Math.round((mx + this.originShift) / res);
		long py = Math.round((my + this.originShift) / res);
		return new Pixels(px, py);
	}

	/** Returns a tile covering region in given pixel coordinates */
	public Tile PixelsToTile(double px, double py) {
		int tx = (int)(Math.ceil(px / (double)this.tileSize) - 1);
		int ty = (int)(Math.ceil(py / (double)this.tileSize) - 1);
		return new Tile(tx, ty);
	}

	/** Move the origin of pixel coordinates to top-left corner */
	public Pixels PixelsToRaster(long px, long py, int zoom) {
		long mapSize = this.tileSize << zoom;
		return new Pixels(px, mapSize - py);
	}

	/** Returns tile for given mercator coordinates */
	public Tile MetersToTile(double mx, double my, int zoom) {
		Pixels p = this.MetersToPixels(mx, my, zoom);
		return this.PixelsToTile(p.x, p.y);
	}

	/** Returns bounds of the given tile in EPSG:900913 coordinates */
	public Meters[] TileBounds(int tx, int ty, int zoom) {
		Pixels[] bounds = this.TilePixelsBounds(tx, ty);
		Meters min = this.PixelsToMeters(bounds[0].x, bounds[0].y, zoom);
		Meters max = this.PixelsToMeters(bounds[1].x, bounds[1].y, zoom);
		return new Meters[] {min, max};
	}

	/** Returns bounds of the given tile in latitude/longitude using WGS84 datum */
	public LatLon[] TileLatLonBounds(int tx, int ty, int zoom) {
		Meters[] bounds = this.TileBounds(tx, ty, zoom);
		LatLon min = this.MetersToLatLon(bounds[0].x, bounds[0].y);
		LatLon max = this.MetersToLatLon(bounds[1].x, bounds[1].y);
		return new LatLon[] {min, max};
	}

	/** Returns bounds of the given tile in pyramid pixel coordinates */
	public Pixels[] TilePixelsBounds(int tx, int ty) {
		Pixels min = new Pixels(tx * this.tileSize, ty * this.tileSize);
		Pixels max = new Pixels((tx + 1) * this.tileSize, (ty + 1) * this.tileSize);
		return new Pixels[] {min, max};
	}

	/** Resolution (meters/pixel) for given zoom level (measured at Equator) */
	public double Resolution(int zoom) {
		// return (2 * Math.PI * 6378137) / (this.tileSize * Math.pow(2, zoom));
		return this.initialResolution / (1 << zoom);
	}

	/** Maximal scaledown zoom of the pyramid closest to the pixelSize. */
	public int ZoomForPixelSize(int pixelSize) {
		for (int i = 0; i < 30; ++i) {
			if (pixelSize > this.Resolution(i)) {
				return i != 0? i - 1 : 0; // We don't want to scale up
			}
		}
		throw new IllegalArgumentException("Cannot find zoom level for pixel size: " + pixelSize);
	}

	/** Converts TMS tile coordinates to Google Tile coordinates */
	public Tile GoogleTile(int tx, int ty, int zoom) {
		// coordinate origin is moved from bottom-left to top-left corner of the extent
		return new Tile(tx, ((1 << zoom) - 1) - ty);
	}

	/** Converts TMS tile coordinates to Microsoft QuadTree */
	public String QuadTree(int tx, int ty, int zoom) {
		String quadKey = "";
		ty = ((1 << zoom) - 1) - ty;
		for (int i = zoom; i > 0; --i) {
			int digit = 0;
			int mask = 1 << (i - 1);
			if ((tx & mask) != 0) {
				digit += 0b01;
			}
			if ((ty & mask) != 0) {
				digit += 0b10;
			}
			quadKey += digit;
		}
		return quadKey;
	}
}
