package de.rlill.modelmanager.struct;

import android.annotation.SuppressLint;
import de.rlill.modelmanager.Util;

@SuppressLint("DefaultLocale")
public class CarOffer {

	private int companyCarId;
	private int carTypeId;
	private int price;
	private String license;
	private String description;

	public CarOffer(String d, int cc, int ct, int p, String l) {
		description = d; companyCarId = cc; carTypeId = ct; price = p; license = l;
	}

	public String toString() {
		String result = description;
		if (price > 0)
			result += String.format(" (%s)", Util.amount(price));
		if (license != null)
			result += " [" + license + "]";
		return result;
	}

	public int getCompanyCarId() {
		return companyCarId;
	}
	public int getCarTypeId() {
		return carTypeId;
	}
	public int getPrice() {
		return price;
	}
}
