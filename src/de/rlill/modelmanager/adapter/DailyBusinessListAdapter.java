package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.service.CarService;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.MessageService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.TrainingService;
import de.rlill.modelmanager.struct.BonusButtonListener;
import de.rlill.modelmanager.struct.BonusOptionListener;
import de.rlill.modelmanager.struct.CarOffer;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.RaiseButtonListener;
import de.rlill.modelmanager.struct.RejectReasons;
import de.rlill.modelmanager.struct.TaskListRefresher;
import de.rlill.modelmanager.struct.TeamOption;
import de.rlill.modelmanager.struct.ViewElements;

public class DailyBusinessListAdapter extends ArrayAdapter<Today> {

	private static final String LOG_TAG = "MM*" + DailyBusinessListAdapter.class.getSimpleName();

	private Context appContext;
    private LayoutInflater mInflater;
    private int activeIndex;
    private Fragment parentFragment;
    private int rememberTodayId;
    private int rememberCompanyCarId;
    private int rememberCarTypeId;
    private int rememberTeamId;
    private int rememberAmount;
    private int rememberVacation;
    private int rememberBonus;
    private int rememberOffer;
    private boolean rememberFire;

	public DailyBusinessListAdapter(Context context, int resource, int textViewResourceId,
			List<Today> objects, LayoutInflater inflater, Fragment parentFragment) {
		super(context, resource, textViewResourceId, objects);
		appContext = context;
        mInflater = inflater;
        activeIndex = 0;
        this.parentFragment = parentFragment;
	}

