package de.rlill.modelmanager.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.CarFile;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.persistance.CarFileDbAdapter;
import de.rlill.modelmanager.service.CarService;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.TodayService;
import de.rlill.modelmanager.service.TransactionService;
import de.rlill.modelmanager.struct.CarAction;
import de.rlill.modelmanager.struct.CarStatus;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;

public class CarMaintenanceDialog extends Activity implements OnClickListener {

	private static final String LOG_TAG = CarMaintenanceDialog.class.getSimpleName();

	private int companyCarId;
	private CompanyCar companyCar;
	public final static String EXTRA_COMPANYCAR_ID = "car.detail.dialog.companycar.id";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_car_maintenance);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

	    // Get the message from the intent
	    Intent intent = getIntent();
	    companyCarId = intent.getIntExtra(EXTRA_COMPANYCAR_ID, -1);

	    if (companyCarId > 0) {

	    	companyCar = CarService.getCompanyCar(companyCarId);

	    	TableLayout tl = (TableLayout)findViewById(R.id.companycar_details);

	    	String label = getResources().getString(R.string.labelCarModel);
	    	tl.addView(mkrow(this, label, companyCar.getCarType().getDescription()));

	    	label = getResources().getString(R.string.labelClass);
	    	tl.addView(mkrow(this, label, companyCar.getCarType().getCclass().getName()));

	    	label = getResources().getString(R.string.labelLicensePlate);
	    	tl.addView(mkrow(this, label, companyCar.getLicensePlate()));

	    	label = getResources().getString(R.string.labelStatus);
	    	tl.addView(mkrow(this, label, companyCar.getStatus().getName()));

	    	if (companyCar.getStatus() == CarStatus.DEFECT) {
	    		label = getResources().getString(R.string.labelRepairCost);
	    		tl.addView(mkrow(this, label, Util.amount(CarService.getCarRepairCost(companyCarId))));

	    		label = getResources().getString(R.string.labelRepair);
	    		tl.addView(mkButtonRow(this, label, CarTreatment.REPAIR, R.drawable.button_green));

	    		label = getResources().getString(R.string.labelSell);
	    		tl.addView(mkButtonRow(this, label, CarTreatment.SELL, R.drawable.button_blue));

	    		label = getResources().getString(R.string.labelTrash);
	    		tl.addView(mkButtonRow(this, label, CarTreatment.TRASH, R.drawable.button_red));
	    	}
	    	else if (companyCar.getStatus() == CarStatus.WRECKED) {
	    		label = getResources().getString(R.string.labelTrash);
	    		tl.addView(mkButtonRow(this, label, CarTreatment.TRASH, R.drawable.button_red));
	    	}
	    	else if (companyCar.getStatus() == CarStatus.STOLEN) {
	    		label = getResources().getString(R.string.labelDeassign);
	    		tl.addView(mkButtonRow(this, label, CarTreatment.DEASSIGN, R.drawable.button_green));
	    	}
	    	else if (companyCar.getStatus() == CarStatus.NEW
	    			|| companyCar.getStatus() == CarStatus.USED_ALITTLE
	    			|| companyCar.getStatus() == CarStatus.USED_ALOT) {
	    		label = getResources().getString(R.string.labelSell);
	    		tl.addView(mkButtonRow(this, label, CarTreatment.SELL, R.drawable.button_blue));
	    	}

	    	label = getResources().getString(R.string.labelDriver);
	    	int driver = CarService.getDriverId(companyCarId);
	    	Model m = ModelService.getModelById(driver);
	    	tl.addView(mkrow(this, label, (driver > 0 && m != null) ? m.getFullname() : ""));

	    	if (driver > 0) {
	    		label = getResources().getString(R.string.labelTakeaway);
	    		tl.addView(mkButtonRow(this, label, CarTreatment.TAKEAWAY, R.drawable.button_red));

	    		label = getResources().getString(R.string.labelSell);
	    		tl.addView(mkButtonRow(this, label, CarTreatment.SELL, R.drawable.button_blue));
	    	}

	    	label = getResources().getString(R.string.labelAge);
	    	tl.addView(mkrow(this, label, Util.duration(this, DiaryService.today() - companyCar.getBuyday())));

	    	label = getResources().getString(R.string.labelPrice);
	    	tl.addView(mkrow(this, label, Util.amount(companyCar.getPrice())));

	    	label = getResources().getString(R.string.labelValue);
	    	tl.addView(mkrow(this, label, Util.amount(CarService.getCarValue(companyCarId))));

	    	StringBuilder carFiles = new StringBuilder();
	    	for (CarFile cf : CarFileDbAdapter.listEntries(companyCarId)) {
	    		carFiles.append(String.format("#%d %s (%s)\n", cf.getDay(), cf.getDescription(), Util.amount(cf.getPrice())));
	    	}
	    	label = getResources().getString(R.string.labelHistory);
	    	tl.addView(mkrow(this, label, carFiles.toString()));

			ImageView iv = (ImageView)findViewById(R.id.imageView1);
			iv.setImageResource(companyCar.getCarType().getImageId());

	    }
	}

	private TableRow mkrow(Context context, String key, String value) {
		TableRow tr = new TableRow(context);

		TextView tv = new TextView(context);
		tv.setText(key);
		tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
		tv.setGravity(Gravity.RIGHT);
		tv.setPadding(40, 4, 5, 4);
		tr.addView(tv);

		tv = new TextView(context);
		tv.setText(value);
		tv.setPadding(5, 4, 5, 4);
		tr.addView(tv);

		return tr;
	}

	private TableRow mkButtonRow(Context context, String value, CarTreatment action, int backgroundDrawableId) {
		TableRow tr = new TableRow(context);

		TextView tv = new TextView(context);
		tv.setText("");
		tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
		tv.setGravity(Gravity.RIGHT);
		tv.setPadding(40, 4, 5, 4);
		tr.addView(tv);

		Button b = new Button(context);
		b.setText(value);
		b.setPadding(5, 4, 5, 4);
		b.setWidth(200);
		b.setTag(action);
		b.setBackgroundResource(backgroundDrawableId);
		b.setOnClickListener(this);
		tr.addView(b);

		return tr;
	}

	@Override
	public void onClick(View v) {
		CarTreatment ca = (CarTreatment)v.getTag();
		int driver = CarService.getDriverId(companyCarId);
		int cost = CarService.getCarRepairCost(companyCarId);
		int worth = CarService.getCarValue(companyCarId);
		String description;
		switch(ca) {
		case REPAIR:
			description = getResources().getString(R.string.logmessage_car_repair, companyCar.getLicensePlate());
			TransactionService.transfer(0, -1, cost, description);
			CarService.setCarStatus(companyCarId, (driver > 0) ? CarStatus.IN_USE : CarStatus.USED_ALOT);
			CarService.log(companyCarId, CarAction.REPAIR, getResources().getString(R.string.logmessage_car_repair3), cost);
			if (driver > 0) {
				TodayService.dropEvents(driver, EventClass.NOTIFICATION, EventFlag.CAR_BROKEN, EventFlag.TRAINING);
				TodayService.dropEvents(driver, EventClass.REQUEST, EventFlag.CAR_UPDATE, EventFlag.TRAINING);
			}
			break;
		case SELL:
			description = getResources().getString(R.string.logmessage_car_sale, companyCar.getLicensePlate());
			int result = worth - cost;
			if (result > 0)
				TransactionService.transfer(-1, 0, result, description);
			CarService.setCarStatus(companyCarId, CarStatus.SOLD);
			CarService.log(companyCarId, CarAction.SELL, getResources().getString(R.string.logmessage_car_sale2), result);
			if (driver > 0) ModelService.setCar(driver, 0);
			break;
		case TRASH:
			CarService.setCarStatus(companyCarId, CarStatus.SOLD);
			CarService.log(companyCarId, CarAction.TRASH, getResources().getString(R.string.logmessage_car_trash), 0);
			if (driver > 0) ModelService.setCar(driver, 0);
			break;
		case TAKEAWAY:
			if (driver > 0) ModelService.setCar(driver, 0);
			CarService.log(companyCarId, CarAction.TAKEAWAY, getResources().getString(R.string.logmessage_car_takeaway), CarService.getCarValue(companyCarId));
			break;
		case DEASSIGN:
			if (driver > 0) ModelService.setCar(driver, 0);
			CarService.setCarStatus(companyCarId, CarStatus.SOLD);
			CarService.log(companyCarId, CarAction.DEASSIGN, getResources().getString(R.string.logmessage_car_deassign), CarService.getCarValue(companyCarId));
			break;
		}

		// update booking requests since ability to attend may have changed:
		if (driver > 0) TodayService.updateBookingRequestAcceptance(driver);

		finish();
	}

	public enum CarTreatment {
		REPAIR, SELL, TRASH, TAKEAWAY, DEASSIGN
	}

}
