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
 * GeoUtil.java
 *
 * Created on June 26, 2006, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.schreibubi.kartlaegga.mapviewer;

import org.schreibubi.kartlaegga.extend.Float11;
import org.schreibubi.kartlaegga.extend.Point2D;
import org.schreibubi.kartlaegga.mapviewer.tilefactories.TileFactoryInfo;


/**
 * These are math utilities for converting between pixels, tiles, and geographic
 * coordinates. Implements a Google Maps style mercator projection.
 * @author joshy
 */
public final class GeoUtil {
        
    /**
     * @return the size of the map at the given zoom, in tiles (num tiles tall
     *         by num tiles wide)
     */
    public static Dimension getMapSize(int zoom, TileFactoryInfo info) {
        return new Dimension(info.getMapWidthInTilesAtZoom(zoom), info.getMapWidthInTilesAtZoom(zoom));
    }
    
    /**
     * @returns true if this point in <em>tiles</em> is valid at this zoom level. For example,
     * if the zoom level is 0 (zoomed all the way out, where there is only
     * one tile), then x,y must be 0,0
     */
    public static boolean isValidTile(int x, int y, int zoomLevel, TileFactoryInfo info ) {
        //int x = (int)coord.getX();
        //int y = (int)coord.getY();
        // if off the map to the top or left
        if(x < 0 || y < 0) {
            return false;
        }
        // if of the map to the right
        if(info.getMapCenterInPixelsAtZoom(zoomLevel).getX()*2 <= x*info.getTileSize(zoomLevel)) {
            return false;
        }
        // if off the map to the bottom
        if(info.getMapCenterInPixelsAtZoom(zoomLevel).getY()*2 <= y*info.getTileSize(zoomLevel)) {
            return false;
        }
        //if out of zoom bounds
        if(zoomLevel < info.getMinimumZoomLevel() || zoomLevel > info.getMaximumZoomLevel()) {
            return false;
        }
        return true;
    }
    /**
     * Given a position (latitude/longitude pair) and a zoom level, return
     * the appropriate point in <em>pixels</em>. The zoom level is necessary because
     * pixel coordinates are in terms of the zoom level
     * 
     * 
     * @param c A lat/lon pair
     * @param zoomLevel the zoom level to extract the pixel coordinate for
     */
    public static Point2D getBitmapCoordinate(GeoPosition c, int zoomLevel, TileFactoryInfo info) {
        return getBitmapCoordinate(c.getLatitude(), c.getLongitude(), zoomLevel, info);
    }
    
    /**
     * Given a position (latitude/longitude pair) and a zoom level, return
     * the appropriate point in <em>pixels</em>. The zoom level is necessary because
     * pixel coordinates are in terms of the zoom level
     * 
     * 
     * @param double latitude
     * @param double longitude
     * @param zoomLevel the zoom level to extract the pixel coordinate for
     */
    public static Point2D getBitmapCoordinate(
            double latitude, 
            double longitude,
            int zoomLevel, TileFactoryInfo info) {
        
        double x = info.getMapCenterInPixelsAtZoom(zoomLevel).getX() + longitude
                * info.getLongitudeDegreeWidthInPixels(zoomLevel);
        double e = Math.sin(latitude * (Math.PI / 180.0));
        if (e > 0.9999) {
            e = 0.9999;
        }
        if (e < -0.9999) {
            e = -0.9999;
        }
        double y = info.getMapCenterInPixelsAtZoom(zoomLevel).getY() + 0.5
                * Float11.log((1 + e) / (1 - e)) * -1
                * (info.getLongitudeRadianWidthInPixels(zoomLevel));
        return new Point2D(x, y);
    }
    
        
    // convert an on screen pixel coordinate and a zoom level to a
    // geo position
    public static GeoPosition getPosition(Point2D pixelCoordinate, int zoom, TileFactoryInfo info) {
        //        p(" --bitmap to latlon : " + coord + " " + zoom);
        double wx = pixelCoordinate.getX();
        double wy = pixelCoordinate.getY();
        // this reverses getBitmapCoordinates
        double flon = (wx - info.getMapCenterInPixelsAtZoom(zoom).getX())
                / info.getLongitudeDegreeWidthInPixels(zoom);
        double e1 = (wy - info.getMapCenterInPixelsAtZoom(zoom).getY())
                / (-1 * info.getLongitudeRadianWidthInPixels(zoom));
        double e2 = (2 * Float11.atan(Float11.exp(e1)) - Math.PI / 2) / (Math.PI / 180.0);
        double flat = e2;
        GeoPosition wc = new GeoPosition(flat, flon);
        return wc;
    }
    
       
}
