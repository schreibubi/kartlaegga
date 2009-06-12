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

package org.schreibubi.kartlaegga.mapviewer;

import java.io.IOException;
import java.util.Vector;

import org.schreibubi.kartlaegga.extend.Point2D;
import org.schreibubi.kartlaegga.extend.Rectangle;
import org.schreibubi.kartlaegga.mapviewer.tilefactories.Tile;
import org.schreibubi.kartlaegga.mapviewer.tilefactories.TileFactory;
import org.schreibubi.kartlaegga.mapviewer.tilefactories.TileFactoryInfo;
import org.schreibubi.kartlaegga.mapviewer.tilefactories.openstreetmap.OpenStreetMapMapnikTileFactory;
import org.schreibubi.kartlaegga.mapviewer.track.TrackListener;
import org.schreibubi.kartlaegga.mapviewer.track.TrackPoint;

import com.sun.lwuit.Component;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.plaf.Style;

/**
 * A tile oriented map component that can easily be used with tile sources on
 * the web like Google and Yahoo maps, satellite data such as NASA imagery, and
 * also with file based sources like pre-processed NASA images. A known map
 * provider can be used with the SLMapServerInfo, which will connect to a 2km
 * resolution version of NASA's Blue Marble Next Generation imagery. @see
 * SLMapServerInfo for more information.
 * 
 * Note, the JXMapViewer has three center point properties. The
 * <B>addressLocation</B> property represents an abstract center of the map.
 * This would usually be something like the first item in a search result. It is
 * a {@link GeoPosition}. The <b>centerPosition</b> property represents the
 * current center point of the map. If the user pans the map then the
 * centerPosition point will change but the <B>addressLocation</B> will not.
 * Calling <B>recenterToAddressLocation()</B> will move the map back to that
 * center address. The <B>center</B> property represents the same point as the
 * centerPosition property, but as a Point2D in pixel space instead of a
 * GeoPosition in lat/long space. Note that the center property is a Point2D in
 * the entire world bitmap, not in the portion of the map currently visible. You
 * can use the <B>getViewportBounds()</B> method to find the portion of the map
 * currently visible and adjust your calculations accordingly. Changing the
 * <B>center</B> property will change the <B>centerPosition</B> property and
 * vice versa. All three properties are bound.
 * 
 * @author Joshua.Marinacci@sun.com
 * @see org.jdesktop.swingx.mapviewer.bmng.SLMapServerInfo
 */
public class JXMapViewerLWUIT extends Component implements TrackListener {

	private Vector track = null;
	private final boolean isNegativeYAllowed = true;
	/**
	 * The zoom level. Generally a value between 1 and 15 (TODO Is this true for
	 * all the mapping worlds? What does this mean if some mapping system
	 * doesn't support the zoom level?
	 */
	private int zoom = 1;

	/**
	 * The position, in <I>map coordinates</I> of the center point. This is
	 * defined as the distance from the top and left edges of the map in pixels.
	 * Dragging the map component will change the center position. Zooming
	 * in/out will cause the center to be recalculated so as to remain in the
	 * center of the new "map".
	 */
	private Point2D center = new Point2D(0, 0);

	/**
	 * Factory used by this component to grab the tiles necessary for painting
	 * the map.
	 */
	private TileFactory factory;

	/**
	 * The position in latitude/longitude of the "address" being mapped. This is
	 * a special coordinate that, when moved, will cause the map to be moved as
	 * well. It is separate from "center" in that "center" tracks the current
	 * center (in pixels) of the viewport whereas this will not change when
	 * panning or zooming. Whenever the addressLocation is changed, however, the
	 * map will be repositioned.
	 */
	private GeoPosition addressLocation;

	private Image loadingImage;
	private Image cursorImage;

