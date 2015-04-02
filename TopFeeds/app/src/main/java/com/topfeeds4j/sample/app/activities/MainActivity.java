package com.topfeeds4j.sample.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.adapters.NewsListPagersAdapter;
import com.topfeeds4j.sample.app.events.OpenLinkEvent;

import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {
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
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

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

	//------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Actionbar and navi-drawer.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mViewPager = (ViewPager) findViewById(R.id.vp);
		mViewPager.setOffscreenPageLimit(3);
		mPagerAdapter = new NewsListPagersAdapter(MainActivity.this, getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		// Bind the tabs to the ViewPager
		mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mTabs.setViewPager(mViewPager);
		mTabs.setIndicatorColorResource(R.color.common_white);
	}

	@Override
	protected void onResume() {
		EventBus.getDefault().registerSticky(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
