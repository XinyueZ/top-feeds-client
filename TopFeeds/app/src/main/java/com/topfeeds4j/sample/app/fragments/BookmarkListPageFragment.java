package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.topfeeds4j.Api;
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.events.LoadedBookmarkEvent;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class BookmarkListPageFragment extends TopFeedsFragment {


	/**
	 * Initialize an {@link  BookmarkListPageFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link BookmarkListPageFragment}.
	 */
	public static BookmarkListPageFragment newInstance(Context context) {
		return (BookmarkListPageFragment) Fragment.instantiate(context, BookmarkListPageFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getNewsList();
	}

	@Override
	protected boolean isRefreshBookmark() {
		return false;
	}

	@Override
	protected int getNewsHostType() {
		return 0;
	}

	/**
	 * @return A list of {@link NewsEntry}s.
	 */
	protected void getNewsList() {
		if (!isInProgress()) {
			setInProgress(true);
			Api.getBookmarkList(this);
		}
	}


	@Override
	public void success(NewsEntries newsEntries, Response response) {
		if (newsEntries != null) {
			App.Instance.setBookmarkList(newsEntries.getNewsEntries());
		}
		super.success(newsEntries, response);
		EventBus.getDefault().post(new LoadedBookmarkEvent());
	}

	@Override
	public void failure(RetrofitError error) {
		super.failure(error);
		EventBus.getDefault().post(new LoadedBookmarkEvent());
	}
}