	/**
	 * Create a new JXMapViewer. By default it will use the EmptyTileFactory
	 */
	public JXMapViewerLWUIT() {
		setFocusable(true);
//		factory = new GoogleMapTileFactory();
		factory = new OpenStreetMapMapnikTileFactory();
		setZoom(11);
		setAddressLocation(new GeoPosition(51.5, 0));

		// make a dummy loading image
		try {
			this.setLoadingImage(Image.createImage("/loading.png"));
		} catch (Throwable ex) {
			System.out.println("could not load 'loading.png'");
			Image img = Image.createImage(16, 16);
			Graphics g = img.getGraphics();
			g.setColor(0xffffff);
			g.fillRect(0, 0, 16, 16);
			this.setLoadingImage(img);
		}
		try {
			cursorImage = Image.createImage("/cursor.png");
		} catch (IOException e) {
			System.out.println("could not load 'cursor.png'");
		}
	}

	public void paint(Graphics g) {
		doPaintComponent(g);
	}

	// the method that does the actual painting
	private void doPaintComponent(Graphics g) {
		int zoom = getZoom();
		Rectangle viewportBounds = getViewportBounds();
		drawMapTiles(g, zoom, viewportBounds);
		if ((track != null) && (track.size() > 2)) {
			g.setColor(0xff0000);
			TrackPoint tp = (TrackPoint) track.elementAt(0);
			Point2D p = convertGeoPositionToPoint(new GeoPosition(tp
					.getLatitude(), tp.getLongitude()));
			int xs = (int) p.getX();
			int ys = (int) p.getY();
			for (int i = 1; i < track.size(); i++) {
				TrackPoint tq = (TrackPoint) track.elementAt(i);
				Point2D q = convertGeoPositionToPoint(new GeoPosition(tq
						.getLatitude(), tq.getLongitude()));
				int x = (int) q.getX();
				int y = (int) q.getY();
				g.drawLine(xs, ys, x, y);
				xs = x;
				ys = y;
			}
		}
		int xc = (int) (viewportBounds.width / 2 - (cursorImage.getWidth() - 1) / 2);
		int yc = (int) (viewportBounds.height / 2 - (cursorImage.getHeight() - 1) / 2);
		g.drawImage(cursorImage, xc, yc);
	}

	/**
	 * Draw the map tiles. This method is for implementation use only.
	 * 
	 * @param g
	 *            Graphics
	 * @param zoom
	 *            zoom level to draw at
	 * @param viewportBounds
	 *            the bounds to draw within
	 */
	protected void drawMapTiles(final Graphics g, final int zoom,
			Rectangle viewportBounds) {
		int size = getTileFactory().getTileSize(zoom);
		Dimension mapSize = getTileFactory().getMapSize(zoom);

		// calculate the "visible" viewport area in tiles
		int numWide = (int) viewportBounds.width / size + 2;
		int numHigh = (int) viewportBounds.height / size + 2;

		TileFactoryInfo info = getTileFactory().getInfo();
		int tpx = (int) Math.floor(viewportBounds.getX()
				/ info.getTileSize(zoom));
		int tpy = (int) Math.floor(viewportBounds.getY()
				/ info.getTileSize(zoom));

		// fetch the tiles from the factory and store them in the tiles cache
		// attach the tileLoadListener
		for (int x = 0; x <= numWide; x++) {
			for (int y = 0; y <= numHigh; y++) {
				int itpx = x + tpx;// topLeftTile.getX();
				int itpy = y + tpy;// topLeftTile.getY();
				// only proceed if the specified tile point lies within the area
				// being painted
				Rectangle clip = new Rectangle(g.getClipX(), g.getClipY(), g
						.getClipWidth(), g.getClipHeight());
				if (clip.intersects(new Rectangle(itpx * size
						- viewportBounds.x, itpy * size - viewportBounds.y,
						size, size))) {
					Tile tile = getTileFactory().getTile(itpx, itpy, zoom);
					tile.addUniquePropertyChangeListener(tileLoadListener);
					int ox = (int) ((itpx * getTileFactory().getTileSize(zoom)) - viewportBounds.x);
					int oy = (int) ((itpy * getTileFactory().getTileSize(zoom)) - viewportBounds.y);

					// if the tile is off the map to the north/south, then just
					// don't paint anything
					if (isTileOnMap(itpx, itpy, mapSize)) {
						// if (isOpaque()) {
						// g.setColor(getBackground());
						// g.fillRect(ox, oy, size, size);
						// }
					} else if (tile.isLoaded()) {
						g.drawImage(tile.getImage(), ox, oy);
					} else {
						int imageX = (getTileFactory().getTileSize(zoom) - getLoadingImage()
								.getWidth()) / 2;
						int imageY = (getTileFactory().getTileSize(zoom) - getLoadingImage()
								.getHeight()) / 2;
						g.setColor(0x808080);
						g.fillRect(ox, oy, size, size);
						g
								.drawImage(getLoadingImage(), ox + imageX, oy
										+ imageY);
					}
				}
			}
		}
	}

