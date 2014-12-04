package de.rlill.modelmanager.dialog;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.CarTypeListAdapter;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.CarType;
import de.rlill.modelmanager.service.CarService;
import de.rlill.modelmanager.setup.CarTypes;
import de.rlill.modelmanager.struct.ViewElements;

public class CarShoppingDialog extends Activity implements OnItemClickListener, View.OnClickListener {

	private static final String LOG_TAG = "MM*" + CarShoppingDialog.class.getSimpleName();

    private List<CarType> carTypeListItems;
    private ArrayAdapter<CarType> carTypeAdapter;
    private int selectedCarType = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_car_shopping);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

        ListView carTypeList = (ListView)findViewById(R.id.listCarTypes);

        carTypeListItems = CarTypes.getCarTypes();
        carTypeAdapter = new CarTypeListAdapter(
        		this,
        		android.R.layout.simple_list_item_1,
        		R.id.listCompanyCars,
        		carTypeListItems,
        		getLayoutInflater(),
        		null);

        carTypeList.setAdapter(carTypeAdapter);
        carTypeList.setTextFilterEnabled(true);
        carTypeList.setOnItemClickListener(this);

        Button b = (Button)findViewById(R.id.button1);
        b.setOnClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ViewElements ve = (ViewElements)v.getTag();
		if (ve != null) {
			int carTypeId = ve.getContextInt();
			selectedCarType = carTypeId;
			CarType ct = CarTypes.getCarType(carTypeId);

			TextView tv = (TextView)findViewById(R.id.textViewName);
			tv.setText(ct.getDescription());

			tv = (TextView)findViewById(R.id.textViewClass);
			tv.setText(ct.getCclass().getName());

			tv = (TextView)findViewById(R.id.textViewTaxInsurance);
			tv.setText(Util.amount(ct.getPrice() / 1000));

			tv = (TextView)findViewById(R.id.textViewPrice);
			tv.setText(Util.amount(ct.getPrice()));

			tv = (TextView)findViewById(R.id.textViewMaxSpeed);
			tv.setText(Integer.toString(ct.getMaxspeedKmh()) + " km/h");

			tv = (TextView)findViewById(R.id.textViewPower);
			tv.setText(Integer.toString(ct.getpowerPs()) + " PS");

			tv = (TextView)findViewById(R.id.textViewCylinders);
			tv.setText(Integer.toString(ct.getCylinders()));

			tv = (TextView)findViewById(R.id.textViewCapacity);
			tv.setText(Integer.toString(ct.getCapacity()) + " ccm");

			ImageView iv = (ImageView)findViewById(R.id.imageView1);
			iv.setImageResource(ct.getImageId());
		}
	}

	@Override
	public void onClick(View v) {
		if (selectedCarType > 0) {
			CarService.buyCar(selectedCarType);
			finish();
		}
	}

}
