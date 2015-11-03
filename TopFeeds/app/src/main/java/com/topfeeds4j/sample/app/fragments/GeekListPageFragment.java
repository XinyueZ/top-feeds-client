package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.topfeeds4j.sample.utils.helpers.AbstractAdapterHelper;
import com.topfeeds4j.sample.utils.helpers.GeekListPagesAdapterHelper;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class GeekListPageFragment extends AbstractLinkedPagesFragment {



	/**
	 * Initialize an {@link  GeekListPageFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link GeekListPageFragment}.
	 */
	public static GeekListPageFragment newInstance(Context context) {
		return (GeekListPageFragment) Fragment.instantiate(context, GeekListPageFragment.class.getName());
	}

	/**
	 * Get host type ident, {@code 1} is CSDN, {@code 2} is techug.com, {@code 3} is Geeker-news, otherwise is oschina.net
	 */
	protected int getNewsHostType() {
		return 3;
	}


	@Override
	protected AbstractAdapterHelper getAdapterHelper() {
		return GeekListPagesAdapterHelper.getInstance();
	}
}
