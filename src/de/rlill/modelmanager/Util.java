package de.rlill.modelmanager;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.Weekday;

public class Util {

	public static int atoi(String s) {
		int result = 0;
		try {
			result = Integer.parseInt(s);
		}
		catch (NumberFormatException e) { }
		return result;
	}

	public static double atof(String s) {
		if (s == null) return 0;
		double result = 0;
		try {
			result = Double.parseDouble(s);
		}
		catch (NumberFormatException e) { }
		return result;
	}

	public static int niceRandom(int min, int max) {
		if (min < 0 || max < min) return -1;
		if (min == max) return min;
		return niceRound((int)(min + Math.random() * (max - min + 1)));
	}

	public static int interpolation(int min, int max) {
		if (min < 0 || max <= min) return min;
		int digitsMax = Integer.toString(max).length();
		int digitsZero = (digitsMax > 3) ? digitsMax - 3 : 0;
		int resultInt = 0;
		if (digitsZero > 0) {
			int factor = (int)Math.pow(10, digitsZero);
			resultInt = min + factor * (int)Math.floor(((max - min) / factor / 2));
		}
		else {
			resultInt = min + (int)Math.floor((max - min) / 2);
		}

		return resultInt;
	}

	public static int niceRound(int nr) {
		if (nr < 0) return -1;
		int digitsMax = Integer.toString(nr).length();
		int digitsZero = (digitsMax > 2) ? digitsMax - 2 : 0;
		if (digitsZero == 0) return nr;
		int factor = (int)Math.pow(10, digitsZero);
		return factor * (int)((nr + 0.5 * factor) / factor);
	}

	public static int rnd(int v) {
		return (int)Math.floor(Math.random() * v);
	}

	public static int[] randomArray(int size) {
		if (size < 1) return null;
		int[] result = new int[size];
		int[] source = new int[size];
		for (int i = 0; i < size; i++) source[i] = i;
		for (int i = 0; i < size; i++) {
			int r = rnd(size);
			while (source[r] == -1) r = ((r+1) % size);
			result[i] = r;
			source[r] = -1;
		}
		return result;
	}

	public static String amount(int v) {
		return amount((long)v);
	}

	public static String amount(long v) {
		String val = Long.toString(v);
		String res = null;
		while (val.length() > 3) {
			res = val.substring(val.length() - 3) + ((res == null) ? ".-" : "\u00A0" + res);
			val = val.substring(0, val.length() - 3);
		}
		res = "\u2730\u00A0" + val + ((res == null) ? ".-" : "\u00A0" + res);
		return res;
	}

	public static String duration(Context ctx, int days) {
		StringBuilder result = new StringBuilder();
		if (days > 360) {
			int years = days / 360;
			String year_si = ctx.getResources().getString(R.string.display_year_si);
			String year_pl = ctx.getResources().getString(R.string.display_year_pl);
			result.append(years).append("\u00A0").append((years > 1) ? year_pl : year_si);
			days -= 360 * years;
		}
		if (days > 30) {
			int months = days / 30;
			String month_si = ctx.getResources().getString(R.string.display_month_si);
			String month_pl = ctx.getResources().getString(R.string.display_month_pl);
			if (result.length() > 0) result.append(", ");
			result.append(months).append("\u00A0").append((months > 1) ? month_pl : month_si);
			days -= 30 * months;
		}
		if (result.length() == 0 && days > 7) {
			int weeks = days / 7;
			String weeks_si = ctx.getResources().getString(R.string.display_week_si);
			String weeks_pl = ctx.getResources().getString(R.string.display_week_pl);
			if (result.length() > 0) result.append(", ");
			result.append(weeks).append("\u00A0").append((weeks > 1) ? weeks_pl : weeks_si);
			days -= 7 * weeks;
		}
		if (days > 0) {
			String day_si = ctx.getResources().getString(R.string.display_day_si);
			String day_pl = ctx.getResources().getString(R.string.display_day_pl);
			if (result.length() > 0) result.append(", ");
			result.append(days).append("\u00A0").append((days > 1) ? day_pl : day_si);
		}
		if (result.length() == 0) {
			String day_pl = ctx.getResources().getString(R.string.display_day_pl);
			result.append("0\u00A0").append(day_pl);
		}

		return result.toString();
	}

	public static Weekday weekday(int d) {
		return Weekday.getInstanceByIndex(d % 7);
	}

	private static final String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String number = "0123456789";
	public static String randomLicense() {
		char [] plate = new char[9];
		for (int i = 0; i < 4; i++) {
			plate[i] = alpha.charAt(rnd(alpha.length()));
		}
		plate[4] = '-';
		for (int i = 5; i < 9; i++) {
			plate[i] = number.charAt(rnd(number.length()));
		}
		return new String(plate);
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device density.
	 *
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static int convertDpToPixel(int dp, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = (int)(dp * (metrics.densityDpi / 160f));
		return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 *
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	public static EventFlag randomBooking(int day) {
		int week = day % 7;
		int r = rnd((week % 7 == 0) ? 30 : 6);
		switch (r) {
			case 0:
			case 1:
				return EventFlag.PHOTO;
			case 2:
				return EventFlag.MOVIE;
		}
		return null;
	}
}
