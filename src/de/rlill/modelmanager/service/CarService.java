package de.rlill.modelmanager.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.CarFile;
import de.rlill.modelmanager.model.CarType;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.persistance.CarFileDbAdapter;
import de.rlill.modelmanager.persistance.CompanyCarDbAdapter;
import de.rlill.modelmanager.setup.CarTypes;
import de.rlill.modelmanager.struct.CarAction;
import de.rlill.modelmanager.struct.CarOffer;
import de.rlill.modelmanager.struct.CarStatus;


public class CarService {

	private static final String LOG_TAG = "MM*" + CarService.class.getSimpleName();

	private static SparseArray<CompanyCar> companyCarsArray;
	private static SparseIntArray carDriver;

	public static CompanyCar getCompanyCar(int companyCarId) {
		if (companyCarsArray == null) companyCarsArray = CompanyCarDbAdapter.listAllCompanyCars();
		return companyCarsArray.get(companyCarId);
	}

	public static int getDriverId(int companyCarId) {
		if (companyCarsArray == null) companyCarsArray = CompanyCarDbAdapter.listAllCompanyCars();
		if (carDriver == null) {
			carDriver = new SparseIntArray();
			for (Model model : ModelService.getAllModels()) {
				if (model.getCarId() > 0) carDriver.put(model.getCarId(), model.getId());
			}
		}
//		cleanCarStatus();
		return carDriver.get(companyCarId, -1);
	}

	public static void cleanCarStatus() {
		Log.i(LOG_TAG, "cleaning car status");
		for (int i = 0; i < companyCarsArray.size(); i++) {
			CompanyCar cc = companyCarsArray.valueAt(i);
			int m = getDriverId(cc.getId());
			Log.i(LOG_TAG, "car #" + cc.getId() + ", driver #" + m);
			if (m > 0) {
				switch (cc.getStatus()) {
				case IN_USE:
				case DEFECT:
					Log.d(LOG_TAG, "Car #" + cc.getId() + " in use by model #" + m + " - " + cc.getStatus());
					break;
				default:
					Log.e(LOG_TAG, "Car #" + cc.getId() + " in use by model #" + m + " - " + cc.getStatus());
					cc.setStatus(CarStatus.IN_USE);
					CompanyCarDbAdapter.updateCompanyCar(cc);
				}
			}
			else {
				switch (cc.getStatus()) {
				case DEFECT:
				case NEW:
				case STOLEN:
				case USED_ALITTLE:
				case USED_ALOT:
				case WRECKED:
				case SOLD:
					Log.d(LOG_TAG, "Car #" + cc.getId() + " in use by no model - " + cc.getStatus());
					break;
				default:
					Log.e(LOG_TAG, "Car #" + cc.getId() + " in use by no model - " + cc.getStatus());
					int age = DiaryService.today() - cc.getBuyday();
					cc.setStatus((age > 84) ? CarStatus.USED_ALOT : CarStatus.USED_ALITTLE);
					CompanyCarDbAdapter.updateCompanyCar(cc);
				}
			}
		}
	}

	public static String getCarInfo(int companyCarId) {
		if (companyCarId == 0) return "-";
		CompanyCar cc = getCompanyCar(companyCarId);
		if (cc == null) return "-";
		return cc.getCarType().getDescription()
				+ " (" + cc.getCarType().getCclass().getName()
				+ ", " + cc.getStatus().getName() + ")";
	}

	public static int getCarValue(int companyCarId) {
		if (companyCarId == 0) return 0;
		CompanyCar cc = getCompanyCar(companyCarId);
		if (cc == null) return 0;
		if (cc.getStatus() == CarStatus.STOLEN || cc.getStatus() == CarStatus.WRECKED) return 0;
		int price = cc.getPrice();
		int age = DiaryService.today() - cc.getBuyday();
		if (age == 0) return price;

		int value = (int)Math.round(
			price - price * 0.7 * Math.log((double)(age + 101)/100));

		if (value < 0) value = 0;
		return value;
	}

