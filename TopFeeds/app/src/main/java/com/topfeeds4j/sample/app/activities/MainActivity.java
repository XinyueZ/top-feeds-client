package com.topfeeds4j.sample.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.chopping.bus.CloseDrawerEvent;
import com.chopping.utils.NetworkUtils;
import com.chopping.utils.Utils;
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
import com.topfeeds4j.ds.NewsEntries;
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
import com.topfeeds4j.sample.app.events.TopEvent;
import com.topfeeds4j.sample.app.fragments.AboutDialogFragment;
import com.topfeeds4j.sample.app.fragments.AboutDialogFragment.EulaConfirmationDialog;
import com.topfeeds4j.sample.app.fragments.BloggerPageFragment;
import com.topfeeds4j.sample.app.fragments.BookmarkListPageFragment;
import com.topfeeds4j.sample.app.fragments.TopFeedsFragment;
import com.topfeeds4j.sample.utils.Prefs;
import com.topfeeds4j.sample.utils.helpers.BloggerHelperFactory;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.johnpersano.supertoasts.SuperToast.Icon.Dark.INFO;


public class MainActivity extends BaseActivity {
	/**
	 * The pagers
	 */
	private ViewPager mViewPager;
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
	 * Indicator when loading application config.
	 */
	private ProgressDialog mPbDlg;
	private boolean mWifiOn;
	private Spinner mProviderSpr;

