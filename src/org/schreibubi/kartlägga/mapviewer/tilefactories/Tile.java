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
/*
 * Tile.java
 *
 * Created on March 14, 2006, 4:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.schreibubi.kartlägga.mapviewer.tilefactories;

import org.schreibubi.kartlägga.mapviewer.PropertyChangeEvent;
import org.schreibubi.kartlägga.mapviewer.PropertyChangeListener;

import com.sun.lwuit.Image;


/**
 * The Tile class represents a particular square image piece of the world bitmap
 * at a particular zoom level.
 * 
 * @author joshy
 */

public class Tile {
	public final static int HIGH = 1;
	public final static int LOW = 0;
	private int priority = HIGH;

	private boolean isLoading = false;

	/**
	 * If an error occurs while loading a tile, store the exception here.
	 */
	private Throwable error;

	/**
	 * The url of the image to load for this tile
	 */
	private String url;

	/**
	 * Indicates that loading has succeeded. A PropertyChangeEvent will be fired
	 * when the loading is completed
	 */

	private boolean loaded = false;
	/**
	 * The zoom level this tile is for
	 */
	private int zoom, x, y;

	/**
	 * The image loaded for this Tile
	 */
	public Image image = null;

	/**
	 * Create a new Tile at the specified tile point and zoom level
	 * 
	 * @param location
	 * @param zoom
	 */
	public Tile(int x, int y, int zoom) {
		loaded = false;
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new Tile that loads its data from the given URL. The URL must
	 * resolve to an image
	 */
	public Tile(int x, int y, int zoom, String url, int priority, TileFactory dtf) {
		this.url = url;
		loaded = false;
		this.zoom = zoom;
		this.x = x;
		this.y = y;
		this.priority = priority;
		this.dtf = dtf;
		// startLoading();
	}

	/**
	 * 
	 * Indicates if this tile's underlying image has been successfully loaded
	 * yet.
	 * 
	 * @returns true if the Tile has been loaded
	 * @return
	 */
	public synchronized boolean isLoaded() {
		return loaded;
	}

	/**
	 * Toggles the loaded state, and fires the appropriate property change
	 * notification
	 */
	public synchronized void setLoaded(boolean loaded) {
		boolean old = isLoaded();
		this.loaded = loaded;
		firePropertyChange("loaded", new Boolean(old), new Boolean(isLoaded()));
	}

	/**
	 * Returns the last error in a possible chain of errors that occured during
	 * the loading of the tile
	 */
	public Throwable getUnrecoverableError() {
		return error;
	}

	/**
	 * Returns the Throwable tied to any error that may have ocurred while
	 * loading the tile. This error may change several times if multiple errors
	 * occur
	 * 
	 * @return
	 */
	public Throwable getLoadingError() {
		return error;
	}

	/**
	 * Returns the Image associated with this Tile. This is a read only property
	 * This may return null at any time, however if this returns null, a load
	 * operation will automatically be started for it.
	 */
	public Image getImage() {
		if (image == null) {
			setLoaded(false);
			dtf.startLoading(this);
		}

		return image;
	}

	/**
	 * @return the location in the world at this zoom level that this tile
	 *         should be placed
	 */
	/*
	 * public TilePoint getLocation() { return location; }
	 */

	/**
	 * @return the zoom level that this tile belongs in
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * Gets the loading priority of this tile.
	 * 
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Set the loading priority of this tile.
	 * 
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	private PropertyChangeListener uniqueListener = null;

	/**
	 * Adds a single property change listener. If a listener has been previously
	 * added then it will be replaced by the new one.
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addUniquePropertyChangeListener(PropertyChangeListener listener) {
		if (uniqueListener != listener) {
			uniqueListener = listener;
		}
	}

	public void firePropertyChange(final String propertyName, final Object oldValue,
			final Object newValue) {
		final Tile t=this;
		Thread execThread = new Thread() {
			public void run() {
				PropertyChangeEvent pce = new PropertyChangeEvent(t,
						propertyName, oldValue, newValue);
				uniqueListener.propertyChange(pce);
			}
		};
		execThread.start();
	}

	/**
	 * @return the error
	 */
	public Throwable getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(Throwable error) {
		this.error = error;
	}

	/**
	 * @return the isLoading
	 */
	public boolean isLoading() {
		return isLoading;
	}

	/**
	 * @param isLoading
	 *            the isLoading to set
	 */
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	/**
	 * Gets the loading priority of this tile.
	 * 
	 * @return
	 */
	public int getint() {
		return priority;
	}

	/**
	 * Set the loading priority of this tile.
	 * 
	 * @param priority
	 *            the priority to set
	 */
	public void setint(int priority) {
		this.priority = priority;
	}

	/**
	 * Gets the URL of this tile.
	 * 
	 * @return
	 */
	public String getURL() {
		return url;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	private TileFactory dtf;

}
