package net.twisterrob.blt.android.data.range.tiles;

import org.maptiler.*;

import android.support.annotation.Nullable;

public abstract class GeoTileProvider extends SafeTileProvider {
	private final GlobalMercator converter;
	private final int tileSize;

	protected GeoTileProvider(int tileSize) {
		converter = new GlobalMercator(tileSize);
		this.tileSize = tileSize;
	}

	public int getTileSize() {
		return tileSize;
	}

	@Override protected @Nullable com.google.android.gms.maps.model.Tile safeGetTile(int x, int y, int zoom) {
		Tile tmsTile = converter.GoogleTile(x, y, zoom);
		LatLon[] bounds = converter.TileLatLonBounds(tmsTile.x, tmsTile.y, zoom);
		return getGeoTile(x, y, zoom, bounds[0].lat, bounds[0].lon, bounds[1].lat, bounds[1].lon);
	}

	/**
	 * Be careful, as the latitudes increase from bottom to top.
	 * Drawing on a {@link android.graphics.Bitmap} is usually the opposite with 0 being the top and size being the bottom.
	 * A simple {@code y = height - y} transformation solves the issue, or use the following setup:
	 * <pre><code>
	 * Matrix flipUp = new Matrix();
	 * flipUp.setScale(1, -1, TILE_SIZE / 2f, TILE_SIZE / 2f);
	 * canvas.setMatrix(flipUp);
	 * </code></pre>
	 * In the latter case, everything will be flipped, for example any text or icon drawn will be upside-down.
	 *
	 * @param x original x coordinate of the tile on the globe
	 * @param y original y coordinate of the tile on the globe
	 * @param zoom the zoom level
	 * @param minLat south boundary of the tile
	 * @param minLon west boundary of the tile
	 * @param maxLat north boundary of the tile
	 * @param maxLon east boundary of the tile
	 * @return a Tile that reflects either the x-y-z tile or one that exactly fits the geo-bounds.
	 */
	protected abstract @Nullable com.google.android.gms.maps.model.Tile getGeoTile(int x, int y, int zoom,
			double minLat, double minLon, double maxLat, double maxLon);
}