	private MenuItem mViewModeMi;
	private NavigationView mNavigationView;
	/**
	 * Container for all created "single-page"s.
	 */
	private SparseArrayCompat<Fragment> mSinglePages = new SparseArrayCompat<>();
	private boolean mAlive;
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
		WebViewActivity.showInstance(this, e.getTitle(), e.getUrl(), e.getNewsEntry());
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


	}

	/**
	 * Handler for {@link com.topfeeds4j.sample.app.events.ShareEvent}.
	 *
	 * @param e
	 * 		Event {@link com.topfeeds4j.sample.app.events.ShareEvent}.
	 */
	public void onEvent(final ShareEvent e) {
		startActivity(e.getIntent());
	}


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
			com.topfeeds4j.sample.utils.Utils.facebookShare(this, msg);
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

	/**
	 * Show single instance of {@link}
	 *
	 * @param cxt
	 * 		{@link Activity}.
	 */
	public static void showInstance(Activity cxt) {
		Intent intent = new Intent(cxt, MainActivity.class);
		intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, null);
	}

	/**
	 * Logical of "single-mode": page-selecting, page-loading, page-reusing etc.
	 */
	public void createSingleModeSelections() {
		//Init bookmark-list.
		if (App.Instance.getBookmarkList() == null) {
			com.topfeeds4j.sample.utils.Utils.loadBookmarkList(new Callback<NewsEntries>() {
				@Override
				public void success(NewsEntries newsEntries, retrofit.client.Response response) {
					if (newsEntries != null) {
						App.Instance.setBookmarkList(newsEntries.getNewsEntries());
					}
				}

				@Override
				public void failure(RetrofitError error) {

				}
			});
		}

		mProviderSpr.setVisibility(!mWifiOn ? View.VISIBLE : View.GONE);//When wifi is unavailable, user can use single page-mode.
		if (mWifiOn && Prefs.getInstance().getViewMode() == Prefs.VIEW_MODE_SINGLE) {
			//Under wifi, user can use different views.
			mProviderSpr.setVisibility(View.VISIBLE);
		}
		mViewModeMi.setVisible(mWifiOn);
		setViewModeMenuItem(mViewModeMi);

		if (mProviderSpr.getOnItemSelectedListener() == null) {
			String[] statics =  getResources().getStringArray(R.array.providers_list);
			String[] dynamics = Prefs.getInstance().getBloggerNames();
			String[] titles=new String[statics.length + dynamics.length];
			int i = 0;
			for(String s : dynamics) {
				titles[i++] = s;
			}
			for(String s : statics) {
				titles[i++] = s;
			}
			ArrayAdapter<String> adp = new ArrayAdapter<>(
					 App.Instance,  R.layout.spinner_item,
					titles);
			mProviderSpr.setAdapter(adp);
			mProviderSpr.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					selectSingleModePage(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});
		}
	}

	public void buildMenu() {
		if (mProviderSpr == null) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			toolbar.inflateMenu(R.menu.menu_main);
			toolbar.setNavigationIcon(R.drawable.ic_menu);
			toolbar.setNavigationOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDrawerLayout.openDrawer(GravityCompat.START);
				}
			});
			mProviderSpr = (Spinner)getLayoutInflater().inflate(R.layout.spinner_layout, toolbar, false);
			toolbar.addView(mProviderSpr);

			//Build menu.
			Menu menu = toolbar.getMenu();
			//Icon of "view change" menu, it works only when wifi is on.
			mViewModeMi = menu.findItem(R.id.action_view_mode);

			//Share this app by other applications.
			MenuItem menuAppShare = menu.findItem(R.id.action_share_app);
			ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuAppShare);
			String subject = String.format(getString(R.string.lbl_share_app_title), getString(
					R.string.application_name));
			String text = getString(R.string.lbl_share_app_content, getString(R.string.application_name),
					Prefs.getInstance().getAppTinyuUrl());
			provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));

			//All event-handlers on menu.
			toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
					case R.id.action_view_mode:
						handleViewModeChanging(item);
						break;
					case R.id.action_about:
						showDialogFragment(AboutDialogFragment.newInstance(MainActivity.this), null);
						break;
					}
					return true;
				}
			});
		}
	}

	/**
	 * Select a provider under "single-page" mode.
	 *
	 * @param position
	 */
	private void selectSingleModePage(int position) {
		FragmentManager frgMgr = getSupportFragmentManager();
		Fragment frg = mSinglePages.get(position);
		boolean newOne = false;
		if (frg == null) {
			//New page that ever been seen before.
			newOne = true;

			int dynamicTotal = Prefs.getInstance().getBloggerNames().length;
			if (position < dynamicTotal) {
				long[] ids = Prefs.getInstance().getBloggerIds();
				frg = BloggerPageFragment.newInstance(App.Instance,  ids[position]);
			} else {
				frg = com.topfeeds4j.sample.utils.Utils.getFragment(App.Instance,position - dynamicTotal );
			}
		}
		if (frg != null) {
			String tag = frg.getClass().getSimpleName();
			frgMgr.beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_right,
					R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.single_page_container, frg, tag)
					.commit();
			frgMgr.executePendingTransactions();
			if (newOne) {
				mSinglePages.put(position, frg);
			}
			TopFeedsFragment topFeedsFrg = (TopFeedsFragment) frg;
			if (!(topFeedsFrg instanceof BookmarkListPageFragment)) {
				//Except the bookmark-list all other pages should be loaded after be created.
				//Bookmark-list can load itself when created.
				topFeedsFrg.getNewsList();
			}
		}
	}

	private void setViewModeMenuItem(MenuItem mi) {
		int mode = Prefs.getInstance().getViewMode();
		mi.setIcon(mode == Prefs.VIEW_MODE_SINGLE ? R.drawable.ic_multi_mode : R.drawable.ic_single_mode);
	}

	private void handleViewModeChanging(MenuItem mi) {
		Prefs prefs = Prefs.getInstance();
		int mode = prefs.getViewMode();
		prefs.setViewMode(mode == Prefs.VIEW_MODE_MULTI ? Prefs.VIEW_MODE_SINGLE : Prefs.VIEW_MODE_MULTI);
		mode = Prefs.getInstance().getViewMode();
		mProviderSpr.setVisibility(mode == Prefs.VIEW_MODE_SINGLE ? View.VISIBLE : View.GONE);
		setViewModeMenuItem(mi);
		buildViews();
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		didAppConfig();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		didAppConfig();
	}

	private void showWarningToast(String text) {
		if(!mAlive) return;
		SuperCardToast toast = new SuperCardToast(this, Type.STANDARD);
		toast.setAnimations(Animations.POPUP);
		toast.setBackground(Background.BLUE);
		toast.setText(text);
		toast.setTextColor(getResources().getColor(R.color.common_white));
		toast.setIcon(INFO, IconPosition.LEFT);
		toast.show();
	}

	private void showErrorToast(String text) {
		if(!mAlive) return;
		SuperCardToast toast = new SuperCardToast(this, Type.STANDARD);
		toast.setAnimations(Animations.FADE);
		toast.setBackground(Background.RED);
		toast.setText(text);
		toast.setTextColor(getResources().getColor(R.color.common_white));
		toast.setIcon(INFO, IconPosition.LEFT);
		toast.show();
	}

	private void showInfoToast(String text) {
		if(!mAlive) return;
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
	private void didAppConfig() {
		Prefs prefs = Prefs.getInstance();
		BloggerHelperFactory.createInstance();
		com.topfeeds4j.Api.initialize(App.Instance, prefs.getTopFeeds4JHost(), prefs.getCacheSize());
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
		checkAndInit();
		if (mPbDlg != null && mPbDlg.isShowing()) {
			mPbDlg.dismiss();
			findViewById(R.id.coordinator_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.top_btn).setVisibility(View.VISIBLE);
		}
	}


	/**
	 * Check play-service and do init-pages.
	 */
	private void checkAndInit() {
		checkPlayService();
		buildViews();
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
	 * Make the main screen, pages, friends-list etc.
	 */
	private void buildViews() {
		if(mNavigationView == null) {
			mNavigationView = (NavigationView) findViewById(R.id.nav_view);
			mNavigationView.addHeaderView(getLayoutInflater().inflate(R.layout.nav_header, mNavigationView, false));
		}
		buildMenu();
		//Init pagers, bind the tabs to the ViewPager
		TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
		ViewGroup singleContainer = (ViewGroup) findViewById(R.id.single_page_container);
		if (mWifiOn) {
			int viewMode = Prefs.getInstance().getViewMode();
			switch (viewMode) {
			case Prefs.VIEW_MODE_MULTI:
				changeToMultiPagesMode(tabs, singleContainer);
				break;
			case Prefs.VIEW_MODE_SINGLE:
				changeToSinglePageMode(tabs, singleContainer);
				break;
			}
		} else {
			changeToSinglePageMode(tabs, singleContainer);
		}
	}

	private void changeToMultiPagesMode(TabLayout tabs, ViewGroup singleContainer) {
		mViewPager.setVisibility(View.VISIBLE);
		if (mPagerAdapter == null) {
			String[] statics =  getResources().getStringArray(R.array.providers_list);
			String[] dynamics = Prefs.getInstance().getBloggerNames();
			mViewPager.setOffscreenPageLimit(statics.length + dynamics.length);
			mPagerAdapter = new NewsListPagersAdapter(MainActivity.this, getSupportFragmentManager());
			mViewPager.setAdapter(mPagerAdapter);
			tabs.setupWithViewPager(mViewPager);
		}
		tabs.setVisibility(View.VISIBLE);
		singleContainer.setVisibility(View.GONE);
		mProviderSpr.setVisibility(View.GONE);
	}

	private void changeToSinglePageMode(TabLayout tabs, ViewGroup singleContainer) {
		mViewPager.setVisibility(View.GONE);
		tabs.setVisibility(View.GONE);
		singleContainer.setVisibility(View.VISIBLE);
		mProviderSpr.setVisibility(View.VISIBLE);
		//No wifi and force to use single mode.
		createSingleModeSelections();
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


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		SuperCardToast.onSaveState(outState);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mWifiOn = NetworkUtils.getCurrentNetworkType(App.Instance) == NetworkUtils.CONNECTION_WIFI;
		mViewPager = (ViewPager) findViewById(R.id.vp);

		final Wrappers wrappers = new Wrappers();
		//		wrappers.add(onClickWrapper);
		//		wrappers.add(onDismissWrapper);
		SuperCardToast.onRestoreState(savedInstanceState, this, wrappers);

		mPbDlg = ProgressDialog.show(this, null, getString(R.string.msg_load_config));
		mPbDlg.setCancelable(false);

		makeAds();

		findViewById(R.id.top_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new TopEvent());
			}
		});
		mAlive = true;
	}

	@Override
	protected void onDestroy() {
		mAlive = false;
		super.onDestroy();
		if (mSinglePages.size() > 0) {
			mSinglePages.clear();
		}
	}
}
