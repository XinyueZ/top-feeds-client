package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class CsdnNewsListPageFragment extends TopFeedsFragment {


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
