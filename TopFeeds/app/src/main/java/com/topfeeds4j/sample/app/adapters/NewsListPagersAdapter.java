package com.topfeeds4j.sample.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.fragments.BookmarkListPageFragment;
import com.topfeeds4j.sample.app.fragments.CsdnNewsListPageFragment;
import com.topfeeds4j.sample.app.fragments.GeekListPageFragment;
import com.topfeeds4j.sample.app.fragments.OscNewsListPageFragment;
import com.topfeeds4j.sample.app.fragments.TechugNewsListPageFragment;

/**
 * Pages's adapter.
 *
 * @author Xinyue Zhao
 */
public final class NewsListPagersAdapter extends FragmentStatePagerAdapter {
	private final int[] TITLES = { R.string.lbl_geek, R.string.lbl_techug, R.string.lbl_csdn, R.string.lbl_osc,R.string.lbl_bookmark_list, };
	private Context mContext;

	public NewsListPagersAdapter(Context cxt, FragmentManager fm) {
		super(fm);
		mContext = cxt;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 4:
			return BookmarkListPageFragment.newInstance(mContext);
		case 3:
			return OscNewsListPageFragment.newInstance(mContext);
		case 2:
			return CsdnNewsListPageFragment.newInstance(mContext);
		case 1:
			return TechugNewsListPageFragment.newInstance(mContext);
		case 0:
			return GeekListPageFragment.newInstance(mContext);
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
