package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.topfeeds4j.sample.app.events.LoadedBookmarkEvent;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class TechugNewsListPageFragment extends TopFeedsFragment {


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
	 * Initialize an {@link  TechugNewsListPageFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link TechugNewsListPageFragment}.
	 */
	public static TechugNewsListPageFragment newInstance(Context context) {
		return (TechugNewsListPageFragment) Fragment.instantiate(context, TechugNewsListPageFragment.class.getName());
	}



	/**
	 * Get host type ident, {@code 1} is CSDN, {@code 2} is techug.com,{@code 3} is Geeker-news, otherwise is oschina.net
	 */
	protected int getNewsHostType() {
		return 2;
	}


}
