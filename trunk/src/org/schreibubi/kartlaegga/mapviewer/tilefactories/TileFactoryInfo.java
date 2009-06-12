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
 * TileFactoryInfo.java
 *
 * Created on June 26, 2006, 10:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.schreibubi.kartlaegga.mapviewer.tilefactories;

import org.schreibubi.kartlaegga.extend.Point2D;


/**
 * A TileFactoryInfo encapsulates all information 
 * specific to a map server. This includes everything from
 * the url to load the map tiles from to the size and depth
 * of the tiles. Theoretically any map server can be used
 * by installing a customized TileFactoryInfo. Currently
 * 
 * @author joshy
 */
public abstract class TileFactoryInfo {
    private int minimumZoomLevel;
    private int maximumZoomLevel;
    private int totalMapZoom;
    // the size of each tile (assumes they are square)
    private int tileSize = 256;
    
    /*
     * The number of tiles wide at each zoom level
     */
    private int[] mapWidthInTilesAtZoom;
   /**
     * An array of coordinates in <em>pixels</em> that indicates the center in the
     * world map for the given zoom level.
     */
    private Point2D[] mapCenterInPixelsAtZoom;// = new Point2D.Double[18];
    
    /**
     * An array of doubles that contain the number of pixels per degree of
     * longitude at a give zoom level.
     */
    private double[] longitudeDegreeWidthInPixels;
     
    /**
     * An array of doubles that contain the number of radians per degree of
     * longitude at a given zoom level (where longitudeRadianWidthInPixels[0] is 
     * the most zoomed out)
     */
    private double[] longitudeRadianWidthInPixels;
    
    /**
     * The base url for loading tiles from.
     */
    private String baseUrl;
    private String urlSuffix;
    private boolean xr2l = true;
    private boolean yt2b = true;
    
    private int defaultZoomLevel;
    
    /** A name for this info. */
    private String name;
	private String provider;
    
    
    /**
     * Creates a new instance of TileFactoryInfo. Note that TileFactoryInfo
     * should be considered invariate, meaning that subclasses should
     * ensure all of the properties stay the same after the class is
     * constructed. Returning different values of getTileSize() for example
     * is considered an error and may result in unexpected behavior.
     * 
	 * @param name A name to identify this information.
     * @param minimumZoomLevel The minimum zoom level
     * @param maximumZoomLevel the maximum zoom level
     * @param totalMapZoom the top zoom level, essentially the height of the pyramid
     * @param tileSize the size of the tiles in pixels (must be square)
     * @param xr2l if the x goes r to l (is this backwards?)
     * @param yt2b if the y goes top to bottom
     * @param baseURL the base url for grabbing tiles
     * @param xparam the x parameter for the tile url
     * @param yparam the y parameter for the tile url
     * @param zparam the z parameter for the tile url
     */
    /*
     * @param xr2l true if tile x is measured from the far left of the map to the far right, or
     * else false if based on the center line. 
     * @param yt2b true if tile y is measured from the top (north pole) to the bottom (south pole)
     * or else false if based on the equator.
     */
    public TileFactoryInfo(String provider,String name, int minimumZoomLevel, int maximumZoomLevel, int totalMapZoom, 
            int tileSize, boolean xr2l, boolean yt2b,
            String baseUrl, String urlSuffix) {
    	this.setProvider(provider);
    	this.name = name;
        this.minimumZoomLevel = minimumZoomLevel;
        this.maximumZoomLevel = maximumZoomLevel;
        this.totalMapZoom = totalMapZoom;
        this.baseUrl = baseUrl;
        this.urlSuffix=urlSuffix;
        this.setXr2l(xr2l);
        this.setYt2b(yt2b);
                
        this.tileSize = tileSize;
        
        // init the num tiles wide
        int tilesize = this.getTileSize(0);

        longitudeDegreeWidthInPixels = new double[totalMapZoom+1];
        longitudeRadianWidthInPixels = new double[totalMapZoom+1];
        mapCenterInPixelsAtZoom = new Point2D[totalMapZoom+1];
        mapWidthInTilesAtZoom = new int[totalMapZoom+1];
    
        // for each zoom level
        for (int z = totalMapZoom; z >= 0; --z) {
            // how wide is each degree of longitude in pixels
            longitudeDegreeWidthInPixels[z] = (double)tilesize / 360;
            // how wide is each radian of longitude in pixels
            longitudeRadianWidthInPixels[z] = (double)tilesize / (2.0*Math.PI);
            int t2 = tilesize / 2;
            mapCenterInPixelsAtZoom[z] = new Point2D(t2, t2);
            mapWidthInTilesAtZoom[z] = tilesize / this.getTileSize(0);
            tilesize *= 2;
        }

    }

    /**
     * 
     * @return 
     */
    public int getMinimumZoomLevel() {
        return minimumZoomLevel;
    }

    /**
     * 
     * @return 
     */
    public int getMaximumZoomLevel() {
        return maximumZoomLevel;
    }

    /**
     * 
     * @return 
     */
    public int getTotalMapZoom() {
        return totalMapZoom;
    }

    /**
     * 
     * @param zoom 
     * @return 
     */
    public int getMapWidthInTilesAtZoom(int zoom) {
        return mapWidthInTilesAtZoom[zoom];
    }

    /**
     * 
     * @param zoom 
     * @return 
     */
    public Point2D getMapCenterInPixelsAtZoom(int zoom) {
        return mapCenterInPixelsAtZoom[zoom];
    }

    
    /**
     * Returns the tile url for the specified tile at the specified 
     * zoom level. By default it will generate a tile url using the
     * base url and parameters specified in the constructor. Thus if
     * 
     * @param zoom the zoom level
     * @param tilePoint the tile point
     * @return a valid url to load the tile 
     */
    
    public abstract String getCoordinatePart(int x, int y, int zoom);
    
    /**
     * Get the tile size.
     * @return the tile size
     */
    public int getTileSize(int zoom) {
        return tileSize;
    }

    /**
     * 
     * @param zoom 
     * @return 
     */
    public double getLongitudeDegreeWidthInPixels(int zoom) {
        return longitudeDegreeWidthInPixels[zoom];
    }

    /**
     * 
     * @param zoom 
     * @return 
     */
    public double getLongitudeRadianWidthInPixels(int zoom) {
        return longitudeRadianWidthInPixels[zoom];
    }

    /**
     * A property indicating if the X coordinates of tiles go
     * from right to left or left to right.
     * @return 
     */
    public boolean isXr2l() {
        return xr2l;
    }

    /**
     * A property indicating if the X coordinates of tiles go
     * from right to left or left to right.
     * @param xr2l 
     */
    public void setXr2l(boolean xr2l) {
        this.xr2l = xr2l;
    }

    /**
     * A property indicating if the Y coordinates of tiles go
     * from right to left or left to right.
     * @return 
     */
    public boolean isYt2b() {
        return yt2b;
    }

    /**
     * A property indicating if the Y coordinates of tiles go
     * from right to left or left to right.
     * @param yt2b 
     */
    public void setYt2b(boolean yt2b) {
        this.yt2b = yt2b;
    }

    public int getDefaultZoomLevel() {
        return defaultZoomLevel;
    }

    public void setDefaultZoomLevel(int defaultZoomLevel) {
        this.defaultZoomLevel = defaultZoomLevel;
    }
    
	/**
	 * The name of this info.
	 * 
	 * @return Returns the name of this info class for debugging or GUI widgets.
	 */
	public String getName() {
		return name;
	}

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProvider() {
		return provider;
	}

}