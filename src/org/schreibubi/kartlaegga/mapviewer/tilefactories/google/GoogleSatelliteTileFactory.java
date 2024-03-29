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
public class GoogleSatelliteTileFactory extends AbstractTileFactory {
	 
	/**
	 * @param info
	 */
	public GoogleSatelliteTileFactory() {
		super(new TileFactoryInfo("Google","Satellite",8, 28, 28, 256, true, true,
				"http://kh0.google.com/kh?n=404&v=25&t=","") {
			public String getCoordinatePart(int x, int y, int zoom) {
				zoom = 25 - zoom;
		        return "&x=" + x +
                "&y=" + y +
                "&zoom=" + zoom;
			}
		});
	}

}
