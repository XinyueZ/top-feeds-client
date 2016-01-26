package com.topfeeds4j.sample.app.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.topfeeds4j.Api;
import com.topfeeds4j.Api.TopFeeds;
import com.topfeeds4j.ds.EntryMeta;
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.events.LoadMoreEvent;
import com.topfeeds4j.sample.app.events.LoadedBookmarkEvent;
import com.topfeeds4j.sample.app.events.ShowProgressIndicatorEvent;
import com.topfeeds4j.sample.utils.helpers.AbstractLinkedPagesAdapterHelper;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public abstract class AbstractLinkedPagesFragment extends TopFeedsFragment {


	private int mVisibleItemCount;
	private int mPastVisibleItems;
	private int mTotalItemCount;
	private boolean mLoading = true;


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------


	/**
	 * Handler for {@link LoadedBookmarkEvent}.
	 *
	 * @param e
	 * 		Event {@link LoadedBookmarkEvent}.
	 */
	public void onEvent( LoadedBookmarkEvent e ) {
		getNewsList();
	}


	//------------------------------------------------
	/**
	 * Initialize an {@link  AbstractLinkedPagesFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link AbstractLinkedPagesFragment}.
	 */
	public static AbstractLinkedPagesFragment newInstance( Context context ) {
		return (AbstractLinkedPagesFragment) Fragment.instantiate( context, AbstractLinkedPagesFragment.class.getName() );
	}


	@Override
	protected void pull2Load() {
		getAdapterHelper().resetPointer();
		super.pull2Load();
	}


	@Override
	public void onResponse( Response<NewsEntries> response ){
		super.onResponse(  response );
		if( response.isSuccess() && response.body().getStatus() == 200 ) { //Feeds with validated content, otherwise the status is 300 or other else.
			AbstractLinkedPagesAdapterHelper helper = (AbstractLinkedPagesAdapterHelper) getAdapterHelper();
			helper.setPrevious( helper.getFrom() );
			helper.setFrom(  response.body().getFrom() );
		}
	}


	/**
	 * Load more and more news
	 */
	private void getMoreNews() {
		if( !isLived() ) {
			return;
		}
		if( !isInProgress() ) {
			EventBus.getDefault().post( new ShowProgressIndicatorEvent( true ) );
			setInProgress( true );
			AbstractLinkedPagesAdapterHelper helper = (AbstractLinkedPagesAdapterHelper) getAdapterHelper();
			Call<NewsEntries>                newsEntriesCall = Api.Retrofit.create( TopFeeds.class )
																	   .getNewsEntries(
																			   getNewsHostType(),
																			   helper.getFrom(),
																			   getEntryMeta()
																	   );
			newsEntriesCall.enqueue( new Callback<NewsEntries>() {
				@Override
				public void onResponse( Response<NewsEntries> response ) {
					if(response.isSuccess()) {
						NewsEntries newsEntries = response.body();
						onFinishLoading();
						AbstractLinkedPagesAdapterHelper helper = (AbstractLinkedPagesAdapterHelper) getAdapterHelper();
						if( newsEntries.getStatus() == 200 ) {
							getAdapter().getData().addAll( newsEntries.getNewsEntries() );
							getAdapter().notifyDataSetChanged();
							helper.setPrevious( helper.getFrom() );
							helper.setFrom( newsEntries.getFrom() );
						} else {
							helper.setFrom( helper.getPrevious() );
						}
					} else {
						onFailure( null );
					}
				}

				@Override
				public void onFailure( Throwable t ) {
					onFinishLoading();
					AbstractLinkedPagesAdapterHelper helper = (AbstractLinkedPagesAdapterHelper) getAdapterHelper();
					helper.setFrom( helper.getPrevious() );

					new DialogFragment() {
						@Override
						public Dialog onCreateDialog( Bundle savedInstanceState ) {
							// Use the Builder class for convenient dialog construction
							AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
							builder.setMessage( R.string.lbl_retry ).setNegativeButton( R.string.lbl_no, null ).setPositiveButton(
									R.string.lbl_yes, new DialogInterface.OnClickListener() {
										public void onClick( DialogInterface dialog, int id ) {
											getMoreNews();
										}
									} );
							return builder.create();
						}
					}.show( getChildFragmentManager(), null );
				}
			} );
		}
	}

	@Override
	protected void onFinishLoading() {
		super.onFinishLoading();
		mLoading = true;
		EventBus.getDefault().post( new ShowProgressIndicatorEvent( false ) );
	}


	@Override
	public void onViewCreated( View view, Bundle savedInstanceState ) {
		super.onViewCreated( view, savedInstanceState );
		getRecyclerView().addOnScrollListener( new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {

				mVisibleItemCount = getLayoutManager().getChildCount();
				mTotalItemCount = getLayoutManager().getItemCount();
				mPastVisibleItems = getLayoutManager().findFirstVisibleItemPosition();

				if( mLoading ) {
					if( ( mVisibleItemCount + mPastVisibleItems ) >= mTotalItemCount ) {
						mLoading = false;
						EventBus.getDefault().post( new LoadMoreEvent() );
						//showLoadingIndicator();
						getMoreNews();
					}
				}
			}
		} );
	}


	protected EntryMeta getEntryMeta() {
		return new EntryMeta( "0" );
	}
}