	public static int getCarRepairCost(int companyCarId) {
		if (companyCarId == 0) return 0;
		CompanyCar cc = getCompanyCar(companyCarId);
		if (cc == null) return 0;
		if (cc.getStatus() != CarStatus.DEFECT) return 0;

		int price = cc.getPrice();
		int rnd = Util.atoi(cc.getLicensePlate().substring(5)) % 100;
		int cost = (int)(price * (rnd * 1.2 + 5) / 300);

		return cost;
	}

	public static int buyCar(int carTypeId) {
		CompanyCar cc = new CompanyCar();
		cc.setBuyday(DiaryService.today());
		cc.setCarTypeId(carTypeId);
		int companyCarId = 0;
		CarType ct = CarTypes.getCarType(carTypeId);
		if (ct != null) {
			cc.setPrice(ct.getPrice());
			cc.setStatus(CarStatus.NEW);
			cc.setLicensePlate(Util.randomLicense());
			companyCarId = CompanyCarDbAdapter.addCompanyCar(cc);
			cc = CompanyCarDbAdapter.getCompanyCar(companyCarId);
			companyCarsArray.put(companyCarId, cc);
			TransactionService.transfer(0, -1, ct.getPrice(), ct.getDescription());
			log(companyCarId, CarAction.BUY,
					MessageService.getMessage(R.string.logmessage_car_buy), ct.getPrice());
		}
		return companyCarId;
	}

	public static List<CompanyCar> getCompanyPoolCars() {
		if (companyCarsArray == null) companyCarsArray = CompanyCarDbAdapter.listAllCompanyCars();
		List<CompanyCar> result = new ArrayList<CompanyCar>();
		for (int i = 0; i < companyCarsArray.size(); i++) {
			CompanyCar cc = companyCarsArray.valueAt(i);
			if (cc.getStatus() == CarStatus.SOLD) continue;
			result.add(cc);
		}
		return result;
	}

	public static List<CompanyCar> getAvailableCompanyCars() {
		if (companyCarsArray == null) companyCarsArray = CompanyCarDbAdapter.listAllCompanyCars();
		List<CompanyCar> result = new ArrayList<CompanyCar>();
		for (int i = 0; i < companyCarsArray.size(); i++) {
			CompanyCar cc = companyCarsArray.valueAt(i);
			if (cc.getStatus() != CarStatus.NEW
					&& cc.getStatus() != CarStatus.USED_ALITTLE
					&& cc.getStatus() != CarStatus.USED_ALOT) continue;

			if (getDriverId(cc.getId()) > 0) continue;

			// car is available for assignment
			result.add(cc);
		}
		return result;
	}

	/**
	 * Don't call this directly, call ModelSerice.setCar(). ModelService then calls assignCar().
	 */
	public static void assignCar(int companyCarId, int modelId) {
		if (companyCarId == 0) return;
		CompanyCar cc = getCompanyCar(companyCarId);
		if (modelId > 0) {
			// assigned to new driver
			Log.d(LOG_TAG, "Car #" + companyCarId + " status change from " + cc.getStatus() + " to IN_USE");
			cc.setStatus(CarStatus.IN_USE);
			CompanyCarDbAdapter.updateCompanyCar(cc);
		}
		else if (cc.getStatus() == CarStatus.IN_USE) {
			// de-assigned, back to car pool
			int age = DiaryService.today() - cc.getBuyday();
			Log.d(LOG_TAG, "Car #" + companyCarId + " status change from " + cc.getStatus() + " to " + ((age > 84) ? CarStatus.USED_ALOT : CarStatus.USED_ALITTLE));
			cc.setStatus((age > 84) ? CarStatus.USED_ALOT : CarStatus.USED_ALITTLE);
			CompanyCarDbAdapter.updateCompanyCar(cc);
		}
		else {
			Log.w(LOG_TAG, "keeping car #" + companyCarId + " status " + cc.getStatus());
		}
		Log.d(LOG_TAG, "remember driver #" + modelId + " for car #" + companyCarId);
		getDriverId(0); // initialize carDriver map
		carDriver.put(companyCarId, modelId);
	}

	public static void setCarStatus(int companyCarId, CarStatus status) {
		CompanyCar cc = getCompanyCar(companyCarId);
		cc.setStatus(status);
		CompanyCarDbAdapter.updateCompanyCar(cc);
	}

