package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.events.LoadedBookmarkEvent;
import com.topfeeds4j.sample.app.events.RefreshListEvent;
import com.topfeeds4j.sample.utils.Utils;
import com.topfeeds4j.sample.utils.helpers.AbstractAdapterHelper;
import com.topfeeds4j.sample.utils.helpers.BookmarkListAdapterHelper;

import de.greenrobot.event.EventBus;
import retrofit2.Response;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class BookmarkListPageFragment extends TopFeedsFragment {


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link RefreshListEvent}.
	 *
	 * @param e
	 * 		Event {@link RefreshListEvent}.
	 */
	public void onEvent( RefreshListEvent e ) {
		if( App.Instance.getBookmarkList() == null || App.Instance.getBookmarkList()
																  .size() == 0 ) {
			getEmptyView().setVisibility( View.VISIBLE );
		} else {
			getAdapter().notifyItemRemoved( e.getPosition() );
		}
	}
	//------------------------------------------------

	/**
	 * Initialize an {@link  BookmarkListPageFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link BookmarkListPageFragment}.
	 */
	public static BookmarkListPageFragment newInstance( Context context ) {
		return (BookmarkListPageFragment) Fragment.instantiate(
				context,
				BookmarkListPageFragment.class.getName()
		);
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState ) {
		super.onViewCreated(
				view,
				savedInstanceState
		);
		getNewsList();
	}


	@Override
	protected int getNewsHostType() {
		return 0;
	}

	/**
	 * @return A list of {@link NewsEntry}s.
	 */
	@Override
	public void getNewsList() {
		if( !isInProgress() ) {
			setInProgress( true );
			Utils.loadBookmarkList( this );
		}
	}


	@Override
	public void onResponse( Response<NewsEntries> response ) {
		if(response.isSuccess()) {
			if( response.body()  != null ) {
				App.Instance.setBookmarkList(  response.body().getNewsEntries() );
			}
			super.onResponse(
					response
			);
			EventBus.getDefault()
					.post( new LoadedBookmarkEvent() );
		} else {
			onFailure( null );
		}
	}

	@Override
	public void onFailure( Throwable t ){
		super.onFailure( t );
		EventBus.getDefault()
				.post( new LoadedBookmarkEvent() );
	}

	@Override
	protected AbstractAdapterHelper getAdapterHelper() {
		return BookmarkListAdapterHelper.getInstance();
	}
}
