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
package org.schreibubi.kartlaegga.mapviewer;

public class PropertyChangeEvent {

	Object source, oldValue, newValue;
	String propertyName;

	public Object getSource() {
		return source;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public PropertyChangeEvent(Object source, String propertyName,
			Object oldValue, Object newValue) {
		this.source=source;
		this.oldValue=oldValue;
		this.newValue=newValue;
		this.propertyName=propertyName;
	}

}
