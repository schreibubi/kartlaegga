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
package org.schreibubi.kartlägga.mapviewer.tilefactories.yahoo;


/**
 * @author sarbogast
 * @version 15 juin 2008, 19:53:59
 */
public class YahooSatelliteTileFactory extends AbstractYahooTileFactory {
    public YahooSatelliteTileFactory() {
        super("Satellite", "http://us.maps3.yimg.com/aerial.maps.yimg.com/ximg?v=1.7&t=a");
    }
}
