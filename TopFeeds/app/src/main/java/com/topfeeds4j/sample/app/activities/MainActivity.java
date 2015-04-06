package com.topfeeds4j.sample.app.activities;

import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.chopping.bus.CloseDrawerEvent;
import com.chopping.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.adapters.NewsListPagersAdapter;
import com.topfeeds4j.sample.app.events.EULAConfirmedEvent;
import com.topfeeds4j.sample.app.events.EULARejectEvent;
import com.topfeeds4j.sample.app.events.LoadMoreEvent;
import com.topfeeds4j.sample.app.events.OpenLinkEvent;
import com.topfeeds4j.sample.app.events.ShowProgressIndicatorEvent;
import com.topfeeds4j.sample.app.fragments.AboutDialogFragment;
import com.topfeeds4j.sample.app.fragments.AboutDialogFragment.EulaConfirmationDialog;
import com.topfeeds4j.sample.app.fragments.AppListImpFragment;
import com.topfeeds4j.sample.utils.Prefs;

import retrofit.Callback;
import retrofit.RetrofitError;


public class MainActivity extends BaseActivity {
	/**
	 * The pagers
	 */
	private ViewPager mViewPager;
	/**
	 * Tabs.
	 */
	private PagerSlidingTabStrip mTabs;
	/**
	 * Adapter for {@link #mViewPager}.
	 */
	private NewsListPagersAdapter mPagerAdapter;


	/**
	 * Navigation drawer.
	 */
	private DrawerLayout mDrawerLayout;
	/**
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.chopping.bus.CloseDrawerEvent}.
	 *
	 * @param e
	 * 		Event {@link com.chopping.bus.CloseDrawerEvent}.
	 */
	public void onEvent(CloseDrawerEvent e) {
		mDrawerLayout.closeDrawers();
	}

