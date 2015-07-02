package de.rlill.modelmanager.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.OpChartGridAdapter;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.model.MovieModel;
import de.rlill.modelmanager.model.Movieproduction;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;
import de.rlill.modelmanager.service.CarService;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.MovieService;
import de.rlill.modelmanager.service.TodayService;
import de.rlill.modelmanager.service.TrainingService;
import de.rlill.modelmanager.service.TransactionService;
import de.rlill.modelmanager.struct.CarAction;
import de.rlill.modelmanager.struct.CarOffer;
import de.rlill.modelmanager.struct.Characteristic;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.ListReverser;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.MovieStatus;
import de.rlill.modelmanager.struct.OpChartElement;
import de.rlill.modelmanager.struct.Operation;
import de.rlill.modelmanager.struct.TaskListRefresher;
import de.rlill.modelmanager.struct.TeamOption;

public class ModelNegotiationDialog extends Activity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, TaskListRefresher {

	private static final String LOG_TAG = "MM*" + ModelNegotiationDialog.class.getSimpleName();

	private int modelId;
	private int carId;
	public final static String EXTRA_MODEL_ID = "model.negotiation.dialog.model.id";
	private int compensation;

	private List<OpChartElement> operationChartListItems;
	private ArrayAdapter<OpChartElement> operationChartAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_model_negotiation_dialog);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

	    // Get the message from the intent
	    Intent intent = getIntent();
	    modelId = intent.getIntExtra(EXTRA_MODEL_ID, 0);

	    if (modelId == 0) return;

	    displayModelData();
	}

	private void displayModelData() {

		Model model = ModelService.getModelById(modelId);
		carId = model.getCarId();

		// status correction
		Movieproduction mpr = null;
		if (model.getStatus() == ModelStatus.MOVIEPROD) {
			MovieModel mm = MovieService.getCurrentMovieForModel(modelId);
			if (mm == null) {
				Log.e(LOG_TAG, "Resetting " + model + "'s status from MOVIEPROD to HIRED");
				ModelService.setModelStatus(modelId, ModelStatus.HIRED);
			}
			else {
				mpr = MovieService.getMovie(mm.getMovieId());
			}
		}

		int ir = getResources().getIdentifier(model.getImage(), "drawable", getPackageName());
		ImageView iv = (ImageView)findViewById(R.id.imageView1);
		iv.setImageResource(ir);

		iv = (ImageView)findViewById(R.id.imageCar);
		iv.setOnClickListener(this);

		TextView tv = (TextView)findViewById(R.id.textview_name);
		tv.setText(model.getFullname());

		EditText et = (EditText)findViewById(R.id.editTextSalary);
		int salary = model.getSalary();
		if (salary == 0) salary = Util.niceRound(ModelService.getAverageSalary(modelId));
		et.setText(Integer.toString(salary));

		et = (EditText)findViewById(R.id.editTextVacation4week);
		int vacation = model.getVacation();
		if (vacation == 0) vacation = 4; // TODO: take from settings
		et.setText(Integer.toString(vacation));

		et = (EditText)findViewById(R.id.editTextVacationRest);
		vacation = model.getVacrest();
		if (vacation == 0) vacation = 4; // TODO: take from settings
		et.setText(Integer.toString(vacation));

		et = (EditText)findViewById(R.id.editTextBonus);
		et.setText("0");


		// Company car
		Spinner sp = (Spinner)findViewById(R.id.selectCar);
		CarService.initSpinner(this, sp, model.getCarId());

		iv = (ImageView)findViewById(R.id.imageCar);
		if (model.getCarId() > 0) {
			CompanyCar cc = CarService.getCompanyCar(model.getCarId());
			iv.setImageResource(cc.getCarType().getImageId());
			iv.setVisibility(View.VISIBLE);
		}
		else {
			iv.setVisibility(View.INVISIBLE);
		}


		boolean hired = ModelService.isModelHired(model);

		// list Trainings
		if (hired) {
			sp = (Spinner)findViewById(R.id.selectTraining);
			ModelTraining pmt = ModelService.plannedTrainingForModel(modelId);
			if (model.getStatus() == ModelStatus.TRAINING) {
				// in progress
				List<String> trainingList = new ArrayList<String>();
				ModelTraining mt = TrainingService.getCurrentTraining(modelId);
				trainingList.add(mt.getTraining().getDescription());
				ArrayAdapter<String> trainingAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, trainingList);
				trainingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp.setAdapter(trainingAdapter);
				sp.setActivated(false);
			}
			else if (pmt != null) {
				// disable
				List<String> trainingList = new ArrayList<String>();
				trainingList.add(getResources().getString(R.string.labelAlreadyPlanned) + ": " + pmt.getTraining().getDescription());
				ArrayAdapter<String> trainingAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, trainingList);
				trainingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp.setAdapter(trainingAdapter);
				sp.setActivated(false);
			}
			else {
				TrainingService.initSpinner(this, sp);
			}
		}


		// list Teams
		sp = (Spinner)findViewById(R.id.selectTeam);
		ModelService.initTeamSpinner(this, sp, model.getTeamId());

		// "save" Button
		Button b = (Button)findViewById(R.id.buttonSave);
		b.setVisibility(hired ? LinearLayout.VISIBLE : LinearLayout.GONE);
		b.setOnClickListener(this);

		// "hire" Button
		b = (Button)findViewById(R.id.buttonHire);
		b.setVisibility((model.getStatus() == ModelStatus.FREE) ? LinearLayout.VISIBLE : LinearLayout.GONE);
		b.setOnClickListener(this);

		// "fire" Button
		b = (Button)findViewById(R.id.buttonFire);
		b.setVisibility(hired ? LinearLayout.VISIBLE : LinearLayout.GONE);
		b.setOnClickListener(this);

		// "account" Button
		b = (Button)findViewById(R.id.buttonAccount);
		b.setVisibility(hired ? LinearLayout.VISIBLE : LinearLayout.GONE);
		b.setOnClickListener(this);


		// ** from here on add additional table rows **

		TableLayout tl = (TableLayout)findViewById(R.id.modelDetailTable);
		tl.removeAllViews();

		// Status
		String label = getResources().getString(R.string.labelStatus);
		String value = model.getStatus().getName();
		if (mpr != null) value = value + " \"" + mpr.getName() + "\"";
		tl.addView(mkTextRow(label, value));

		if (hired) {
			// hired since
			label = getResources().getString(R.string.labelOnBoard);
			String day = getResources().getString(R.string.labelDay);
			tl.addView(mkTextRow(label, day + " " + Integer.toString(model.getHireday())
					+ " (" + Util.duration(this, DiaryService.today() - model.getHireday()) + ")"));

			// Account balance
			label = getResources().getString(R.string.labelBalance);
			tl.addView(mkTextRow(label, Util.amount(TransactionService.getBalance(modelId))));
		}

		// Trainings
		String day_s = getResources().getString(R.string.display_day_si);
		StringBuilder sb = new StringBuilder();
		for (ModelTraining mt : ModelTrainingDbAdapter.getTrainingsForModel(modelId)) {
			if (mt.getTraining() == null) continue;
			if (sb.length() > 0) sb.append('\n');
			sb.append('(').append(mt.getTrainingStatus().getName()).append(") ");
			sb.append(day_s).append(' ').append(mt.getStartDay())
				.append('-').append(mt.getEndDay()).append(": ");
			sb.append(mt.getTraining().getDescription());
			sb.append(' ').append(Util.amount(mt.getPrice()));
		}

		label = getResources().getString(R.string.labelTraining);
		if (sb.length() > 0) tl.addView(mkTextRow(label, sb.toString()));

		// planned for / participating in movie
		sb = new StringBuilder();
		for (Movieproduction mp2 : MovieService.getMovieproductionsForModel(modelId)) {
			if (sb.length() > 0) sb.append('\n');
			sb.append('(').append(mp2.getStatus().getName()).append(") ").append(mp2.getName())
					.append(", ").append(mp2.getType().getName());
		}
		label = getResources().getString(R.string.labelMovieproduction);
		if (sb.length() > 0) tl.addView(mkTextRow(label, sb.toString()));

		if (hired) {
			// Events today
			sb = new StringBuilder();
			for (String t : TodayService.getAllEventsForModelToday(modelId)) {
				sb.append(t).append("\n");
			}
	    	label = getResources().getString(R.string.labelTodayEvents);
	    	tl.addView(mkTextRow(label, sb.toString()));
		}

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean geek = sharedPref.getBoolean("geekMode", false);

			// rating bars
    	label = getResources().getString(R.string.labelQualityPhoto);
    	tl.addView(mkRatingRow(label, model.getQuality_photo(), Characteristic.QUALITY_PHOTO, geek));

    	label = getResources().getString(R.string.labelQualityMovie);
    	tl.addView(mkRatingRow(label, model.getQuality_movie(), Characteristic.QUALITY_MOVIE, geek));

    	label = getResources().getString(R.string.labelQualityTLead);
    	tl.addView(mkRatingRow(label, model.getQuality_tlead(), Characteristic.QUALITY_TLEAD, geek));

		label = getResources().getString(R.string.labelMood);
		tl.addView(mkRatingRow(label, model.getMood(), Characteristic.MOOD, geek));

		if (geek) {
			label = getResources().getString(R.string.labelErotic);
			tl.addView(mkRatingRow(label, model.getErotic(), Characteristic.EROTIC, geek));

			label = getResources().getString(R.string.labelHealth);
			tl.addView(mkRatingRow(label, model.getHealth(), Characteristic.HEALTH, geek));

	    	label = getResources().getString(R.string.labelAmbition);
	    	tl.addView(mkRatingRow(label, model.getAmbition(), Characteristic.AMBITION, geek));

	    	label = getResources().getString(R.string.labelCriminal);
	    	tl.addView(mkRatingRow(label, model.getCriminal(), Characteristic.CRIMINAL, geek));
		}

    	// past events
		StringBuilder pastEventLog = new StringBuilder();
		int sumCost = 0;
		int sumEarn = 0;
		SparseArray<Operation> modelOperation = new SparseArray<Operation>();
		int startDay = 0;

		List<Diary> diaryList = DiaryService.listEventsForModel(model.getId());
		for (Diary diary : diaryList) {

			// these events use the field modelId for movieId, so ignore them here:
			if (diary.getEventClass() == EventClass.MOVIE_START
					|| diary.getEventClass() == EventClass.MOVIE_PROGRESS
					|| diary.getEventClass() == EventClass.MOVIE_FINISH) continue;

			// text event listing
			pastEventLog.append('#').append(diary.getDay()).append(' ').append(diary.getDescription()).append("\n");
		}

		// collect data for chart
		// reverse order
		for (Diary diary : new ListReverser<Diary>(diaryList)) {

			// opchart diagram + win/loss sums
			if (startDay == 0 || diary.getDay() < startDay) startDay = diary.getDay();
			switch (diary.getEventClass()) {
			case BOOKING:
				modelOperation.put(diary.getDay(), Operation.BOOKING);
				sumEarn += diary.getAmount();
				break;
			case NOTIFICATION:
				switch (diary.getEventFlag()) {
				case SICK:
					modelOperation.put(diary.getDay(), Operation.SICK);
					break;
				case PAYCHECK:
				case TRAINING_FIN:
					sumCost += diary.getAmount();
					break;
				case GROUPWORK:
					sumEarn += diary.getAmount();
					break;
				default:
				}
				break;
			case ACCEPT:
				switch (diary.getEventFlag()) {
				case TRAINING:
					modelOperation.put(diary.getDay(), Operation.TRAINING);
					break;
				case VACATION:
					modelOperation.put(diary.getDay(), Operation.VACATION);
					break;
				case HIRE:
					modelOperation.put(diary.getDay(), Operation.HIRED);
					break;
				case QUIT:
					modelOperation.put(diary.getDay(), Operation.QUIT);
					break;
				case WIN_PERSON:
					sumEarn += diary.getAmount();
					break;
				case BONUS:
        		case PAYFIX_PERSON:
        		case PAYVAR_PERSON:
        		case PAYOPT_PERSON:
        		case LOSE_PERSON:
        		case CAR_UPDATE:
					sumCost += diary.getAmount();
					break;
				default:
				}
				break;
			case EXTRA_IN:
			case EXTRA_OUT:
			case EXTRA_LOSS:
				switch (diary.getEventFlag()) {
				case WIN_PERSON:
					sumEarn += diary.getAmount();
					break;
        		case PAYFIX_PERSON:
        		case PAYVAR_PERSON:
        		case PAYOPT_PERSON:
        		case LOSE_PERSON:
					sumCost += diary.getAmount();
					break;
				default:
				}
				break;
			case MOVIE_CAST:
				if (diary.getEventFlag() == EventFlag.MOVIE) {
					sumCost += diary.getAmount();
					modelOperation.put(diary.getDay(), Operation.MOVIE);
				}
			default:
			}
		}

		ModelService.Statistics st = DiaryService.getTotalStatistics();
		if (st.extraModelCount > 0) {
			int avgEarnings = (st.w4movieEarnings + st.w4photoEarnings + st.extraEarnings) / st.extraModelCount;
			int avgPayments = (st.w4payments + st.w4bonus + st.extraLoss) / st.extraModelCount;
			int maxavg = (avgEarnings > avgPayments) ? avgEarnings : avgPayments;

			if (maxavg > 0) {
				label = getResources().getString(R.string.labelExpenses);
				tl.addView(mkChartRow(label, sumCost, maxavg, Color.RED));
				label = getResources().getString(R.string.labelIncomes);
				tl.addView(mkChartRow(label, sumEarn, maxavg, Color.GREEN));
			}
		}

		// create chart
		if (startDay > 0) {
			GridView operationChartGrid = (GridView)findViewById(R.id.gridOpChart);

			hired = false;
			operationChartListItems = new ArrayList<OpChartElement>();
			for (int day = startDay; day <= DiaryService.today(); day++) {

				Operation op = Operation.FREE;
				if (modelOperation != null) op = modelOperation.get(day, Operation.FREE);
				if (op == Operation.HIRED || op == Operation.BOOKING) hired = true;
				if (op == Operation.QUIT) hired = false;
				if (hired && op == Operation.FREE) op = Operation.HIRED;

				operationChartListItems.add(new OpChartElement(
						day,
						op));
			}

			operationChartAdapter = new OpChartGridAdapter(
					this,
					android.R.layout.simple_list_item_1,
					R.id.gridOpChart,
					operationChartListItems,
					getLayoutInflater());

			operationChartGrid.setAdapter(operationChartAdapter);
			operationChartGrid.setTextFilterEnabled(true);
		}


		label = getResources().getString(R.string.labelPastEvents);
		tl.addView(mkTextRow(label, pastEventLog.toString()));
	}

	private TableRow mkRatingRow(String key, int value, Characteristic ch, boolean modifyable) {
		TableRow tr = new TableRow(this);

		TextView tv = new TextView(this);
		tv.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setText(key);
		tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
		tv.setPadding(4, 4, 4, 4);
		tr.addView(tv);

		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tr.addView(ll);

		RatingBar rb = new RatingBar(this, null, android.R.attr.ratingBarStyleSmall);
		rb.setTag(ch);
		rb.setStepSize((float) 0.1);
		rb.setIsIndicator(!modifyable);
		rb.setNumStars(10);
		rb.setRating((float) value / 10);
		rb.setOnRatingBarChangeListener(this);
		ll.addView(rb);

		return tr;
	}

	private TableRow mkChartRow(String key, int value, int maxavg, int color) {
		TableRow tr = new TableRow(this);

		TextView tv = new TextView(this);
		tv.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setText(key);
		tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
		tv.setPadding(4, 4, 4, 4);
		tr.addView(tv);

		double ac = (double)value / maxavg;
		int width = (int)(250 * ac);
		if (width > 500) width = 500;

		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout rl = (RelativeLayout)inflater.inflate( R.layout.element_vertical_bar, null );
		rl.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		LinearLayout bar = (LinearLayout)rl.findViewById(R.id.layoutBar);
		bar.setLayoutParams(new RelativeLayout.LayoutParams(width, LayoutParams.MATCH_PARENT));
		bar.setBackgroundColor(color);

		tv = (TextView)rl.findViewById(R.id.textViewValue);
//		tv.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setText(Util.amount(value));
		tv.setBackgroundColor(0x00FFFFFF);

		tr.addView(rl);

		return tr;
	}

	private TableRow mkTextRow(String key, String value) {
		TableRow tr = new TableRow(this);

		TextView tv = new TextView(this);
		tv.setText(key);
		tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
		tv.setPadding(4, 4, 4, 4);
		tr.addView(tv);

		tv = new TextView(this);
		tv.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setText(value);
		tv.setPadding(4, 4, 4, 4);
		tr.addView(tv);

		return tr;
	}

	public void refreshTaskList() {
		displayModelData();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonSave)
			saveChanges();
		else if (v.getId() == R.id.buttonHire)
			recruitModel();
		else if (v.getId() == R.id.buttonFire)
			dismissModel();
		else if (v.getId() == R.id.buttonAccount) {
			Intent intent = new Intent(this, AccountDetailDialog.class);
			intent.putExtra(AccountDetailDialog.EXTRA_MODEL_ID, modelId);
			startActivityForResult(intent, 1);
		}
		else if (v.getId() == R.id.imageCar) {
			Intent intent = new Intent(this, CarMaintenanceDialog.class);
			intent.putExtra(CarMaintenanceDialog.EXTRA_COMPANYCAR_ID, carId);
			startActivityForResult(intent, 1);
		}
		else {
			Log.e(LOG_TAG, "onClick for invalid view " + v);
		}
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if (!(sharedPref.getBoolean("geekMode", false))) return;
		int q = (int)(10 * ratingBar.getRating());
		ModelService.updateModelCharacteristic(modelId, (Characteristic)ratingBar.getTag(), q);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		displayModelData();
	}


	private void saveChanges() {

		Model model = ModelService.getModelById(modelId);

		EditText et = (EditText)findViewById(R.id.editTextVacation4week);
		int vacation = Util.atoi(et.getText().toString());
		boolean geek = false;
		if (vacation == 42) {
			vacation = 4;
			geek = true;
		}
		if (vacation > 0 && vacation != model.getVacation()) {
			String msg = getString(R.string.logmessage_vacation4week, model.getVacation(), vacation);
			ModelService.setVacation(modelId, vacation);
			DiaryService.log(msg, EventClass.NOTIFICATION, EventFlag.VACATION, modelId, 0);
		}

		et = (EditText)findViewById(R.id.editTextSalary);
		int salary = Util.atoi(et.getText().toString());
		if (salary > model.getSalary() || geek) {
			String msg = getString(R.string.logmessage_raise, Util.amount(model.getSalary()), Util.amount(salary));
			ModelService.grantRaise(modelId, salary);
			DiaryService.log(msg, EventClass.ACCEPT, EventFlag.RAISE, modelId, salary);
			TodayService.dropEvents(modelId, EventClass.REQUEST, EventFlag.RAISE);
		}

		et = (EditText)findViewById(R.id.editTextVacationRest);
		vacation = Util.atoi(et.getText().toString());
		if (vacation > model.getVacrest() || geek) {
			String msg = getString(R.string.logmessage_vacationRest, model.getVacrest(), vacation);
			ModelService.setVacationRest(modelId, vacation);
			DiaryService.log(msg, EventClass.NOTIFICATION, EventFlag.VACATION, modelId, 0);
		}

		et = (EditText)findViewById(R.id.editTextBonus);
		int bonus = Util.atoi(et.getText().toString());
		if (bonus > 0) {
			ModelService.grantBonus(modelId, bonus);
			TodayService.dropEvents(modelId, EventClass.REQUEST, EventFlag.BONUS);
		}

		Spinner sp = (Spinner)findViewById(R.id.selectCar);
		if ((model.getCarId() == 0 && sp.getSelectedItemPosition() != 0)
				|| (model.getCarId() != 0 && sp.getSelectedItemPosition() != 1)) {

			if (model.getCarId() != 0)
				CarService.log(model.getCarId(), CarAction.TAKEAWAY,
						getResources().getString(R.string.logmessage_car_takeaway),
						CarService.getCarValue(model.getCarId()));

			CarOffer co = (CarOffer)sp.getSelectedItem();
			int carId = co.getCompanyCarId();
			if (carId == 0) {
				// must buy first
				int ct = co.getCarTypeId();
				if (ct != 0) carId = CarService.buyCar(ct);
			}
			ModelService.setCar(modelId, carId);
			CarService.log(carId, CarAction.ASSIGN,
					getResources().getString(R.string.logmessage_car_assign2) + " " + model.getFullname(),
					CarService.getCarValue(carId));

			String msg;
			if (model.getCarId() == 0)
				msg = getString(R.string.logmessage_car_assign, co.toString());
			else
				msg = getString(R.string.logmessage_car_change, co.toString());

			DiaryService.log(msg, EventClass.ACCEPT, EventFlag.CAR_UPDATE, modelId, co.getPrice());
			TodayService.dropEvents(modelId, EventClass.REQUEST, EventFlag.CAR_UPDATE);
			TodayService.dropEvents(modelId, EventClass.NOTIFICATION, EventFlag.CAR_BROKEN, EventFlag.CAR_STOLEN, EventFlag.CAR_WRECKED);
		}

		sp = (Spinner)findViewById(R.id.selectTraining);
		if (sp.getSelectedItemPosition() != 0) {
			Training training = (Training)sp.getSelectedItem();
			ModelService.sendToTraining(modelId, training.getId());
			TodayService.dropEvents(modelId, EventClass.REQUEST, EventFlag.TRAINING, EventFlag.VACATION);
		}

		sp = (Spinner)findViewById(R.id.selectTeam);
		TeamOption to = (TeamOption)sp.getSelectedItem();
		if (to.getTeamId() == ModelService.TEAM_NEW_TEAM) {
			// create new team and make model 1st leader
			int teamId = ModelService.createTeam(model.getId());
			ModelService.setTeam(model.getId(), teamId);
		}
		else if (to.getTeamId() != model.getTeamId()) {
			// change team
			ModelService.setTeam(model.getId(), to.getTeamId());
		}


		finish();
	}

	private void dismissModel() {
		Model model = ModelService.getModelById(modelId);
		compensation = ModelService.getQuitCompensation(modelId);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.labelDismissal));
		String question = getResources().getString(R.string.questionQuitCompensation, model.getFullname(), Util.amount(compensation));
		builder.setMessage(question);

		builder.setPositiveButton(getResources().getString(R.string.display_option_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				TransactionService.transfer(0, modelId, compensation, getResources().getString(R.string.accountmessage_compensation));
				TodayService.dropRequests(modelId);
				ModelService.release(modelId);
				DiaryService.log(getResources().getString(R.string.logmessage_dismissal), EventClass.ACCEPT, EventFlag.QUIT, modelId, compensation);
				finish();

				dialog.dismiss();
			}
		});

		builder.setNegativeButton(getResources().getString(R.string.display_option_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private void recruitModel() {
		// Salary
		EditText et = (EditText)findViewById(R.id.editTextSalary);
		int salary = Util.atoi(et.getText().toString());
		int expect = ModelService.getAverageSalary(modelId);
		if (salary < expect) {
			String msg = getResources().getString(R.string.display_msg_hire_insuff_offer);
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			return;
		}

		// Vacation
		et = (EditText)findViewById(R.id.editTextVacation4week);
		int vacation = Util.atoi(et.getText().toString());
		if (vacation < 4) { // FIXME: use value from some kind of settings
			String msg = getResources().getString(R.string.display_msg_hire_insuff_offer);
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			return;
		}

		// Bonus
		et = (EditText)findViewById(R.id.editTextBonus);
		int bonus = Util.atoi(et.getText().toString());
		if (bonus > 0) {
			ModelService.grantBonus(modelId, bonus);
		}

		// Team
		int teamId = 0;
		Spinner sp = (Spinner)findViewById(R.id.selectTeam);
		TeamOption to = (TeamOption)sp.getSelectedItem();
		if (to != null) {
			teamId = to.getTeamId();
			if (teamId == ModelService.TEAM_NEW_TEAM) {
				// create new team and make model 1st leader
				teamId = ModelService.createTeam(modelId);
			}
		}

		ModelService.hire(modelId, salary, vacation, teamId);
		Model model = ModelService.getModelById(modelId);
		DiaryService.log(getResources().getString(R.string.logmessage_hire, model.getFullname(), Util.amount(salary)),
				EventClass.ACCEPT, EventFlag.HIRE, modelId, salary);

		// clear application Today-entries
		ModelService.clearApplications(modelId);

		// Car
		sp = (Spinner)findViewById(R.id.selectCar);
		CarOffer co = (CarOffer)sp.getSelectedItem();
		if (co != null) {
			int companyCarId = co.getCompanyCarId();
			int carTypeId = co.getCarTypeId();
			int carPrice = co.getPrice();
			if (companyCarId > 0 || carTypeId > 0) {
				if (companyCarId == 0) {
					// must buy first
					companyCarId = CarService.buyCar(carTypeId);
				}
				ModelService.setCar(modelId, companyCarId);
				CarService.log(companyCarId, CarAction.ASSIGN,
						getResources().getString(R.string.logmessage_car_assign, model.getFullname()),
						CarService.getCarValue(companyCarId));

				String msg = getResources().getString(R.string.logmessage_car_assign, CarService.getCarInfo(companyCarId));
				DiaryService.log(msg, EventClass.ACCEPT, EventFlag.CAR_UPDATE, modelId, carPrice);
			}
		}

        StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

        displayModelData();
	}

	public void modelQuickBonus(View view) {
		ModelService.showStandardBonusButtons(this, modelId, this);
	}
}
