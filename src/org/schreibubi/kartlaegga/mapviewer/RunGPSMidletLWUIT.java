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

import java.util.Vector;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.midlet.MIDlet;

import org.schreibubi.kartlaegga.extend.ListItem;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;

public class RunGPSMidletLWUIT extends MIDlet implements ActionListener,
		LocationListener, PreviousMenuInterface, MyLocationProviderInterface {
	private static final int EXIT_COMMAND = 1;
	private static final int RUN_COMMAND = 2;
	private static final int BACK_COMMAND = 3;
	private static final int ABOUT_COMMAND = 4;
	private static final Command runCommand = new Command("Run", RUN_COMMAND);
	private static final Command exitCommand = new Command("Exit", EXIT_COMMAND);
	private static final Command backCommand = new Command("Back", BACK_COMMAND);
	private static final Command aboutCommand = new Command("About",
			ABOUT_COMMAND);

	private Form mainMenu = null;
	private Vector locationListenerList = new Vector();
	private RunGPSMidletLWUIT myself = this;

	/** location provider */
	private LocationProvider locationProvider = null;

	public RunGPSMidletLWUIT() {
		createLocationProvider();

		if (locationProvider == null) {
			System.out.println("Cannot run without location provider!");
			destroyApp(false);
			notifyDestroyed();
		}
	}

	private boolean midletPaused = false;
	private Resources themeResource;
	private Resources resource;

	/**
	 * Initilizes the application. It is called only once when the MIDlet is
	 * started. The method is called before the <code>startMIDlet</code> method.
	 */
	private void initialize() {
		try {
			Display.init(this);

			themeResource = Resources.open("/businessTheme.res");
			UIManager.getInstance().setThemeProps(
					themeResource.getTheme(themeResource
							.getThemeResourceNames()[0]));

			resource = Resources.open("/resources.res");
			UIManager.getInstance().setResourceBundle(
					resource.getL10N("localize", "en"));

		} catch (Throwable ex) {
			ex.printStackTrace();
			Dialog.show("Exception", ex.getMessage(), "OK", null);
		}

	}

	/**
	 * Performs an action assigned to the Mobile Device - MIDlet Started point.
	 */
	public void startMIDlet() {
		Form mainMenu = getMainMenuForm();
		mainMenu.show();
	}

	private Form getMainMenuForm() {
		if (mainMenu == null) {
			mainMenu = new Form("RunGPS2");
			mainMenu.setTransitionOutAnimator(CommonTransitions.createSlide(
					CommonTransitions.SLIDE_HORIZONTAL, true, 100));

			mainMenu
					.setTransitionOutAnimator(CommonTransitions.createFade(400));

			ListItem[] li = new ListItem[3];
			li[0] = new ListItem("New Workout", "start a new workout", resource
					.getImage("NewWorkout"));
			li[1] = new ListItem("Training Diary", "previous workouts",
					resource.getImage("TrainingDiary"));
			li[2] = new ListItem("Settings", "configure application", resource
					.getImage("Settings"));
			List list = new List(li);
			list.setFixedSelection(List.FIXED_NONE_CYCLIC);
			list.setSmoothScrolling(true);
			list.getStyle().setBgTransparency(0);
			FishEyeRenderer renderer = new FishEyeRenderer();
			list.setListCellRenderer(renderer);
			list.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					int newSelected = ((List) evt.getSource())
							.getSelectedIndex();
					switch (newSelected) {
					case 0:
						MapViewer mv = new MapViewer(myself, myself);
						break;
					default:
						break;
					}
				};
			});
			mainMenu.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
			mainMenu.addComponent(list);

			mainMenu.addCommand(exitCommand);
			mainMenu.addCommand(aboutCommand);
			mainMenu.addCommand(runCommand);

			mainMenu.setCommandListener(this);
		}
		return mainMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.schreibubi.mapviewer.PreviousMenuInterface#switchToPreviousMenu()
	 */
	public void switchToPreviousMenu() {
		mainMenu.show();
	}

	/**
	 * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
	 */
	public void resumeMIDlet() {
	}

	/**
	 * Exits MIDlet.
	 */
	public void exitMIDlet() {
		destroyApp(true);
		notifyDestroyed();
	}

	/**
	 * Called when MIDlet is started. Checks whether the MIDlet have been
	 * already started and initialize/starts or resumes the MIDlet.
	 */
	public void startApp() {
		if (midletPaused) {
			resumeMIDlet();
		} else {
			initialize();
			startMIDlet();
		}
		midletPaused = false;
	}

	/**
	 * Called when MIDlet is paused.
	 */
	public void pauseApp() {
		midletPaused = true;
	}

	/**
	 * Called to signal the MIDlet to terminate.
	 * 
	 * @param unconditional
	 *            if true, then the MIDlet has to be unconditionally terminated
	 *            and all resources has to be released.
	 */
	public void destroyApp(boolean unconditional) {
	}

	/**
	 * Initializes LocationProvider uses default criteria
	 */
	void createLocationProvider() {
		if (locationProvider == null) {
			Criteria criteria = new Criteria();

			try {
				locationProvider = LocationProvider.getInstance(criteria);
				locationProvider.setLocationListener(this, -1, -1, -1);
			} catch (LocationException le) {
				System.out
						.println("Cannot create LocationProvider for this criteria.");
				le.printStackTrace();
			}
		}
	}

	public void actionPerformed(ActionEvent evt) {
		Command cmd = evt.getCommand();
		switch (cmd.getId()) {
		case RUN_COMMAND:
			break;
		case EXIT_COMMAND:
			notifyDestroyed();
			break;
		case BACK_COMMAND:

			// for series 40 devices
			System.gc();
			System.gc();
			break;
		case ABOUT_COMMAND:
			Form aboutForm = new Form("About");
			aboutForm.setScrollable(false);
			aboutForm.setLayout(new BorderLayout());
			TextArea aboutText = new TextArea("Info", 5, 10);
			aboutText.setEditable(false);
			aboutForm.addComponent(BorderLayout.CENTER, aboutText);
			aboutForm.addCommand(new Command("Back") {
				public void actionPerformed(ActionEvent evt) {
					switchToPreviousMenu();
				}
			});
			aboutForm.show();
			break;
		}

	}

	private class FishEyeRenderer extends Label implements ListCellRenderer {

		private Label title;
		private Label description;

		private Container selected = new Container(new BoxLayout(
				BoxLayout.Y_AXIS));

		public FishEyeRenderer() {
			super("");

			title = new Label("");
			title.getStyle().setBgTransparency(0);
			title.setFocus(true);

			description = new Label("");
			description.getStyle().setBgTransparency(0);
			description.setFocus(true);

			selected.addComponent(title);
			selected.addComponent(description);

		}

		public Component getListCellRendererComponent(List list, Object value,
				int index, boolean isSelected) {
			if (value instanceof ListItem) {
				ListItem v = (ListItem) value;
				if (isSelected) {
					title.setText(v.getTitle());
					title.setIcon(v.getIcon());
					description.setText(v.getDescription());
					return selected;
				}
				setText(v.getTitle());
				setIcon(v.getIcon());
				setFocus(false);
				getStyle().setBgTransparency(0);
			}
			return this;
		}

		public Component getListFocusComponent(List list) {
			setText("");
			setIcon(null);
			setFocus(true);
			getStyle().setBgTransparency(100);
			return this;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.schreibubi.mapviewer.MyLocationProviderInterface#addLocationListener
	 * (javax.microedition.location.LocationListener)
	 */
	public void addLocationListener(LocationListener listener) {
		locationListenerList.addElement(listener);
	}

	public void removeLocationListener(LocationListener listener) {
		locationListenerList.removeElement(listener);
	}

	/**
	 * A method which is called by the location provider when the current
	 * location is changed.
	 */
	public synchronized void locationUpdated(LocationProvider provider,
			Location location) {
		for (int i = 0; i < locationListenerList.size(); i++) {
			((LocationListener) locationListenerList.elementAt(i))
					.locationUpdated(provider, location);
		}
	}

	/**
	 * A method which is called by the location provider when its state changes
	 * (for example, when its services are temporary unavailable).
	 */
	public synchronized void providerStateChanged(LocationProvider provider,
			int newState) {
		for (int i = 0; i < locationListenerList.size(); i++) {
			((LocationListener) locationListenerList.elementAt(i))
					.providerStateChanged(provider, newState);
		}
	}

}