	/**
	 * Handler for {@link com.topfeeds4j.sample.app.events.OpenLinkEvent}.
	 *
	 * @param e
	 * 		Event {@link com.topfeeds4j.sample.app.events.OpenLinkEvent}.
	 */
	public void onEvent(OpenLinkEvent e) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(e.getUrl()));
		startActivity(i);
	}

	/**
	 * Handler for {@link com.topfeeds4j.sample.app.events.LoadMoreEvent}.
	 *
	 * @param e
	 * 		Event {@link com.topfeeds4j.sample.app.events.LoadMoreEvent}.
	 */
	public void onEvent(LoadMoreEvent e) {
		Utils.showShortToast(getApplicationContext(), R.string.lbl_load_more);
	}

	/**
	 * Handler for {@link com.topfeeds4j.sample.app.events.ShowProgressIndicatorEvent}.
	 *
	 * @param e
	 * 		Event {@link com.topfeeds4j.sample.app.events.ShowProgressIndicatorEvent}.
	 */
	public void onEvent(ShowProgressIndicatorEvent e) {
		if (e.isShow()) {
			findViewById(R.id.loading_pb).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.loading_pb).setVisibility(View.GONE);
		}
	}


	/**
	 * Handler for {@link  EULARejectEvent}.
	 *
	 * @param e
	 * 		Event {@link  EULARejectEvent}.
	 */
	public void onEvent(EULARejectEvent e) {
		finish();
	}

	/**
	 * Handler for {@link EULAConfirmedEvent}
	 *
	 * @param e
	 * 		Event {@link  EULAConfirmedEvent}.
	 */
	public void onEvent(EULAConfirmedEvent e) {

		initViewPager();

	}


	//------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Actionbar and navi-drawer.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


		//Navi-drawer.
		initDrawer();


	}


	@Override
	public void onResume() {
		super.onResume();
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {

		Api.getTinyUrl(getString(R.string.lbl_store_url, getPackageName()), new Callback<Response>() {
			@Override
			public void success(Response response, retrofit.client.Response response2) {

				MenuItem menuShare = menu.findItem(R.id.action_share_app);
				//Getting the actionprovider associated with the menu item whose id is share.
				android.support.v7.widget.ShareActionProvider provider =
						(android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);
				//Setting a share intent.
				String subject = String.format(getString(R.string.lbl_share_app_title), getString(
						R.string.application_name));
				String text = getString(R.string.lbl_share_app_content, getString(R.string.application_name), response.getResult() );
				provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));
			}

			@Override
			public void failure(RetrofitError error) {

				MenuItem menuShare = menu.findItem(R.id.action_share_app);
				//Getting the actionprovider associated with the menu item whose id is share.
				android.support.v7.widget.ShareActionProvider provider =
						(android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);
				//Setting a share intent.
				String subject = String.format(getString(R.string.lbl_share_app_title), getString(R.string.application_name));
				String text = getString(R.string.lbl_share_app_content,  getString(R.string.application_name), getString(R.string.lbl_store_url, getPackageName()));
				provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));
			}
		});

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();

		switch (item.getItemId()) {
		case R.id.action_about:
			showDialogFragment(AboutDialogFragment.newInstance(this), null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		showAppList();
		checkAndInit();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		showAppList();
		checkAndInit();
	}


	/**
	 * Check play-service and do init-pages.
	 */
	private void checkAndInit() {
		checkPlayService();
		if (Prefs.getInstance().isEULAOnceConfirmed() && mViewPager == null) {
			initViewPager();
		}
	}


	/**
	 * To confirm whether the validation of the Play-service of Google Inc.
	 */
	private void checkPlayService() {
		final int isFound = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isFound == ConnectionResult.SUCCESS) {//Ignore update.
			//The "End User License Agreement" must be confirmed before you use this application.
			if (!Prefs.getInstance().isEULAOnceConfirmed()) {
				showDialogFragment(new EulaConfirmationDialog(), null);
			}
		} else {
			new Builder(this).setTitle(R.string.application_name).setMessage(R.string.lbl_play_service).setCancelable(
					false).setPositiveButton(R.string.lbl_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(getString(R.string.play_service_url)));
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e0) {
						intent.setData(Uri.parse(getString(R.string.play_service_web)));
						try {
							startActivity(intent);
						} catch (Exception e1) {
							//Ignore now.
						}
					} finally {
						finish();
					}
				}
			}).create().show();
		}
	}


	/**
	 * Show all external applications links.
	 */
	private void showAppList() {
		getSupportFragmentManager().beginTransaction().replace(R.id.app_list_fl, AppListImpFragment.newInstance(this))
				.commit();
	}


	/**
	 * Initialize the navigation drawer.
	 */
	private void initDrawer() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.application_name,
					R.string.app_name);
			mDrawerLayout.setDrawerListener(mDrawerToggle);


		}
	}

	/**
	 * Make the main screen, pages, friends-list etc.
	 */
	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.vp);
		mViewPager.setOffscreenPageLimit(3);
		mPagerAdapter = new NewsListPagersAdapter(MainActivity.this, getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		// Bind the tabs to the ViewPager
		mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mTabs.setViewPager(mViewPager);
		mTabs.setIndicatorColorResource(R.color.common_white);
		mTabs.setVisibility(View.VISIBLE);
	}


	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link
	 * 		android.support.v4.app.DialogFragment} can been seen.
	 */
	protected void showDialogFragment(DialogFragment dlgFrg, String tagName) {
		try {
			if (dlgFrg != null) {
				DialogFragment dialogFragment = dlgFrg;
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = getSupportFragmentManager().findFragmentByTag("dlg");
				if (prev != null) {
					ft.remove(prev);
				}
				try {
					if (TextUtils.isEmpty(tagName)) {
						dialogFragment.show(ft, "dlg");
					} else {
						dialogFragment.show(ft, tagName);
					}
				} catch (Exception _e) {
				}
			}
		} catch (Exception _e) {
		}
	}

}
