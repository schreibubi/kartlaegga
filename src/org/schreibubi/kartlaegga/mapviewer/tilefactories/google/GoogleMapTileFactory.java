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
/**
 * 
 */
package org.schreibubi.kartlaegga.mapviewer.tilefactories.google;

import org.schreibubi.kartlaegga.mapviewer.tilefactories.AbstractTileFactory;
import org.schreibubi.kartlaegga.mapviewer.tilefactories.TileFactoryInfo;


/**
 * @author joerg
 * 
 */
public class GoogleMapTileFactory extends AbstractTileFactory {

	 private final static String PROTOCOL_VERSION = "2.89";
	 
	/**
	 * @param info
	 */
	public GoogleMapTileFactory() {
		// http://mt1.google.com/mt?v=w2.89&hl=de&x=1085&y=650&z=11&s=G
		super(new TileFactoryInfo("Google","Maps",8, 25, 25, 256, true, true,
				"http://mt1.google.com/mt?v=w" + PROTOCOL_VERSION, "") {
			public String getCoordinatePart(int x, int y, int zoom) {
				zoom = 25 - zoom;
		        return "&x=" + x +
                "&y=" + y +
                "&z=" + zoom;
			}
		});
	}

}
