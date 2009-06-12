/**
 * Copyright (C) 2009 joerg <schreibubi@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.schreibubi.kartlaegga.mapviewer.tilefactories;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.schreibubi.kartlaegga.extend.Comparator;
import org.schreibubi.kartlaegga.mapviewer.GeoUtil;
import org.schreibubi.kartlaegga.mapviewer.PriorityQueue;
import org.schreibubi.kartlaegga.mapviewer.cache.LRUMemoryCache;

import com.sun.lwuit.Image;

/**
 * The <code>AbstractTileFactory</code> provides a basic implementation for the
 * TileFactory.
 */
public abstract class AbstractTileFactory extends TileFactory {

	/**
	 * Creates a new instance of DefaultTileFactory using the spcified
	 * TileFactoryInfo
	 * 
	 * @param info
	 *            a TileFactoryInfo to configure this TileFactory
	 */
	public AbstractTileFactory(TileFactoryInfo info) {
		super(info);
		TileRunner tr = new TileRunner();
		Thread trt = new Thread(tr);
		trt.start();
	}

	private LRUMemoryCache tileCache =new LRUMemoryCache(null);

	/**
	 * Returns the tile that is located at the given tilePoint for this zoom.
	 * For example, if getMapSize() returns 10x20 for this zoom, and the
	 * tilePoint is (3,5), then the appropriate tile will be located and
	 * returned.
	 * 
	 * @param tilePoint
	 * @param zoom
	 * @return
	 */
	public Tile getTile(int x, int y, int zoom) {
		return getTile(x, y, zoom, true);
	}

	private Tile getTile(int tpx, int tpy, int zoom, boolean eagerLoad) {
		// wrap the tiles horizontally --> mod the X with the max width
		// and use that
		System.out.println("Get Tile " + tpx + "/" + tpy + "/" + zoom);
		int tileX = tpx;// tilePoint.getX();
		int numTilesWide = (int) getMapSize(zoom).getWidth();
		if (tileX < 0) {
			tileX = numTilesWide - (Math.abs(tileX) % numTilesWide);
		}

		tileX = tileX % numTilesWide;
		int tileY = tpy;
		String url = getTileUrl(tileX, tileY, zoom);

		int pri = Tile.HIGH;
		if (!eagerLoad) {
			pri = Tile.LOW;
		}
		Tile tile = null;
		// System.out.println("testing for validity: " + tilePoint + " zoom = "
		// + zoom);
		tile = (Tile) tileCache.get(url);
		if (tile==null) {
			if (!GeoUtil.isValidTile(tileX, tileY, zoom, getInfo())) {
				tile = new Tile(tileX, tileY, zoom);
			} else {
				tile = new Tile(tileX, tileY, zoom, url, pri, this);
				startLoading(tile);
			}
			tileCache.put(url, tile);
		} else {
			// if its in the map but is low and isn't loaded yet
			// but we are in high mode
			if (tile.getPriority() == Tile.LOW && eagerLoad && !tile.isLoaded()) {
				promote(tile);
			}
		}
		return tile;
	}

    /**
     * Returns the tile url for the specified tile at the specified
     * zoom level. By default it will generate a tile url using the
     * base url and parameters specified in the constructor.
     *
     * @param zoom the zoom level
     * @param x    abscissa of the tile point
     * @param y    ordinate of the tile point
     * @return a valid url to load the tile
     */
    public String getTileUrl(int x, int y, int zoom) {
        String url = new StringBuffer().append(getInfo().getBaseUrl()).append(getInfo().getCoordinatePart(x, y, zoom)).append(getInfo().getUrlSuffix()).toString();
        return url;
    }

	private static PriorityQueue tileQueue = new PriorityQueue(5,
			new Comparator() {
				public int compare(Object ob1, Object ob2) {
					Tile o1 = (Tile) ob1;
					Tile o2 = (Tile) ob2;
					if (o1.getPriority() == Tile.LOW
							&& o2.getPriority() == Tile.HIGH) {
						return 1;
					}
					if (o1.getPriority() == Tile.HIGH
							&& o2.getPriority() == Tile.LOW) {
						return -1;
					}
					return 0;
				}

				public boolean equals(Object obj) {
					return obj == this;
				}
			});

	public synchronized void startLoading(Tile tile) {
		if (tile.isLoading()) {
			System.out.println("already loading. bailing");
			return;
		}
		tile.setLoading(true);
		try {
			System.out.println("adding tile to queue");
			tileQueue.add(tile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Increase the priority of this tile so it will be loaded sooner.
	 */
	public synchronized void promote(Tile tile) {
		try {
			if (tileQueue.remove(tile)) {
				tile.setPriority(Tile.HIGH);
				tileQueue.add(tile);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * An inner class which actually loads the tiles. Used by the thread queue.
	 * Subclasses can override this if necessary.
	 */
	private class TileRunner implements Runnable {

		/**
		 * implementation of the Runnable interface.
		 */
		public void run() {
			/*
			 * 3 strikes and you're out. Attempt to load the url. If it fails,
			 * decrement the number of tries left and try again. Log failures.
			 * If I run out of try s just get out. This way, if there is some
			 * kind of serious failure, I can get out and let other tiles try to
			 * load.
			 */
			do {
				final Tile tile = (Tile) tileQueue.poll();

				int trys = 3;
				while (!tile.isLoaded() && trys > 0) {
					try {
						Image img = null;
						String uri = tile.getURL();
						img = loadTileThroughHttp(uri);
						if (img == null) {
							System.out.println("error loading: " + uri);
							trys--;
						} else {
							// SwingUtilities.invokeAndWait(new Runnable() {
							tile.image = img;
							tile.setLoaded(true);
						}
						System.out.println("Loaded " + uri);
					} catch (OutOfMemoryError memErr) {
					} catch (Throwable e) {
						System.err.println("Failed to load a tile at url: "
								+ tile.getURL());
						e.printStackTrace();
						Object oldError = tile.getError();
						tile.setError(e);
						tile.firePropertyChange("loadingError", oldError, e);
						if (trys == 0) {
							tile.firePropertyChange("unrecoverableError", null,
									e);
						} else {
							trys--;
						}
					}
				}
				tile.setLoading(false);
			} while (true);
		}

		private Image loadTileThroughHttp(String url) throws IOException {
			HttpConnection c = (HttpConnection) Connector.open(url);
			c.setRequestProperty("User-Agent",
			"Mozilla/5.0 (X11; U; Linux i586; en-US; rv:1.7.3) Gecko/20040924 Epiphany/1.4.4 (Ubuntu)");
			int status = c.getResponseCode();
			if (status != HttpConnection.HTTP_OK) {
				throw new IOException("Response status not OK [" + status + "]");
			}
			InputStream ins = c.openInputStream();
			Image i = Image.createImage(ins);
			return i;
		}
	}
}
