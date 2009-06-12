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
 * DefaultTileFactory.java
 *
 * Created on June 27, 2006, 2:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.schreibubi.kartlaegga.mapviewer.tilefactories;


/**
 * A tile factory which configures itself using a TileFactoryInfo object and uses a Google Maps like mercator projection.
 * @author joshy
 */
public class DefaultTileFactory extends AbstractTileFactory {
    
    /**
     * Creates a new instance of DefaultTileFactory using the spcified TileFactoryInfo
     * @param info a TileFactoryInfo to configure this TileFactory
     */
    public DefaultTileFactory(TileFactoryInfo info) {
        super(info);
    }
        
}
