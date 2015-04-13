package com.topfeeds4j.sample.app.activities;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
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
import android.support.v7.widget.ShareActionProvider;
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
import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.github.johnpersano.supertoasts.SuperCardToast;
import com.github.johnpersano.supertoasts.SuperToast.Animations;
import com.github.johnpersano.supertoasts.SuperToast.Background;
import com.github.johnpersano.supertoasts.SuperToast.IconPosition;
import com.github.johnpersano.supertoasts.SuperToast.Type;
import com.github.johnpersano.supertoasts.util.Wrappers;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.adapters.NewsListPagersAdapter;
import com.topfeeds4j.sample.app.events.EULAConfirmedEvent;
import com.topfeeds4j.sample.app.events.EULARejectEvent;
import com.topfeeds4j.sample.app.events.LoadMoreEvent;
import com.topfeeds4j.sample.app.events.OpenLinkEvent;
import com.topfeeds4j.sample.app.events.ShareEntryEvent;
import com.topfeeds4j.sample.app.events.ShareEvent;
import com.topfeeds4j.sample.app.events.ShowProgressIndicatorEvent;
import com.topfeeds4j.sample.app.events.ShowToastEvent;
import com.topfeeds4j.sample.app.fragments.AboutDialogFragment;
import com.topfeeds4j.sample.app.fragments.AboutDialogFragment.EulaConfirmationDialog;
import com.topfeeds4j.sample.app.fragments.AppListImpFragment;
import com.topfeeds4j.sample.utils.Prefs;

import retrofit.Callback;
import retrofit.RetrofitError;

