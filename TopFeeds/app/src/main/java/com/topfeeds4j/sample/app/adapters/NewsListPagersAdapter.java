package com.topfeeds4j.sample.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.fragments.CsdnNewsListPageFragment;
import com.topfeeds4j.sample.app.fragments.OscNewsListPageFragment;

/**
 * Pages's adapter.
 *
 * @author Xinyue Zhao
 */
public final class NewsListPagersAdapter extends FragmentStatePagerAdapter {
	private final int[] TITLES = { R.string.lbl_osc, R.string.lbl_csdn, };
	private Context mContext;

	public NewsListPagersAdapter(Context cxt, FragmentManager fm) {
		super(fm);
		mContext = cxt;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return OscNewsListPageFragment.newInstance(mContext);
		case 1:
			return CsdnNewsListPageFragment.newInstance(mContext);
		}
		return null;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getString(TITLES[position]);
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}
}
