package de.rlill.modelmanager.service;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Property;
import de.rlill.modelmanager.persistance.PropertyDbAdapter;


public class PropertiesService {

	private static final String LOG_TAG = "MM*" + PropertiesService.class.getSimpleName();
	private static Map<String, String> propertyMap;

	private final static String PROPERTY_WORTHINCREASE = "worthincrease";

	public static void setProperty(String key, String value) {
		if (propertyMap == null) {
			propertyMap = new HashMap<String, String>();
			for (Property p : PropertyDbAdapter.getAllProperties()) {
				propertyMap.put(p.getKey(), p.getValue());
			}
		}
		propertyMap.put(key, value);
		Property p = new Property();
		p.setKey(key);
		p.setValue(value);
		// setProperty() tries update and fallbacks to insert
		PropertyDbAdapter.setProperty(p);
	}

	public static String getProperty(String key) {
		if (propertyMap == null) {
			propertyMap = new HashMap<String, String>();
			for (Property p : PropertyDbAdapter.getAllProperties()) {
				propertyMap.put(p.getKey(), p.getValue());
			}
		}
		return propertyMap.get(key);
	}

	public static double getWorthincrease() {
		double wi = Util.atof(getProperty(PROPERTY_WORTHINCREASE));
		if (wi < 1) wi = 1;
		return wi;
	}
	public static boolean setWorthincrease(double inf) {
		double wi = getWorthincrease();
		if (inf > wi * 1.2) {
			Log.e(LOG_TAG, "A worthincrease from " + wi + " to " + inf + " is not realistic");
			return false;
		}
		setProperty(PROPERTY_WORTHINCREASE, Double.toString(inf));
		return true;
	}
}
