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
package org.schreibubi.kartlägga.mapviewer.tilefactories.microsoft;

import org.schreibubi.kartlägga.mapviewer.tilefactories.AbstractTileFactory;
import org.schreibubi.kartlägga.mapviewer.tilefactories.TileFactoryInfo;

/**
 * @author joerg
 */
public class AbstractVirtualEarthTileFactory extends AbstractTileFactory {
	private static final int PROTOCOL_VERSION = 117;

	public AbstractVirtualEarthTileFactory( String name, String baseUrl) {
		super(new TileFactoryInfo("Microsoft", name, 9, 25, 25, 256, true, true,
				baseUrl, "?g=" + PROTOCOL_VERSION)
		{
			public String getCoordinatePart(int x, int y, int zoom) {
				StringBuffer id = new StringBuffer();
				for (int z = zoom; z >= getMinimumZoomLevel(); z--) {
					if (x % 2 != 0) {
						if (y % 2 != 0) {
							id.insert(0, '3');
						} else {
							id.insert(0, '1');
						}
					} else {
						if (y % 2 != 0) {
							id.insert(0, '2');
						} else {
							id.insert(0, '0');
						}
					}
					x /= 2;
					y /= 2;
				}
				return id.toString();
			}

		});
	}
}