	private boolean isTileOnMap(int x, int y, Dimension mapSize) {
		return !isNegativeYAllowed && y < 0 || y >= mapSize.getHeight();
	}

	/**
	 * Returns the bounds of the viewport in pixels. This can be used to
	 * transform points into the world bitmap coordinate space.
	 * 
	 * @return the bounds in <em>pixels</em> of the "view" of this map
	 */
	public Rectangle getViewportBounds() {
		return calculateViewportBounds(getCenter());
	}

	private Rectangle calculateViewportBounds(Point2D center) {
		// calculate the "visible" viewport area in pixels
		Style style = getStyle();
		int viewportWidth = getWidth() - style.getPadding(LEFT)
				- style.getPadding(RIGHT);
		int viewportHeight = getHeight() - style.getPadding(TOP)
				- style.getPadding(BOTTOM);
		double viewportX = (center.getX() - viewportWidth / 2);
		double viewportY = (center.getY() - viewportHeight / 2);
		return new Rectangle((int) viewportX, (int) viewportY, viewportWidth,
				viewportHeight);
	}

	/**
	 * Set the current zoom level
	 * 
	 * @param zoom
	 *            the new zoom level
	 */
	public void setZoom(int zoom) {
		if (zoom == this.zoom) {
			return;
		}

		TileFactoryInfo info = getTileFactory().getInfo();
		// don't repaint if we are out of the valid zoom levels
		if (info != null
				&& (zoom < info.getMinimumZoomLevel() || zoom > info
						.getMaximumZoomLevel())) {
			return;
		}

		// if(zoom >= 0 && zoom <= 15 && zoom != this.zoom) {
		int oldzoom = this.zoom;
		Point2D oldCenter = getCenter();
		Dimension oldMapSize = getTileFactory().getMapSize(oldzoom);
		this.zoom = zoom;

		Dimension mapSize = getTileFactory().getMapSize(zoom);
		Point2D newCenter = new Point2D(oldCenter.getX()
				* (mapSize.getWidth() / oldMapSize.getWidth()), oldCenter
				.getY()
				* (mapSize.getHeight() / oldMapSize.getHeight()));
		setCenter(newCenter);
	}

	/**
	 * Gets the current zoom level
	 * 
	 * @return the current zoom level
	 */
	public int getZoom() {
		return this.zoom;
	}

	/**
	 * Gets the current address location of the map. This property does not
	 * change when the user pans the map. This property is bound.
	 * 
	 * @return the current map location (address)
	 */
	public GeoPosition getAddressLocation() {
		return addressLocation;
	}

	/**
	 * Gets the current address location of the map
	 * 
	 * @param addressLocation
	 *            the new address location
	 * @see getAddressLocation()
	 */
	public void setAddressLocation(GeoPosition addressLocation) {
		this.addressLocation = addressLocation;
		setCenter(getTileFactory().geoToPixel(addressLocation, getZoom()));
	}

