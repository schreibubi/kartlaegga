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
package org.schreibubi.kartlägga.mapviewer.tilefactories.openstreetmap;

import org.schreibubi.kartlägga.mapviewer.tilefactories.AbstractTileFactory;
import org.schreibubi.kartlägga.mapviewer.tilefactories.TileFactoryInfo;

/**
 * @author joerg
 */
public class AbstractOpenStreetMapTileFactory extends AbstractTileFactory {
	/**
	 * Creates a new instance of TileFactory
	 */
	public AbstractOpenStreetMapTileFactory(String name, String baseUrl) {
		super(new TileFactoryInfo("OpenStreetMap",name,1, 17 - 2, 17, 256, true, true,
				baseUrl, ".png") {
			public String getCoordinatePart(int x, int y, int zoom) {
				zoom = 17 - zoom;
				return "" + zoom + "/" + x + "/" + y;
			}
		});
	}
}
