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
package org.schreibubi.kartlaegga.mapviewer.track;

import java.util.Vector;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

public class TrackRecorder implements LocationListener {

	private Vector track = new Vector();

	private Vector trackListenerList = new Vector();

	public TrackRecorder() {

	}

	public void addTrackListener(TrackListener tl) {
		trackListenerList.addElement(tl);
	}

	public void removeTrackListener(TrackListener tl) {
		trackListenerList.removeElement(tl);
	}

	public void fireTrackChanged() {
		for (int i = 0; i < trackListenerList.size(); i++) {
			((TrackListener) trackListenerList.elementAt(i))
					.trackChanged(track);
		}

	}

	public void locationUpdated(LocationProvider provider, Location location) {
		Coordinates coordinates = location.getQualifiedCoordinates();
		double latitude = coordinates.getLatitude();
		double longitude = coordinates.getLongitude();
		float altitude = coordinates.getAltitude();
		float sp = location.getSpeed();
		double course = location.getCourse();
		long time=location.getTimestamp();

		TrackPoint tp=new TrackPoint(latitude,longitude,altitude,sp,course,time);
		track.addElement(tp);
		fireTrackChanged();
	}

	public void providerStateChanged(LocationProvider provider, int newState) {
	}

}