	/**
	 * Re-centers the map to have the current address location be at the center
	 * of the map, accounting for the map's width and height.
	 * 
	 * @see getAddressLocation
	 */
	public void recenterToAddressLocation() {
		setCenter(getTileFactory().geoToPixel(getAddressLocation(), getZoom()));
	}

	/**
	 * A property indicating the center position of the map
	 * 
	 * @param geoPosition
	 *            the new property value
	 */
	public void setCenterPosition(GeoPosition geoPosition) {
		setCenter(getTileFactory().geoToPixel(geoPosition, zoom));
	}

	/**
	 * A property indicating the center position of the map
	 * 
	 * @return the current center position
	 */
	public GeoPosition getCenterPosition() {
		return getTileFactory().pixelToGeo(getCenter(), zoom);
	}

	/**
	 * Get the current factory
	 * 
	 * @return the current property value
	 */
	public TileFactory getTileFactory() {
		return factory;
	}

	/**
	 * Set the current tile factory
	 * 
	 * @param factory
	 *            the new property value
	 */
	public void setTileFactory(TileFactory factory) {
		this.factory = factory;
		this.setZoom(factory.getInfo().getDefaultZoomLevel());
	}

	/**
	 * A property for an image which will be display when an image is still
	 * loading.
	 * 
	 * @return the current property value
	 */
	public Image getLoadingImage() {
		return loadingImage;
	}

	/**
	 * A property for an image which will be display when an image is still
	 * loading.
	 * 
	 * @param loadingImage
	 *            the new property value
	 */
	public void setLoadingImage(Image loadingImage) {
		this.loadingImage = loadingImage;
	}

	/**
	 * Gets the current pixel center of the map. This point is in the global
	 * bitmap coordinate system, not as lat/longs.
	 * 
	 * @return the current center of the map as a pixel value
	 */
	public Point2D getCenter() {
		return center;
	}

	/**
	 * Sets the new center of the map in pixel coordinates.
	 * 
	 * @param center
	 *            the new center of the map in pixel coordinates
	 */
	public void setCenter(Point2D center) {
		if (!this.center.equals(center)) {
			int viewportHeight = getHeight();
			int viewportWidth = getWidth();

			// don't let the user pan over the top edge
			Rectangle newVP = calculateViewportBounds(center);
			if (newVP.getY() < 0) {
				double centerY = viewportHeight / 2;
				center = new Point2D(center.getX(), centerY);
			}

			// don't let the user pan over the left edge
			if (newVP.getX() < 0) {
				double centerX = viewportWidth / 2;
				center = new Point2D(centerX, center.getY());
			}

			// don't let the user pan over the bottom edge
			Dimension mapSize = getTileFactory().getMapSize(getZoom());
			int mapHeight = (int) mapSize.getHeight()
					* getTileFactory().getTileSize(getZoom());
			if (newVP.getY() + newVP.getHeight() > mapHeight) {
				double centerY = mapHeight - viewportHeight / 2;
				center = new Point2D(center.getX(), centerY);
			}

			// don't let the user pan over the right edge
			int mapWidth = (int) mapSize.getWidth()
					* getTileFactory().getTileSize(getZoom());
			if (newVP.getX() + newVP.getWidth() > mapWidth) {
				double centerX = mapWidth - viewportWidth / 2;
				center = new Point2D(centerX, center.getY());
			}

			// if map is to small then just center it vert
			if (mapHeight < newVP.getHeight()) {
				double centerY = mapHeight / 2;// viewportHeight/2;// -
				// mapHeight/2;
				center = new Point2D(center.getX(), centerY);
			}

			// if map is too small then just center it horiz
			if (mapWidth < newVP.getWidth()) {
				double centerX = mapWidth / 2;
				center = new Point2D(centerX, center.getY());
			}
			this.center = center;
			repaint();
		}
	}