	public static void repairCar(int companyCarId) {
		int cost = CarService.getCarRepairCost(companyCarId);
		String description = MessageService.getMessage(R.string.logmessage_car_repair2, getCompanyCar(companyCarId));
		TransactionService.transfer(0, -1, cost, description);
		CarService.setCarStatus(companyCarId, CarStatus.IN_USE);
		log(companyCarId, CarAction.REPAIR, MessageService.getMessage(R.string.logmessage_car_repair3), cost);
	}

	public static void log(int companyCarId, CarAction action, String description, int price) {
		CarFile file = new CarFile();
		file.setCarId(companyCarId);
		file.setDay(DiaryService.today());
		file.setAction(action);
		file.setDescription(description);
		file.setPrice(price);
		CarFileDbAdapter.addEntry(file);
	}

	public static void initSpinner(Context ctx, Spinner sp, int currentCarId) {

		// list unassigned company cars + NEW car types for purchase
		List<CarOffer> carList = new ArrayList<CarOffer>();

		CarOffer co = new CarOffer("-", 0, 0, 0, null);
		carList.add(co);
		int position = 0;

		if (currentCarId != 0) {
			CompanyCar cc = getCompanyCar(currentCarId);
			co = new CarOffer(
					cc.getCarType().getDescription() + " (" + cc.getStatus().getName() + ")",
					cc.getId(),
					cc.getCarTypeId(),
					cc.getPrice(),
					cc.getLicensePlate()
				);
			carList.add(co);
			position = 1;
		}

		String ava = ctx.getResources().getString(R.string.display_state_available);
		for (CompanyCar cc : CarService.getAvailableCompanyCars()) {
			co = new CarOffer(
					"(" + ava + ") " + cc.getCarType().getDescription() + " (" + cc.getStatus().getName() + ")",
					cc.getId(),
					cc.getCarTypeId(),
					cc.getPrice(),
					cc.getLicensePlate()
				);
			carList.add(co);
		}

		for (CarType ct : CarTypes.getCarTypes()) {
			co = new CarOffer(
					"(" + CarStatus.NEW.getName() + ") " + ct.getDescription(),
					0,
					ct.getId(),
					ct.getPrice(),
					null
				);
			carList.add(co);
		}


		ArrayAdapter<CarOffer> carOfferAdapter = new ArrayAdapter<CarOffer>(ctx,
				android.R.layout.simple_spinner_item, carList);
		carOfferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp.setAdapter(carOfferAdapter);

		sp.setSelection(position);

	}

	public static void initSpinner(Context ctx, Spinner sp, int selectedCarId, int selectedCarTypeId) {

		// list unassigned company cars + NEW car types for purchase
		List<CarOffer> carList = new ArrayList<CarOffer>();

		CarOffer co = new CarOffer("-", 0, 0, 0, null);
		carList.add(co);
		int position = 0;

		String ava = ctx.getResources().getString(R.string.display_state_available);
		for (CompanyCar cc : CarService.getAvailableCompanyCars()) {
			co = new CarOffer(
					"(" + ava + ") " + cc.getCarType().getDescription() + " (" + cc.getStatus().getName() + ")",
					cc.getId(),
					cc.getCarTypeId(),
					cc.getPrice(),
					cc.getLicensePlate()
					);
			carList.add(co);
			if (selectedCarId == cc.getId()) position = carList.indexOf(co);
		}

		for (CarType ct : CarTypes.getCarTypes()) {
			co = new CarOffer(
					"(" + CarStatus.NEW.getName() + ") " + ct.getDescription(),
					0,
					ct.getId(),
					ct.getPrice(),
					null
					);
			carList.add(co);
			if (position == 0 && selectedCarTypeId == ct.getId()) position = carList.indexOf(co);
		}


		ArrayAdapter<CarOffer> carOfferAdapter = new ArrayAdapter<CarOffer>(ctx,
				android.R.layout.simple_spinner_item, carList);
		carOfferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp.setAdapter(carOfferAdapter);

		sp.setSelection(position);

	}
}
