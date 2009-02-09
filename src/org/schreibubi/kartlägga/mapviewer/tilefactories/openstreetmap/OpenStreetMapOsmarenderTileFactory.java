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


/**
 * @author sarbogast
 * @version 15 juin 2008, 20:39:22
 */
public class OpenStreetMapOsmarenderTileFactory extends AbstractOpenStreetMapTileFactory {
    public OpenStreetMapOsmarenderTileFactory() {
        super("OpenStreetMapOsmarender", "http://tah.openstreetmap.org/Tiles/tile.php");
    }
}