	public void setActiveIndex(int i) { activeIndex = i; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		Today today = getItem(position);
		DailyBusinessViewElements viewElements = null;

		convertView = createView(position, today);
		viewElements = new DailyBusinessViewElements(convertView);
		convertView.setTag(viewElements);

		Model model = today.getModel();
		viewElements.setContextModel(model);
		viewElements.setContextToday(today);

		// event icon
		viewElements.getIcon().setImageResource(today.getEvent().getIcon().getResourceId());

		// face icon
		ImageView image = viewElements.getImage();
		if (model != null) {
			int ir = appContext.getResources().getIdentifier(model.getImage(), "drawable", appContext.getPackageName());
			image.setVisibility(ImageView.VISIBLE);
			image.setImageResource(ir);
		}
		else {
			image.setVisibility(ImageView.INVISIBLE);
		}

		// account icon
		ImageView accountIcon = viewElements.getAccountIcon();
		accountIcon.setVisibility((model != null && model.getStatus() != ModelStatus.FREE)
				? ImageView.VISIBLE : ImageView.INVISIBLE);
		viewElements.setDescription(today.getDescription());
		viewElements.setUpdateableView(parentFragment);

		// description
		if (today.getEvent().getEclass() == EventClass.NOTIFICATION
				&& today.getEvent().getFlag() == EventFlag.CAR_STOLEN) {
			Diary d = DiaryService.getDiaryEntry(today.getAmount2());
			if (d != null)
				viewElements.setDescription(d.getDescription());
		}

		// expanded view for active element
		if (position == activeIndex) {

			if (today.getId() != rememberTodayId) {
				rememberTeamId = ModelService.TEAM_NO_TEAM;
				rememberCompanyCarId = 0;
				rememberCarTypeId = 0;
				rememberAmount = 0;
				rememberVacation = 0;
				rememberBonus = 0;
				rememberOffer = 0;
				rememberFire = false;
			}

			// description
			if (today.getEvent().getEclass() == EventClass.NOTIFICATION
					&& today.getEvent().getFlag() == EventFlag.TRAINING_FIN) {
				Diary d = DiaryService.getDiaryEntry(today.getAmount2());
				if (d != null)
					viewElements.setDescription(d.getDescription());
			}

			// Training
			if (today.getEvent().getEclass() == EventClass.REQUEST
					&& today.getEvent().getFlag() == EventFlag.TRAINING) {
				Spinner sp = viewElements.getTrainingSelect();
				if (sp != null) TrainingService.initSpinner(getContext(), sp);

				viewElements.setPresentation(MessageService.trainingHistory(getContext(), today.getModelId()));
			}

			// Car
			if (today.getEvent().getEclass() == EventClass.REQUEST
					&& today.getEvent().getFlag() == EventFlag.CAR_UPDATE) {
				Spinner sp = viewElements.getCarSelect();
				if (sp != null) CarService.initSpinner(getContext(), sp, 0);
			}

			// Booking
			if (today.getEvent().getEclass() == EventClass.BOOKING) {
				viewElements.setNegotiationOffer(today.getAmount1());
				Spinner sp = viewElements.getReplacementSelect();
				if (sp != null) {
					List<Model> substList = ModelService.getModelsForBooking(today.getEvent().getFlag());
//					Log.w(LOG_TAG, "found " + substList.size() + " alternatives");
					ArrayAdapter<Model> replacementAdapter = new ModelSpinnerAdapter(
							appContext,
							R.layout.fragment_model_spinner_item,
							R.id.textView1,
							substList,
							mInflater,
							today.getEvent().getFlag());

					sp.setAdapter(replacementAdapter);
					int p = substList.indexOf(model);
					if (p < 0) p = 0;
					sp.setSelection(p);
				}
				Button b = (Button)convertView.findViewById(R.id.buttonOk);
				int visibility = View.VISIBLE;
				if (model.getId() <= 0) visibility = View.INVISIBLE;
				if (model.getStatus() != ModelStatus.HIRED) visibility = View.INVISIBLE;
				if (b != null) b.setVisibility(visibility);
			}

			// Booking rejection
			if (today.getEvent().getEclass() == EventClass.BOOKREJECT) {
				viewElements.setPresentation(MessageService.workRejectReason(getContext(), model));

				RejectReasons rr = ModelService.bookingRejectReasons(model.getId());
				if (rr.carMissing) {
					Spinner sp = viewElements.getCarSelect();
					CarService.initSpinner(appContext, sp, 0);
				}
				else {
					// hide car select
					TableRow tr = (TableRow)convertView.findViewById(R.id.tableRowCar);
					tr.setVisibility(View.GONE);
				}

				if (rr.bonusMissing > 0) {
					viewElements.setNegotiationOffer(rr.bonusMissing);
					TextView tv = (TextView)convertView.findViewById(R.id.textViewDescriptionOffer);
					tv.setText(R.string.labelBonus);
				}
				else {
					// hide offer input
					TableRow tr = (TableRow)convertView.findViewById(R.id.tableRowOffer);
					tr.setVisibility(View.GONE);
				}

				TextView tv = (TextView)convertView.findViewById(R.id.textViewDescriptionOk);
				tv.setText(R.string.labelVacation);

				// update description text for cancel button ( -> set undefined model)
				tv = (TextView)convertView.findViewById(R.id.textViewDescriptionDeny);
				tv.setText(R.string.display_offer_other_model);
			}

			// Application
			if (today.getEvent().getEclass() == EventClass.APPLICATION) {
				viewElements.setSalary((rememberAmount > 0) ? rememberAmount : today.getAmount1());
				viewElements.setVacation(4);  // TODO: take value from preferences

				// add self-presentation
				viewElements.setPresentation(MessageService.applicationSelfPresentation(
						appContext, model, today.getAmount1(), 0, 0, null));

				Spinner sp = viewElements.getTeamSelect();
				ModelService.initTeamSpinner(appContext, sp, rememberTeamId);

				sp = viewElements.getCarSelect();
				CarService.initSpinner(getContext(), sp, rememberCompanyCarId, rememberCarTypeId);

				if (rememberBonus > 0) viewElements.setBonus(rememberBonus);

				if (rememberVacation > 0) viewElements.setVacation(rememberVacation);
			}

			// Raise
			if (today.getEvent().getEclass() == EventClass.REQUEST
					&& today.getEvent().getFlag() == EventFlag.RAISE) {
				viewElements.setNegotiationOffer(today.getAmount1());
				viewElements.enableMoneyButton(new RaiseButtonListener(getContext(), model.getId(), (TaskListRefresher)parentFragment));
			}

			// Bonus
			if (today.getEvent().getEclass() == EventClass.REQUEST
					&& today.getEvent().getFlag() == EventFlag.BONUS) {
				viewElements.setPresentation(MessageService.recentWorkPresentation(appContext, model));
				if (rememberOffer > 0) viewElements.setNegotiationOffer(rememberOffer);

				viewElements.enableMoneyButton(new BonusButtonListener(getContext(), model.getId(), (TaskListRefresher)parentFragment));
			}

			// Extras
			if ((today.getEvent().getEclass() == EventClass.EXTRA_OUT
					|| today.getEvent().getEclass() == EventClass.EXTRA_LOSS)
				&& (today.getEvent().getFlag() == EventFlag.PAYVAR_PERSON
					|| today.getEvent().getFlag() == EventFlag.PAYOPT_PERSON)) {
				viewElements.setNegotiationOffer(rememberOffer > 0 ? rememberOffer : today.getAmount1());
			}
			if (today.getEvent().getEclass() == EventClass.EXTRA_LOSS) {
				// set quit checkbox
				CheckBox cb = (CheckBox)convertView.findViewById(R.id.checkBoxFire);
				cb.setChecked(rememberFire);
			}

			// Reclaims
			if (today.getEvent().getEclass() == EventClass.EXTRA_LOSS
					&& (today.getEvent().getFlag() == EventFlag.PAYFIX_PERSON
						|| today.getEvent().getFlag() == EventFlag.LOSE_PERSON)) {
				viewElements.setReclaim(today.getAmount1());
			}

			// Quit request
			if (today.getEvent().getEclass() == EventClass.REQUEST
					&& today.getEvent().getFlag() == EventFlag.QUIT) {
				viewElements.setSalary(model.getSalary());
				viewElements.setBonus((rememberBonus > 0) ? rememberBonus : ModelService.getExpectedBonus(model.getId()));
			}

			// Gambling
			if (today.getEvent().getEclass() == EventClass.GAMBLE) {
				Button b = (Button)convertView.findViewById(R.id.buttonPlay1);
				GameButtonViewElements ve = new GameButtonViewElements(b);
				ve.setContextView(convertView);
				ve.setContextToday(today);
				ve.setContextInt(1);
				b.setTag(ve);

				b = (Button)convertView.findViewById(R.id.buttonPlay2);
				ve = new GameButtonViewElements(b);
				ve.setContextView(convertView);
				ve.setContextToday(today);
				ve.setContextInt(2);
				b.setTag(ve);
			}

		}

		return convertView;
	}

