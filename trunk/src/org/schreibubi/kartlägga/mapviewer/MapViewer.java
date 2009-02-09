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
package org.schreibubi.kartlägga.mapviewer;

import java.util.Date;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import org.schreibubi.kartlägga.extend.Point2D;
import org.schreibubi.kartlägga.extend.Rectangle;
import org.schreibubi.kartlägga.mapviewer.track.TrackRecorder;

import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.FlowLayout;

public class MapViewer extends Form implements LocationListener {

	private static final int OFFSET = 10;

	private boolean tracking = true;

	private static final int BACK_COMMAND = 3;

	private static final Command backCommand = new Command("Back", BACK_COMMAND);

	private Label lat = new Label("--------");
	private Label lon = new Label("--------");
	private Label height = new Label("----");
	private Label speed = new Label("---");
	private Label direction = new Label("---");
	private Label timestamp=new Label("--------");

	private JXMapViewerLWUIT jxMapViewer = null;
	private MapViewer myself=null;
	
	public MapViewer(final MyLocationProviderInterface locationListener,
			final PreviousMenuInterface parent) {
		myself=this;
		setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		jxMapViewer = new JXMapViewerLWUIT();
		addComponent(jxMapViewer);
		setCommandListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Command cmd = evt.getCommand();
				switch (cmd.getId()) {
				case BACK_COMMAND:
					locationListener.removeLocationListener(myself);
					parent.switchToPreviousMenu();
					break;
				}
			}
		});
		addCommand(backCommand);
		setScrollable(false);

		//
//		Container gpsPanel = new Container(new BoxLayout(BoxLayout.X_AXIS));
		Container gpsPanel = new Container(new FlowLayout());
		gpsPanel.addComponent(lat);
		gpsPanel.addComponent(lon);
		gpsPanel.addComponent(height);
		gpsPanel.addComponent(speed);
		gpsPanel.addComponent(direction);
		gpsPanel.addComponent(timestamp);
		
		addComponent(gpsPanel);
		locationListener.addLocationListener(this);
		TrackRecorder tr=new TrackRecorder();
		locationListener.addLocationListener(tr);
		tr.addTrackListener(jxMapViewer);

		show();
	}

	/**
	 * A method which is called by the location provider when the current
	 * location is changed.
	 */
	public synchronized void locationUpdated(LocationProvider provider,
			Location location) {
		System.out.println("MapViewer location updated");
		Coordinates coordinates = location.getQualifiedCoordinates();
		double latitude = coordinates.getLatitude();
		double longitude = coordinates.getLongitude();
		float altitude = coordinates.getAltitude();
		float sp = location.getSpeed();
		double course = location.getCourse();
		long time=location.getTimestamp();
		lat.setText(Coordinates.convert(latitude,Coordinates.DD_MM_SS));
		lon.setText(Coordinates.convert(longitude,Coordinates.DD_MM_SS));
		height.setText(altitude+"m");
		speed.setText(sp*3.6+"km/h");
		direction.setText(course+"°");
		timestamp.setText(new Date(time).toString());
		if (tracking) {
			GeoPosition gp = new GeoPosition(latitude, longitude);
			jxMapViewer.setCenterPosition(gp);
		}

	}

	/**
	 * A method which is called by the location provider when its state changes
	 * (for example, when its services are temporary unavailable).
	 */
	public synchronized void providerStateChanged(LocationProvider provider,
			int newState) {
		System.out.println("Location provider state changed");
	}

	public void keyReleased(int k) {
		super.keyReleased(k);
		int delta_x = 0;
		int delta_y = 0;

		if (k < 0) {
			switch (Display.getInstance().getGameAction(k)) {
			case Display.GAME_DOWN:
				delta_y = OFFSET;
				tracking = false;
				break;
			case Display.GAME_UP:
				delta_y = -OFFSET;
				tracking = false;
				break;
			case Display.GAME_LEFT:
				delta_x = -OFFSET;
				tracking = false;
				break;
			case Display.GAME_RIGHT:
				delta_x = OFFSET;
				tracking = false;
				break;
			}
		} else {
			switch ((char) k) {
			case '1':
				jxMapViewer.setZoom(jxMapViewer.getZoom() - 1);
				break;
			case '3':
				jxMapViewer.setZoom(jxMapViewer.getZoom() + 1);
				break;
			case '4':
				delta_x = -OFFSET;
				tracking = false;
				break;
			case '6':
				delta_x = OFFSET;
				tracking = false;
				break;
			case '2':
				delta_y = -OFFSET;
				tracking = false;
				break;
			case '8':
				delta_y = OFFSET;
				tracking = false;
				break;
			case '0':
				tracking = true;
				break;
			}
		}
		if (delta_x != 0 || delta_y != 0) {
			Rectangle bounds = jxMapViewer.getViewportBounds();
			double x = bounds.getCenterX() + delta_x;
			double y = bounds.getCenterY() + delta_y;
			System.out.println("Old x/y " + bounds.getCenterX() + "/"
					+ bounds.getCenterY() + " new x/y " + x + "/" + y);

			jxMapViewer.setCenter(new Point2D(x, y));
		}
	}

}
