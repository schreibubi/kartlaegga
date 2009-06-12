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
package org.schreibubi.kartlaegga.mapviewer.cache;

import org.schreibubi.kartlaegga.extend.LinkedHashMap;
import org.schreibubi.kartlaegga.extend.LinkedHashMap.KeyIterator;

/**
 * @author joerg
 *
 */
public class LRUDiskCache extends AbstractLRUCache {

	public LRUDiskCache(LRUCacheInterface child) {
		super(child);
	}

	final static int MAX_CACHE_TILES=20;
	LinkedHashMap cache = new LinkedHashMap() {
		protected boolean removeEldestEntry(LinkedHashMapEntry eldest) {
			if (cache.size() > MAX_CACHE_TILES) {
				if (child != null) {
					child.put(eldest.getKey(), eldest.getValue());
				}
				return true;
			} else
				return false;
		}
	};


	public Object get(Object key) {
		Object value = cache.get(key);
		if (value == null) {
			if (child != null) {
				value = child.get(key);
			}
		}
		return value;
	}

	public void put(Object key, Object value) {
		cache.put(key, value);
	}

	public void flush() throws Exception {
		for (KeyIterator k = cache.iterator(); k.hasNext();) {
			Object key = k.next();
			if (child!=null) {
				child.put(key, cache.get(key));
			}
		}

	}

}
