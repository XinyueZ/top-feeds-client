package com.topfeeds4j.sample.app.fragments;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.events.LoadedBookmarkEvent;
import com.topfeeds4j.sample.app.events.RefreshListEvent;
import com.topfeeds4j.sample.utils.Prefs;
import com.topfeeds4j.sample.utils.helpers.AbstractAdapterHelper;
import com.topfeeds4j.sample.utils.helpers.BookmarkListAdapterHelper;

import de.greenrobot.event.EventBus;


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
			loadedFromFirebase();
		}
	}


	private void loadedFromFirebase() {
		Prefs    prefs    = Prefs.getInstance();
		Firebase firebase = new Firebase( prefs.getFirebaseUrl() + prefs.getDeviceIdent() );
		firebase.authWithCustomToken( prefs.getFirebaseAuth(), null );
		firebase.addListenerForSingleValueEvent( new ValueEventListener() {
			@Override
			public void onDataChange( DataSnapshot dataSnapshot ) {
				setInProgress( false );
				if( dataSnapshot != null ) {
					GenericTypeIndicator<List<NewsEntry>> t = new GenericTypeIndicator<List<NewsEntry>>() {
					};
					App.Instance.setBookmarkList( dataSnapshot.getValue( t ) );
					showEntryList( App.Instance.getBookmarkList() );
					EventBus.getDefault()
							.post( new LoadedBookmarkEvent() );
				}
			}

			@Override
			public void onCancelled( FirebaseError firebaseError ) {
				EventBus.getDefault()
						.post( new LoadedBookmarkEvent() );
				onFailure( null );
			}
		} );
	}




	@Override
	protected AbstractAdapterHelper getAdapterHelper() {
		return BookmarkListAdapterHelper.getInstance();
	}
}