	private View createView(int position, Today today) {
		View view = null;
		if (position == activeIndex) {
			switch (today.getEvent().getEclass()) {
			case NOTIFICATION:
				view = mInflater.inflate(R.layout.fragment_dailytask_notification, null);
				break;
			case BOOKING:
				view = mInflater.inflate(R.layout.fragment_dailytask_booking, null);
				break;
			case BOOKREJECT:
				view = mInflater.inflate(R.layout.fragment_dailytask_bookreject, null);
				break;
			case APPLICATION:
				view = mInflater.inflate(R.layout.fragment_dailytask_hiring, null);
				break;
			case GAMBLE:
				view = mInflater.inflate(R.layout.fragment_dailytask_gamble, null);
				break;
			case REQUEST:
				switch (today.getEvent().getFlag()) {
				case BONUS:
					view = mInflater.inflate(R.layout.fragment_dailytask_messagecancel, null);
					break;
				case RAISE:
					view = mInflater.inflate(R.layout.fragment_dailytask_cancel, null);
					break;
				case QUIT:
					view = mInflater.inflate(R.layout.fragment_dailytask_quit, null);
					break;
				case VACATION:
					view = mInflater.inflate(R.layout.fragment_dailytask_accept, null);
					break;
				case TRAINING:
					view = mInflater.inflate(R.layout.fragment_dailytask_training, null);
					break;
				case CAR_UPDATE:
					view = mInflater.inflate(R.layout.fragment_dailytask_car, null);
					break;
				default:
					view = mInflater.inflate(R.layout.fragment_dailytask_notification, null);
				}
				break;
			case EXTRA_OUT:
				switch (today.getEvent().getFlag()) {
				case PAYVAR_PERSON:
					view = mInflater.inflate(R.layout.fragment_dailytask_negotiation, null);
					break;
				case PAYOPT_PERSON:
					view = mInflater.inflate(R.layout.fragment_dailytask_cancel, null);
					break;
				default:
					view = mInflater.inflate(R.layout.fragment_dailytask_notification, null);
				}
				break;
			case EXTRA_LOSS:
				switch (today.getEvent().getFlag()) {
				case PAYVAR_PERSON:
					view = mInflater.inflate(R.layout.fragment_dailytask_negofire, null);
					break;
				case PAYOPT_PERSON:
					view = mInflater.inflate(R.layout.fragment_dailytask_cancelfire, null);
					break;
				default:
					view = mInflater.inflate(R.layout.fragment_dailytask_reclaim, null);
				}
				break;
			default:
				view = mInflater.inflate(R.layout.fragment_dailytask_notification, null);
			}
		} else {
			view = mInflater.inflate(R.layout.fragment_dailytask_preview, null);
		}
		return view;
	}

