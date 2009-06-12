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
package org.schreibubi.kartlaegga.extend;

import java.util.Hashtable;

/**
 * LinkedHashMap is a variant on HashMap. Its entries are kept in a
 * doubly-linked list. The iteration order is, by default, the order in which
 * keys were inserted.
 * <p>
 * If the three argument constructor is used, and <code>order</code> is
 * specified as <code>true</code>, the iteration would be in the order that
 * entries were accessed. The access order gets affected by put(), get(),
 * putAll() operations, but not by operations on the collection views.
 * <p>
 * Null elements are allowed, and all the optional Map operations are supported.
 * <p>
 * 
 * @since 1.4
 */
public class LinkedHashMap {

	Hashtable hashMap = new Hashtable();
	private static final long serialVersionUID = 3801124242820219131L;

	transient private LinkedHashMapEntry head, tail;

	/**
	 * Constructs a new empty instance of LinkedHashMap.
	 */
	public LinkedHashMap() {
		head = null;
	}

	public int size() {
		return hashMap.size();
	}

	public KeyIterator iterator() {
		return new KeyIterator(this);
	}
	
	public static final class LinkedHashMapEntry {
		Object key;
		public Object getKey() {
			return key;
		}

		public void setKey(Object key) {
			this.key = key;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		Object value;
		LinkedHashMapEntry chainForward, chainBackward;

		LinkedHashMapEntry(Object theKey, Object theValue) {
			key = theKey;
			value = theValue;
			chainForward = null;
			chainBackward = null;
		}

	}

	/**
	 * Retrieve the map value corresponding to the given key.
	 * 
	 * @param key
	 *            Key value
	 * @return mapped value or null if the key is not in the map
	 */
	public Object get(Object key) {
		LinkedHashMapEntry m;
		m = (LinkedHashMapEntry) hashMap.get(key);
		if (m == null) {
			return null;
		}
		if (tail != m) {
			LinkedHashMapEntry p = m.chainBackward;
			LinkedHashMapEntry n = m.chainForward;
			n.chainBackward = p;
			if (p != null) {
				p.chainForward = n;
			} else {
				head = n;
			}
			m.chainForward = null;
			m.chainBackward = tail;
			tail.chainForward = m;
			tail = m;
		}
		return m.value;
	}

	/**
	 * Set the mapped value for the given key to the given value.
	 * 
	 * @param key
	 *            Key value
	 * @param value
	 *            New mapped value
	 */
	public void put(Object key, Object value) {
		LinkedHashMapEntry m = new LinkedHashMapEntry(key, value);
		linkEntry(m);
		hashMap.put(key, m);

		if (removeEldestEntry(head)) {
			remove(head.key);
		}

	}

	/*
	 * @param m
	 */
	void linkEntry(LinkedHashMapEntry m) {
		if (tail == m) {
			return;
		}

		if (head == null) {
			// Check if the map is empty
			head = tail = m;
			return;
		}

		// we need to link the new entry into either the head or tail
		// of the chain depending on if the LinkedHashMap is accessOrder or not
		LinkedHashMapEntry p = m.chainBackward;
		LinkedHashMapEntry n = m.chainForward;
		if (p == null) {
			if (n != null) {
				// The entry must be the head but not the tail
				head = n;
				n.chainBackward = null;
				m.chainBackward = tail;
				m.chainForward = null;
				tail.chainForward = m;
				tail = m;
			} else {
				// This is a new entry
				m.chainBackward = tail;
				m.chainForward = null;
				tail.chainForward = m;
				tail = m;
			}
			return;
		}

		if (n == null) {
			// The entry must be the tail so we can't get here
			return;
		}

		// The entry is neither the head nor tail
		p.chainForward = n;
		n.chainBackward = p;
		m.chainForward = null;
		m.chainBackward = tail;
		tail.chainForward = m;
		tail = m;
	}

	/**
	 * This method is queried from the put and putAll methods to check if the
	 * eldest member of the map should be deleted before adding the new member.
	 * If this map was created with accessOrder = true, then the result of
	 * removeEldesrEntry is assumed to be false.
	 * 
	 * @param eldest
	 * @return true if the eldest member should be removed
	 */
	protected boolean removeEldestEntry(LinkedHashMapEntry eldest) {
		return false;
	}

	/**
	 * Remove the entry corresponding to the given key.
	 * 
	 * @param key
	 *            the key
	 * @return the value associated with the key or null if the key was no in
	 *         the map
	 */
	public Object remove(Object key) {
		LinkedHashMapEntry m = (LinkedHashMapEntry) hashMap.remove(key);
		if (m == null) {
			return null;
		}
		LinkedHashMapEntry p = m.chainBackward;
		LinkedHashMapEntry n = m.chainForward;
		if (p != null) {
			p.chainForward = n;
		} else {
			head = n;
		}
		if (n != null) {
			n.chainBackward = p;
		} else {
			tail = p;
		}
		return m.value;
	}

	/**
	 * Removes all mappings from this HashMap, leaving it empty.
	 * 
	 * @see #isEmpty()
	 * @see #size()
	 */
	public void clear() {
		hashMap.clear();
		head = tail = null;
	}

	public static class KeyIterator {
		LinkedHashMapEntry futureEntry;
		LinkedHashMapEntry currentEntry;
		final LinkedHashMap associatedMap;

		KeyIterator(LinkedHashMap map) {
			futureEntry = map.head;
			associatedMap = map;
		}

		public boolean hasNext() {
			return (futureEntry != null);
		}

		final void makeNext() throws Exception {
			if (!hasNext()) {
				throw new Exception();
			}
			currentEntry = futureEntry;
			futureEntry = futureEntry.chainForward;
		}

		public void remove() throws Exception {
			if (currentEntry == null) {
				throw new Exception();
			}
			associatedMap.remove(currentEntry);
			currentEntry = null;
		}
	       public Object next() throws Exception {
	            makeNext();
	            return currentEntry.key;
	        }

	}

}
