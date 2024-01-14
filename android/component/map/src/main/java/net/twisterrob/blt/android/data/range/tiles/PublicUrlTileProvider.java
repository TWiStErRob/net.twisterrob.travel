package net.twisterrob.blt.android.data.range.tiles;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.maps.model.UrlTileProvider;

import androidx.annotation.NonNull;

public class PublicUrlTileProvider extends UrlTileProvider {
	/** @see <a href="http://wiki.openstreetmap.org/wiki/Tile_servers">Tile servers</a> */
	public static final String OSM = "http://a.tile.openstreetmap.org/{z}/{x}/{y}.png";
	private String baseUrl;

	public PublicUrlTileProvider(@NonNull String url) {
		this(256, 256, url);
	}

	public PublicUrlTileProvider(int width, int height, @NonNull String url) {
		super(width, height);
		this.baseUrl = url;
	}

	@Override public @NonNull URL getTileUrl(int x, int y, int zoom) {
		String tileUrl = baseUrl
				.replace("{x}", Integer.toString(x))
				.replace("{y}", Integer.toString(y))
				.replace("{zoom}", Integer.toString(zoom));
		try {
			return new URL(tileUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid url template: " + baseUrl, e);
		}
	}
}