	private class GameButtonViewElements extends ViewElements {

		public GameButtonViewElements(View view) {
			super(view);
		}

		public void setContextView(View v) {
			adaptedView = v;
		}

		@Override
		public SparseArray<String> getFormularData() {
			SparseArray<String> result = new SparseArray<String>();
			EditText et = (EditText)adaptedView.findViewById(R.id.editTextBet1);
			result.put(R.string.labelBet1, et.getText().toString());
			et = (EditText)adaptedView.findViewById(R.id.editTextBet2);
			result.put(R.string.labelBet2, et.getText().toString());
			return result;
		}

	}

    private class DailyBusinessViewElements extends ViewElements {
        private ImageView image = null;
        private ImageView icon = null;
        private ImageView account = null;
        private Spinner car = null;
        private Spinner training = null;
        private Spinner replacement = null;
        private Spinner team = null;

    	public DailyBusinessViewElements(View view) {
    		super(view);
    	}

    	public ImageView getImage() {
    		if (image == null) image = (ImageView) adaptedView.findViewById(R.id.messageImage);
    		return image;
    	}
    	public ImageView getIcon() {
    		if (icon == null) icon = (ImageView) adaptedView.findViewById(R.id.messageIcon);
    		return icon;
    	}
    	public ImageView getAccountIcon() {
    		if (account == null) account = (ImageView) adaptedView.findViewById(R.id.accountIcon);
    		return account;
    	}

    	public Spinner getCarSelect() {
    		if (car == null) car = (Spinner) adaptedView.findViewById(R.id.selectCar);
    		return car;
    	}

    	public Spinner getTrainingSelect() {
    		if (training == null) training = (Spinner) adaptedView.findViewById(R.id.selectTraining);
    		return training;
    	}

    	public void setNegotiationOffer(int offer) {
    		findTextView(R.id.editTextOffer).setText(Integer.toString(offer));
    	}

    	public void setReclaim(int offer) {
    		findTextView(R.id.editTextReclaim).setText(Integer.toString(offer));
    	}

    	public Spinner getReplacementSelect() {
    		if (replacement == null) replacement = (Spinner) adaptedView.findViewById(R.id.selectSubstitute);
    		return replacement;
    	}

    	public Spinner getTeamSelect() {
    		if (team == null) team = (Spinner) adaptedView.findViewById(R.id.selectTeam);
    		return team;
    	}

    	public void setDescription(String description) {
    		findTextView(R.id.messageText).setText(description);
    	}

