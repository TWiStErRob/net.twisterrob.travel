package net.twisterrob.blt.android.data.range.tiles;

import org.slf4j.*;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.*;

public abstract class SafeTileProvider implements TileProvider {
	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	@Override public final @Nullable Tile getTile(int x, int y, int zoom) {
		LOG.trace("getTile({},{} @ {})", x, y, zoom);
		try {
			return safeGetTile(x, y, zoom);
		} catch (Throwable t) {
			LOG.error("Cannot render tile {},{} @ {}", x, y, zoom, t);
			return NO_TILE;
		}
	}

	/**
	 * Returns the tile to be used for this tile coordinate.
	 *
	 * @param x    The x coordinate of the tile. This will be in the range [0, 2^(zoom - 1)] inclusive.
	 * @param y    The y coordinate of the tile. This will be in the range [0, 2^(zoom - 1)] inclusive.
	 * @param zoom The zoom level of the tile.
	 * @return the {@link Tile} to be used for this tile coordinate. If you do not wish to provide
	 * a tile for this tile coordinate, return {@link #NO_TILE}. If the tile could not be found at
	 * this point in time, return null and further requests might be made with an exponential
	 * backoff.
	 * @throws Throwable it's safe to throw exceptions during tile creation.
	 */
	protected abstract @Nullable Tile safeGetTile(int x, int y, int zoom) throws Throwable;
}
