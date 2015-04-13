package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.topfeeds4j.sample.app.events.LoadedBookmarkEvent;
import com.topfeeds4j.sample.app.events.RefreshListEvent;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class CsdnNewsListPageFragment extends TopFeedsFragment {


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------



	/**
	 * Handler for {@link LoadedBookmarkEvent}.
	 *
	 * @param e
	 * 		Event {@link LoadedBookmarkEvent}.
	 */
	public void onEvent(LoadedBookmarkEvent e) {
		getNewsList();
	}


	//------------------------------------------------
	/**
	 * Initialize an {@link  CsdnNewsListPageFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link CsdnNewsListPageFragment}.
	 */
	public static CsdnNewsListPageFragment newInstance(Context context) {
		return (CsdnNewsListPageFragment) Fragment.instantiate(context, CsdnNewsListPageFragment.class.getName());
	}



	/**
	 * Get host type ident, {@code 1} is CSDN, otherwise is oschina.net
	 */
	protected int getNewsHostType() {
		return 1;
	}


}
