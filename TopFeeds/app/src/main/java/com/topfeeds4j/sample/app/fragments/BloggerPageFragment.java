package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.topfeeds4j.Api;
import com.topfeeds4j.ds.EntryMeta;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.utils.helpers.AbstractAdapterHelper;
import com.topfeeds4j.sample.utils.helpers.BloggerHelperFactory;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class BloggerPageFragment extends AbstractLinkedPagesFragment {

	private static final String EXTRAS_ID = BloggerPageFragment.class.getName() + ".EXTRAS.id";

	public static BloggerPageFragment newInstance(Context context, long bloggerId) {
		Bundle args = new Bundle();
		args.putLong(EXTRAS_ID, bloggerId);
		return (BloggerPageFragment) Fragment.instantiate(context, BloggerPageFragment.class.getName(), args);
	}

	/**
	 * Get host type ident, {@code 1} is CSDN, {@code 2} is techug.com, {@code 3} Geeker-news,{@code 4} androider-blog,
	 * {@code 5} blogger, otherwise is oschina.net
	 */
	protected int getNewsHostType() {
		return 5;
	}


	/**
	 * @return A list of {@link NewsEntry}s.
	 */
	@Override
	public void getNewsList() {
		if (!isInProgress()) {
			setInProgress(true);
			Api.getNewsEntries(getNewsHostType(), "0", new EntryMeta(getArguments().getLong(EXTRAS_ID) + ""), this);
		}
	}

	@Override
	protected AbstractAdapterHelper getAdapterHelper() {
		return BloggerHelperFactory.getInstance().getHelper(getArguments().getLong(EXTRAS_ID));
	}

	@Override
	protected EntryMeta getEntryMeta() {
		return new EntryMeta(getArguments().getLong(EXTRAS_ID) + "");
	}
}
