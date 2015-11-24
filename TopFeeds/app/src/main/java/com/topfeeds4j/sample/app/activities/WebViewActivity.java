package com.topfeeds4j.sample.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chopping.utils.Utils;
import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.utils.Prefs;

import retrofit.Callback;
import retrofit.RetrofitError;

import static android.R.id.home;


public final class WebViewActivity extends AppCompatActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_webview;
	/**
	 * The menu to this view.
	 */
	private static final int MENU = R.menu.webview;
	/**
	 * Extras for webview's url.
	 */
	private static final String EXTRAS_URL = WebViewActivity.class.getName() + ".EXTRAS.url";
	/**
	 * Extras for webview's title
	 */
	private static final String EXTRAS_TITLE = WebViewActivity.class.getName() + ".EXTRAS.title";
	/**
	 * Extras for entry itself
	 */
	private static final String EXTRAS_ENTRY = WebViewActivity.class.getName() + ".EXTRAS.entry";
	/**
	 * WebView .
	 */
	private WebView mWebView;
	/**
	 * Loading indicator.
	 */
	private View mLoadingPbV;

	/**
	 * Show single instance of {@link WebViewActivity}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 * @param title
	 * 		Title shows on actionbar.
	 * @param url
	 * 		Url to show.
	 * 	@param entry
	 * 		The news-entry.
	 */
	public static void showInstance(Activity cxt, String title, String url, NewsEntry entry) {
		Intent intent = new Intent(cxt, WebViewActivity.class);
		intent.putExtra(EXTRAS_URL, url);
		intent.putExtra(EXTRAS_TITLE, title);
		intent.putExtra(EXTRAS_ENTRY, entry);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);

		//Actionbar and navi-drawer.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDefaultDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getIntent().getStringExtra(EXTRAS_TITLE));

		mLoadingPbV = findViewById(R.id.loading_pb);

		mWebView = (WebView) findViewById(R.id.webView);
		WebSettings settings = mWebView.getSettings();
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setCacheMode(WebSettings.LOAD_NORMAL);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(false);
		settings.setDomStorageEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mLoadingPbV.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mLoadingPbV.setVisibility(View.GONE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(getIntent());
	}

	/**
	 * Handle intent to show web.
	 *
	 * @param intent
	 * 		Data for the activity.
	 */
	private void handleIntent(Intent intent) {
		String url = intent.getStringExtra(EXTRAS_URL);
		if (!TextUtils.isEmpty(url)) {
			mWebView.loadUrl(url);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(MENU, menu);
		final MenuItem menuFB = menu.findItem(R.id.action_fb);
		menuFB.setVisible(false);
		final MenuItem menuShare = menu.findItem(R.id.action_item_share);
		menuShare.setVisible(false);
		final android.support.v7.widget.ShareActionProvider provider =
				(android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);

		final String url = getIntent().getStringExtra(EXTRAS_URL);
		Api.getTinyUrl(url, new Callback<Response>() {
			@Override
			public void success(Response response, retrofit.client.Response response2) {
				Intent intent = getIntent();
				String subject = App.Instance.getString(R.string.lbl_share_item_title);
				String text = App.Instance.getString(R.string.lbl_share_item_content, getIntent().getStringExtra(
						EXTRAS_TITLE),
						TextUtils.isEmpty(response.getResult()) ? intent.getStringExtra(EXTRAS_URL) : response.getResult(),
						subject, Prefs.getInstance().getAppTinyuUrl());
				provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));
				menuFB.setVisible(true);
				menuShare.setVisible(true);
			}

			@Override
			public void failure(RetrofitError error) {
				Intent intent = getIntent();
				String subject = App.Instance.getString(R.string.lbl_share_item_title);
				String text = App.Instance.getString(R.string.lbl_share_item_content, intent.getStringExtra(
						EXTRAS_TITLE), intent.getStringExtra(EXTRAS_URL), subject, Prefs.getInstance().getAppTinyuUrl());
				provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));
				menuFB.setVisible(true);
				menuShare.setVisible(true);
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case home:
			ActivityCompat.finishAfterTransition(this);
			break;
		case R.id.action_forward:
			if (mWebView.canGoForward()) {
				mWebView.goForward();
			}
			break;
		case R.id.action_backward:
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			}
			break;
		case R.id.action_fb:
			NewsEntry entry = (NewsEntry) getIntent().getSerializableExtra(EXTRAS_ENTRY);
			com.topfeeds4j.sample.utils.Utils.facebookShare(this , entry);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
