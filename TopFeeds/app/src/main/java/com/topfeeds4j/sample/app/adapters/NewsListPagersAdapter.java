package com.topfeeds4j.sample.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.utils.Utils;

/**
 * Pages's adapter.
 *
 * @author Xinyue Zhao
 */
public final class NewsListPagersAdapter extends FragmentStatePagerAdapter {
	private Context mContext;
	private String[] mTitles;

	public NewsListPagersAdapter(Context cxt, FragmentManager fm) {
		super(fm);
		mContext = cxt;
		mTitles = cxt.getResources().getStringArray(R.array.providers_list);
	}

	@Override
	public Fragment getItem(int position) {
		return  Utils.getFragment(mContext, position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return  mTitles[position];
	}

	@Override
	public int getCount() {
		return mTitles.length;
	}
}
