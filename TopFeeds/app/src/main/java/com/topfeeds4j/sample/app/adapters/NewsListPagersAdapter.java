package com.topfeeds4j.sample.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.fragments.BloggerPageFragment;
import com.topfeeds4j.sample.utils.Prefs;
import com.topfeeds4j.sample.utils.Utils;

/**
 * Pages's adapter.
 *
 * @author Xinyue Zhao
 */
public final class NewsListPagersAdapter extends FragmentStatePagerAdapter {
	private Context  mContext;
	private String[] mTitles;
	private int      mDynamicTotal;

	public NewsListPagersAdapter( Context cxt, FragmentManager fm ) {
		super( fm );
		mContext = cxt;
		String[] statics  = cxt.getResources().getStringArray( R.array.providers_list );
		String[] dynamics = Prefs.getInstance().getBloggerNames();
		mDynamicTotal = dynamics.length;
		mTitles = new String[ mDynamicTotal + statics.length ];
		int i = 0;
		for( String s : dynamics ) {
			mTitles[ i++ ] = s;
		}
		for( String s : statics ) {
			mTitles[ i++ ] = s;
		}
	}

	@Override
	public Fragment getItem( int position ) {
		if( position < mDynamicTotal ) {
			long[] ids = Prefs.getInstance().getBloggerIds();
			return BloggerPageFragment.newInstance( mContext, ids[ position ] );
		} else {
			return Utils.getFragment( mContext, position - mDynamicTotal );
		}
	}

	@Override
	public CharSequence getPageTitle( int position ) {
		return mTitles[ position ];
	}

	@Override
	public int getCount() {
		return mTitles.length;
	}
}
