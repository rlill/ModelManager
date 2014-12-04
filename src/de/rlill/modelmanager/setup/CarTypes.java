package de.rlill.modelmanager.setup;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.model.CarType;
import de.rlill.modelmanager.struct.CarClass;

public class CarTypes {

	private static List<CarType> carTypeList = null;
	private static SparseArray<CarType> carTypeMap = null;

	public static List<CarType> getCarTypes() {
		if (carTypeList == null) init();
		return carTypeList;
	}

	public static CarType getCarType(int id) {
		if (carTypeMap == null) init();
		return carTypeMap.get(id, null);
	}

	private static void init() {
		carTypeList = new ArrayList<CarType>();
		carTypeMap= new SparseArray<CarType>();

		CarType carType = new CarType(1, CarClass.SMALL, 10000, R.drawable.car_small1, "Swingo compact");
		carType.setEngine(4, 1200, 75, 160);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(7, CarClass.SMALL, 12000, R.drawable.car_small2, "Maestro shopper");
		carType.setEngine(4, 1250, 80, 170);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(2, CarClass.MEDIUM, 40000, R.drawable.car_medium1, "Forum F25");
		carType.setEngine(4, 1800, 120, 210);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(8, CarClass.MEDIUM, 44000, R.drawable.car_medium2, "Sumarum DD");
		carType.setEngine(6, 2200, 180, 240);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(3, CarClass.LARGE, 55000, R.drawable.car_large2, "WMB 8.50i");
		carType.setEngine(6, 2800, 240, 250);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(4, CarClass.LARGE, 60000, R.drawable.car_large3, "Merciless 500");
		carType.setEngine(8, 3000, 250, 250);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(9, CarClass.LARGE, 58000, R.drawable.car_large1, "Pexus 3");
		carType.setEngine(8, 3300, 280, 250);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(5, CarClass.SPORTS, 200000, R.drawable.car_sports2, "Farorry Lightening");
		carType.setEngine(12, 5600, 400, 300);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(10, CarClass.SPORTS, 190000, R.drawable.car_sports1, "Forum Z2");
		carType.setEngine(12, 4800, 440, 310);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(6, CarClass.LUXURY, 200000, R.drawable.car_limo2, "Ladylac Coupe Deville");
		carType.setEngine(12, 4000, 360, 250);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);

		carType = new CarType(11, CarClass.LUXURY, 190000, R.drawable.car_limo1, "Pexus 07");
		carType.setEngine(12, 4400, 380, 250);
		carTypeList.add(carType);
		carTypeMap.put(carType.getId(), carType);
	}
}
