package de.rlill.modelmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import de.rlill.modelmanager.dialog.AccountDetailDialog;
import de.rlill.modelmanager.dialog.ModelNegotiationDialog;
import de.rlill.modelmanager.fragments.AccountFragment;
import de.rlill.modelmanager.fragments.CompanyCarFragment;
import de.rlill.modelmanager.fragments.DailyBusinessFragment;
import de.rlill.modelmanager.fragments.MovieproductionFragment;
import de.rlill.modelmanager.fragments.MyModelsFragment;
import de.rlill.modelmanager.fragments.OperationchartFragment;
import de.rlill.modelmanager.fragments.TeamFragment;
import de.rlill.modelmanager.fragments.TrainingFragment;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.persistance.DbAdapter;
import de.rlill.modelmanager.service.MessageService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.TodayService;
import de.rlill.modelmanager.struct.CarClass;
import de.rlill.modelmanager.struct.CarStatus;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.MovieStatus;
import de.rlill.modelmanager.struct.MovieType;
import de.rlill.modelmanager.struct.TrainingStatus;
import de.rlill.modelmanager.struct.ViewElements;
import de.rlill.modelmanager.struct.Weekday;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	private static final String LOG_TAG = "MM*" + MainActivity.class.getSimpleName();

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;


	private List<ActivityFragmentProvider> activityFragments = new ArrayList<ActivityFragmentProvider>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_daily_business);
			};
			public Fragment getActivityFragment() {
				return new DailyBusinessFragment();
			}
		});

		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_my_models);
			};
			public Fragment getActivityFragment() {
				return new MyModelsFragment();
			}
		});

		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_cars);
			};
			public Fragment getActivityFragment() {
				return new CompanyCarFragment();
			}
		});

		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_training);
			};
			public Fragment getActivityFragment() {
				return new TrainingFragment();
			}
		});

		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_account);
			};
			public Fragment getActivityFragment() {
				return new AccountFragment();
			}
		});
/*
		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_incidents);
			};
			public Fragment getActivityFragment() {
				return new IncidentFragment();
			}
		});
*/
		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_teams);
			};
			public Fragment getActivityFragment() {
				return new TeamFragment();
			}
		});

		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_operation);
			};
			public Fragment getActivityFragment() {
				return new OperationchartFragment();
			}
		});

		activityFragments.add(new ActivityFragmentProvider() {
			public String getTitle(Resources res) {
				return res.getString(R.string.title_section_movieproduction);
			};
			public Fragment getActivityFragment() {
				return new MovieproductionFragment();
			}
		});


		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		Context ctx = getApplicationContext();
		DbAdapter.init(ctx);

		Weekday.translate(ctx);
		ModelStatus.translate(ctx);
		CarClass.translate(ctx);
		CarStatus.translate(ctx);
		TrainingStatus.translate(ctx);
		MessageService.translate(ctx);
		MovieType.translate(ctx);
		MovieStatus.translate(ctx);


		ModelService.setUndefinedModel(getResources().getString(R.string.displaySomeModel),
				"internetexplorer");

		View fv = getCurrentFocus();
		if (fv != null) fv.clearFocus();

		Log.i(LOG_TAG, "initialized");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_settings:
			// Launch settings activity
			intent = new Intent(this, PreferenceActivity.class);
			startActivity(intent);
			break;
		case R.id.action_account:
			// Launch account
			intent = new Intent(this, AccountDetailDialog.class);
			intent.putExtra(AccountDetailDialog.EXTRA_MODEL_ID, 0);
			startActivity(intent);
			break;
		}
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch
		// to the corresponding page in the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());

		// remove focus
		View fv = getCurrentFocus();
		if (fv != null) fv.clearFocus();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void dailyBusinessNegotiate(View view) {
	    Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Title");
	    builder.setMessage("negotiate");
	    builder.setPositiveButton("ok", null);
	    builder.show();
	}

	public void dailyBusinessDeny(View view) {
		ViewElements ve = (ViewElements)view.getTag();
		if (ve != null) {
			TodayService.reject(this, ve.getContextToday());
			ve.updateParentView();
		}
	}

	public void dailyBusinessAccept(View view) {
		ViewElements ve = (ViewElements)view.getTag();
		if (ve != null) {
			TodayService.accept(this, ve);
			ve.updateParentView();
			refreshModelList();
		}
	}

	public void dailyBusinessOffer(View view) {
		ViewElements ve = (ViewElements)view.getTag();
		if (ve != null) {
			TodayService.offer(this, ve.getContextToday(), ve.getFormularData());
			ve.updateParentView();
			refreshModelList();
		}
	}

	public void refreshModelList() {
		// refresh Model List
		// TODO: index 1 might change!!! Find other way of addressing MyModelsFragment!
		MyModelsFragment modelList = (MyModelsFragment)getSupportFragmentManager().getFragments().get(1);
		modelList.refreshData();
	}

	public void modelViewDetails(View view) {
		ViewElements ve = (ViewElements)view.getTag();
		if (ve != null) {
			Model model = ve.getContextModel();
			if (model != null && model.getId() > 0) {
				Intent intent = new Intent(this, ModelNegotiationDialog.class);
			    intent.putExtra(ModelNegotiationDialog.EXTRA_MODEL_ID, model.getId());
			    startActivity(intent);
			}
		}
	}

	public void modelViewAccount(View view) {
		ViewElements ve = (ViewElements)view.getTag();
		if (ve != null) {
			Model model = ve.getContextModel();
			if (model != null && model.getId() > 0) {
				Intent intent = new Intent(this, AccountDetailDialog.class);
				intent.putExtra(AccountDetailDialog.EXTRA_MODEL_ID, model.getId());
				startActivity(intent);
			}
		}
	}

	// This method is called once the menu is selected
	public void carShopping(View v) {
		// TODO: replacement for debug-purposes - revert!
//		Intent i = new Intent(this, CarShoppingDialog.class);
//		startActivity(i);
		Intent intent = new Intent(this, PreferenceActivity.class);
		startActivity(intent);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			ActivityFragmentProvider p = activityFragments.get(position);
			if (p == null) throw new IllegalArgumentException("ItemPosition " + position + " not initialized");
			return p.getActivityFragment();
		}

		@Override
		public int getCount() {
			return activityFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			ActivityFragmentProvider p = activityFragments.get(position);
			if (p == null) throw new IllegalArgumentException("ItemPosition " + position + " not initialized");
			return p.getTitle(getResources()).toUpperCase();
		}
	}


	public interface ActivityFragmentProvider {
		public String getTitle(Resources res);
		public Fragment getActivityFragment();
	}
}