	/**
	 * Calculates a zoom level so that all points in the specified set will be
	 * visible on screen. This is useful if you have a bunch of points in an
	 * area like a city and you want to zoom out so that the entire city and
	 * it's points are visible without panning.
	 * 
	 * @param positions
	 *            A set of GeoPositions to calculate the new zoom from
	 */
	/*
	 * public void calculateZoomFrom(Set<GeoPosition> positions) { if
	 * (positions.size() < 2) { return; }
	 * 
	 * int zoom = getZoom(); Rectangle rect = generateBoundingRect(positions,
	 * zoom); int count = 0; while (!getViewportBounds().contains(rect)) {
	 * Point2D center = new Point2D(rect.getX() + rect.getWidth() / 2,
	 * rect.getY() + rect.getHeight() / 2); GeoPosition px =
	 * getTileFactory().pixelToGeo(center, zoom); setCenterPosition(px);
	 * count++; if (count > 30) break;
	 * 
	 * if (getViewportBounds().contains(rect)) { break; } zoom = zoom + 1; if
	 * (zoom > 15) { break; } setZoom(zoom); rect =
	 * generateBoundingRect(positions, zoom); } }
	 * 
	 * private Rectangle generateBoundingRect(final Set<GeoPosition> positions,
	 * int zoom) { Point2D point1 = getTileFactory().geoToPixel(
	 * positions.iterator().next(), zoom); Rectangle rect = new
	 * Rectangle(point1.getX(), point1.getY(), 0, 0);
	 * 
	 * for (GeoPosition pos : positions) { Point2D point =
	 * getTileFactory().geoToPixel(pos, zoom); rect.add(point); } return rect; }
	 */

	// a property change listener which forces repaints when tiles finish
	// loading
	private TileLoadListener tileLoadListener = new TileLoadListener();

	private final class TileLoadListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if ("loaded".equals(evt.getPropertyName())
					&& Boolean.TRUE.equals(evt.getNewValue())) {
				Tile t = (Tile) evt.getSource();
				if (t.getZoom() == getZoom()) {
					repaint();
				}
			}
		}
	}

	/**
	 * Converts the specified GeoPosition to a point in the JXMapViewer's local
	 * coordinate space. This method is especially useful when drawing lat/long
	 * positions on the map.
	 * 
	 * @param pos
	 *            a GeoPosition on the map
	 * @return the point in the local coordinate space of the map
	 */
	public Point2D convertGeoPositionToPoint(GeoPosition pos) {
		// convert from geo to world bitmap
		Point2D pt = getTileFactory().geoToPixel(pos, getZoom());
		// convert from world bitmap to local
		Rectangle bounds = getViewportBounds();
		return new Point2D(pt.getX() - bounds.getX(), pt.getY() - bounds.getY());
	}

	/**
	 * Converts the specified Point2D in the JXMapViewer's local coordinate
	 * space to a GeoPosition on the map. This method is especially useful for
	 * determining the GeoPosition under the mouse cursor.
	 * 
	 * @param pt
	 *            a point in the local coordinate space of the map
	 * @return the point converted to a GeoPosition
	 */
	public GeoPosition convertPointToGeoPosition(Point2D pt) {
		// convert from local to world bitmap
		Rectangle bounds = getViewportBounds();
		Point2D pt2 = new Point2D(pt.getX() + bounds.getX(), pt.getY()
				+ bounds.getY());

		// convert from world bitmap to geo
		GeoPosition pos = getTileFactory().pixelToGeo(pt2, getZoom());
		return pos;
	}

	protected com.sun.lwuit.geom.Dimension calcPreferredSize() {
		return new com.sun.lwuit.geom.Dimension(200, 250);
	}

	protected String getUIID() {
		return "MapViewer";
	}

	public void trackChanged(Vector track) {
		System.out.println("Track changed in JXMap");
		this.track = track;
		repaint();
	}

}