    	public void setPresentation(String presentation) {
    		findTextView(R.id.textViewPresentation).setText(presentation);
    	}

    	public void setSalary(int salary) {
    		findTextView(R.id.editSalary).setText(Integer.toString(salary));
    	}

    	public void setBonus(int bonus) {
    		findTextView(R.id.editBonus).setText(Integer.toString(bonus));
    	}

    	public void setVacation(int vacation) {
    		findTextView(R.id.editVacation).setText(Integer.toString(vacation));
    	}

	    public void enableMoneyButton(View.OnClickListener listener) {
		    ImageView b = (ImageView) adaptedView.findViewById(R.id.standardBonusIcon);
		    b.setVisibility(View.VISIBLE);
		    b.setOnClickListener(listener);
	    }

    	@Override
    	public SparseArray<String> getFormularData() {
    		SparseArray<String> result = new SparseArray<String>();
    		Today today = getItem(activeIndex);
    		rememberTodayId = today.getId();

    		// Salary
			EditText et = (EditText)adaptedView.findViewById(R.id.editSalary);
			if (et != null) {
				result.put(R.string.labelSalary, et.getText().toString());
				rememberAmount = Util.atoi(et.getText().toString());
			}

			// Bonus
			et = (EditText)adaptedView.findViewById(R.id.editBonus);
			if (et != null) {
				result.put(R.string.labelBonus, et.getText().toString());
				rememberBonus = Util.atoi(et.getText().toString());
			}

			// Vacation
			et = (EditText)adaptedView.findViewById(R.id.editVacation);
			if (et != null) {
				result.put(R.string.labelVacation, et.getText().toString());
				rememberVacation = Util.atoi(et.getText().toString());
			}

			// Negotiation offer
			et = (EditText)adaptedView.findViewById(R.id.editTextOffer);
			if (et != null) {
				result.put(R.string.labelTheOffer, et.getText().toString());
				rememberOffer = Util.atoi(et.getText().toString());
			}

			// Reclaim
			et = (EditText)adaptedView.findViewById(R.id.editTextReclaim);
			if (et != null) result.put(R.string.labelTheReclaim, et.getText().toString());

			// Negotiation offer
			CheckBox cb = (CheckBox)adaptedView.findViewById(R.id.checkBoxFire);
			if (cb != null) {
				result.put(R.string.labelFireImmmediately, cb.isChecked() ? "1" : "0");
				rememberFire = cb.isChecked();
			}

			// Car
			Spinner sp = getCarSelect();
			if (sp != null) {
				CarOffer co = (CarOffer)sp.getSelectedItem();
				if (co != null) {
					result.put(R.string.labelCarTypes, Integer.toString(co.getCarTypeId()));
					result.put(R.string.labelCar, Integer.toString(co.getCompanyCarId()));
					result.put(R.string.labelPrice, Integer.toString(co.getPrice()));
					rememberCompanyCarId = co.getCompanyCarId();
					rememberCarTypeId = co.getCarTypeId();
				}
			}

			// Training
			sp = getTrainingSelect();
			if (sp != null) {
				Training tr = (Training)sp.getSelectedItem();
				if (tr != null) result.put(R.string.labelTraining, Integer.toString(tr.getId()));
			}

			// Team
			sp = getTeamSelect();
			if (sp != null) {
				TeamOption to = (TeamOption)sp.getSelectedItem();
				if (to != null) {
					result.put(R.string.labelTeam, Integer.toString(to.getTeamId()));
					rememberTeamId = to.getTeamId();
				}
			}

			// Booking
			et = (EditText)adaptedView.findViewById(R.id.editTextOffer);
			if (et != null) result.put(R.string.labelTheOffer, et.getText().toString());
			sp = (Spinner)adaptedView.findViewById(R.id.selectSubstitute);
			if (sp != null) {
				Model m = (Model)sp.getSelectedItem();
				if (m != null)
					result.put(R.string.labelSubstitute, Integer.toString(m.getId()));
			}

    		return result;
    	}
    }

}