import static com.github.johnpersano.supertoasts.SuperToast.Icon.Dark.INFO;


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
	 * The interstitial ad.
	 */
	private InterstitialAd mInterstitialAd;

	/**
	 * Navigation drawer.
	 */
	private DrawerLayout mDrawerLayout;
	/**
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	/**
	 * Indicator when loading application config.
	 */
	private ProgressDialog mPbDlg;

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

	/**
	 * Handler for {@link com.topfeeds4j.sample.app.events.ShareEvent}.
	 *
	 * @param e
	 * 		Event {@link com.topfeeds4j.sample.app.events.ShareEvent}.
	 */
	public void onEvent(final ShareEvent e) {
//		Intent sendIntent = new Intent();
//		sendIntent.setAction(Intent.ACTION_SEND);
//		sendIntent.putExtra(Intent.EXTRA_SUBJECT, e.getSubject());
//		sendIntent.putExtra(Intent.EXTRA_TEXT, e.getContent());
//		sendIntent.setType("text/plain");
//		startActivity(sendIntent);
		startActivity(e.getIntent());
	}

	WebDialog fbDlg;

	/**
	 * Handler for {@link  ShareEntryEvent}.
	 *
	 * @param e
	 * 		Event {@link  ShareEntryEvent}.
	 */
	public void onEvent(ShareEntryEvent e) {
		NewsEntry msg = e.getEntry();
		switch (e.getType()) {
		case Facebook:
			Bundle postParams = new Bundle();
			String desc = !TextUtils.isEmpty(msg.getDesc()) ? msg.getDesc() : null;
			if (desc == null) {
				fbDlg = new WebDialog.FeedDialogBuilder(this, getString(R.string.applicationId), postParams).setName(
						msg.getTitle()).setLink(msg.getUrlMobile()).build();
			} else {
				fbDlg = new WebDialog.FeedDialogBuilder(this, getString(R.string.applicationId), postParams).setName(
						msg.getTitle()).setDescription(desc).setLink(msg.getUrlMobile()).build();
			}
			fbDlg.setOnCompleteListener(new OnCompleteListener() {
				@Override
				public void onComplete(Bundle bundle, FacebookException e) {
					fbDlg.dismiss();
				}
			});
			fbDlg.show();
			break;
		case Tweet:
			break;
		}
	}


	/**
	 * Handler for {@link  ShowToastEvent}.
	 *
	 * @param e
	 * 		Event {@link  ShowToastEvent}.
	 */
	public void onEventMainThread(ShowToastEvent e) {
		switch (e.getType()) {
		case WARNING:
			showWarningToast(e.getText());
			break;
		case INFO:
			showInfoToast(e.getText());
			break;
		case ERROR:
			showErrorToast(e.getText());
			break;
		}
	}

	//------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Wrappers wrappers = new Wrappers();
		//		wrappers.add(onClickWrapper);
		//		wrappers.add(onDismissWrapper);
		SuperCardToast.onRestoreState(savedInstanceState, this, wrappers);

		//Actionbar and navi-drawer.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


		//Navi-drawer.
		initDrawer();

		mPbDlg = ProgressDialog.show(this, null, getString(R.string.msg_load_config));
		mPbDlg.setCancelable(false);

		makeAds();
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		SuperCardToast.onSaveState(outState);

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
		MenuItem  menuAppShare = menu.findItem(R.id.action_share_app);
		ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuAppShare);
		//Share application.
		String subject = String.format(getString(R.string.lbl_share_app_title), getString(R.string.application_name));
		String text = getString(R.string.lbl_share_app_content, getString(R.string.application_name),
				Prefs.getInstance().getAppTinyuUrl());
		provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
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
		doAppConfig();

		Prefs prefs = Prefs.getInstance();
		com.topfeeds4j.Api.initialize(App.Instance, prefs.getTopFeeds4JHost(), prefs.getCacheSize());
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		doAppConfig();

		Prefs prefs = Prefs.getInstance();
		com.topfeeds4j.Api.initialize(App.Instance, prefs.getTopFeeds4JHost(), prefs.getCacheSize());
	}

	private void showWarningToast(String text) {
		SuperCardToast toast = new SuperCardToast(this, Type.STANDARD);
		toast.setAnimations(Animations.POPUP);
		toast.setBackground(Background.BLUE);
		toast.setText(text);
		toast.setTextColor(getResources().getColor(R.color.common_white));
		toast.setIcon(INFO, IconPosition.LEFT);
		toast.show();
	}

	private void showErrorToast(String text) {
		SuperCardToast toast = new SuperCardToast(this, Type.STANDARD);
		toast.setAnimations(Animations.FADE);
		toast.setBackground(Background.RED);
		toast.setText(text);
		toast.setTextColor(getResources().getColor(R.color.common_white));
		toast.setIcon(INFO, IconPosition.LEFT);
		toast.show();
	}

	private void showInfoToast(String text) {
		SuperCardToast toast = new SuperCardToast(this, Type.STANDARD);
		toast.setAnimations(Animations.FLYIN);
		toast.setBackground(Background.GREEN);
		toast.setText(text);
		toast.setTextColor(getResources().getColor(R.color.common_white));
		toast.setIcon(INFO, IconPosition.LEFT);
		toast.show();
	}


	/**
	 * Work with application's configuration.
	 */
	private void doAppConfig() {
		String url = Prefs.getInstance().getAppTinyuUrl();
		if (TextUtils.isEmpty(url) || !url.contains("tinyurl")) {
			Api.getTinyUrl(getString(R.string.lbl_store_url, getPackageName()), new Callback<Response>() {
				@Override
				public void success(Response response, retrofit.client.Response response2) {
					Prefs.getInstance().setAppTinyUrl(response.getResult());
					showAll();
				}

				@Override
				public void failure(RetrofitError error) {
					Prefs.getInstance().setAppTinyUrl(getString(R.string.lbl_store_url, getPackageName()));
					showAll();
				}
			});
		} else {
			showAll();
		}
	}

	/**
	 * Show all list.
	 */
	private void showAll() {
		showAppList();
		checkAndInit();
		if (mPbDlg != null && mPbDlg.isShowing()) {
			mPbDlg.dismiss();
		}
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
	private void showDialogFragment(DialogFragment dlgFrg, String tagName) {
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


	/**
	 * Make an Admob.
	 */
	private void makeAds() {
		Prefs prefs = Prefs.getInstance();
		int curTime = prefs.getShownDetailsTimes();
		int adsTimes = prefs.getShownDetailsAdsTimes();
		if (curTime % adsTimes == 0) {
			// Create an ad.
			mInterstitialAd = new InterstitialAd(this);
			mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
			// Create ad request.
			AdRequest adRequest = new AdRequest.Builder().build();
			// Begin loading your interstitial.
			mInterstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {
					super.onAdLoaded();
					displayInterstitial();
				}
			});
			mInterstitialAd.loadAd(adRequest);
		}
		curTime++;
		prefs.setShownDetailsTimes(curTime);
	}

	/**
	 * Invoke displayInterstitial() when you are ready to display an interstitial.
	 */
	private void displayInterstitial() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
	}


}
