package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.chopping.utils.Utils;
import com.topfeeds4j.Api;
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.adapters.NewsListAdapter;
import com.topfeeds4j.sample.app.events.RefreshListEvent;
import com.topfeeds4j.sample.app.events.TopEvent;
import com.topfeeds4j.sample.utils.Prefs;
import com.topfeeds4j.sample.utils.helpers.AbstractAdapterHelper;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * System base {@link android.support.v4.app.Fragment}
 */
public abstract class TopFeedsFragment extends BaseFragment implements Callback<NewsEntries> {

	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_news_list;

	/**
	 * List container for showing all comments.
	 */
	private RecyclerView       mRv;
	/**
	 * Pull to load.
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;
	//Indicator for not load and error.
	private View               mNotLoadV;
	private View               mErrorV;
	private View               mEmptyV;

	private LinearLayoutManager mLayoutManager;
	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean             mInProgress;

	private boolean mLived;
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
		if( getAdapter() != null ) {
			getAdapter().notifyDataSetChanged();
		}
	}

	/**
	 * Handler for {@link com.topfeeds4j.sample.app.events.TopEvent}.
	 *
	 * @param e
	 * 		Event {@link com.topfeeds4j.sample.app.events.TopEvent}.
	 */
	public void onEvent( TopEvent e ) {
		NewsListAdapter adapter = getAdapter();
		if( getUserVisibleHint() && adapter != null && adapter.getItemCount() > 0 ) {
			mLayoutManager.scrollToPositionWithOffset(
					0,
					0
			);
			Utils.showShortToast(
					App.Instance,
					R.string.action_to_top
			);
		}
	}
	//------------------------------------------------


	/**
	 * Get host type ident, {@code 1} is CSDN, otherwise is oschina.net
	 */
	protected abstract int getNewsHostType();

	/**
	 * @return A list of {@link NewsEntry}s.
	 */
	public void getNewsList() {
		if( !isInProgress() ) {
			setInProgress( true );
			Api.getNewsEntries(
					getNewsHostType(),
					0,
					this
			);
		}
	}

	/**
	 * Doing pull-to-load.
	 */
	protected void pull2Load() {
		getNewsList();
	}

	@Override
	public void success( NewsEntries newsEntries, Response response ) {
		if( !isLived() ) {
			return;
		}
		onFinishLoading();
		NewsListAdapter adp = getAdapter();
		if( newsEntries.getStatus() == 200 ) {
			if( newsEntries.getNewsEntries() != null && newsEntries.getNewsEntries()
																   .size() > 0 ) {
				adp.setData( newsEntries.getNewsEntries() );
				adp.notifyDataSetChanged();
			} else {
				if( adp.getData() == null || adp.getData()
												.size() == 0 ) {
					mEmptyV.setVisibility( View.VISIBLE );
				}
			}
		} else {
			if( adp != null && adp.getData() != null && adp.getData()
														   .size() > 0 ) {
				return;
			}
			mErrorV.setVisibility( View.VISIBLE );
		}
	}


	@Override
	public void failure( RetrofitError error ) {
		onFinishLoading();
		mErrorV.setVisibility( View.VISIBLE );
	}

	/**
	 * Views changing after finishing loading feeds.
	 */
	protected void onFinishLoading() {
		setInProgress( false );
		mNotLoadV.setVisibility( View.GONE );
		mErrorV.setVisibility( View.GONE );
		mEmptyV.setVisibility( View.GONE );
		mSwipeRefreshLayout.setRefreshing( false );
	}

	protected NewsListAdapter getAdapter() {
		return getAdapterHelper().getNewsListAdapter();
	}


	protected abstract AbstractAdapterHelper getAdapterHelper();

	protected LinearLayoutManager getLayoutManager() {
		return mLayoutManager;
	}

	protected RecyclerView getRecyclerView() {
		return mRv;
	}

	protected boolean isInProgress() {
		return mInProgress;
	}

	protected void setInProgress( boolean inProgress ) {
		mInProgress = inProgress;
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	protected View getEmptyView() {
		return mEmptyV;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		return inflater.inflate(
				LAYOUT,
				container,
				false
		);
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState ) {
		super.onViewCreated(
				view,
				savedInstanceState
		);
		setLived( true );

		ScreenSize screenSize   = DeviceUtils.getScreenSize( App.Instance );
		int        defaultWidth = getDefaultWidth( App.Instance );
		int        div          = (int) Math.floor( screenSize.Width / defaultWidth );

		if( div > 3 ) {
			div = 3;
		}
		mRv = (RecyclerView) view.findViewById( R.id.news_list_rv );
		mRv.setLayoutManager( mLayoutManager = new GridLayoutManager(
				getActivity(),
				div
		) );
		mRv.setHasFixedSize( false );

		mRv.setAdapter( getAdapter() );

		mNotLoadV = view.findViewById( R.id.not_loaded_pb );
		mErrorV = view.findViewById( R.id.error_v );
		mEmptyV = view.findViewById( R.id.empty_iv );

		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById( R.id.content_srl );
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.color_pocket_1,
				R.color.color_pocket_2,
				R.color.color_pocket_3,
				R.color.color_pocket_4
		);
		mSwipeRefreshLayout.setOnRefreshListener( new OnRefreshListener() {
			@Override
			public void onRefresh() {
				pull2Load();
			}
		} );

	}

	@Override
	public void onDestroyView() {
		setLived( false );
		setInProgress( false );
		super.onDestroyView();
	}

	protected int getDefaultWidth( Context cxt ) {
		return cxt.getResources()
				  .getDimensionPixelSize( R.dimen.card_width );
	}


	protected boolean isLived() {
		return mLived;
	}

	private void setLived( boolean lived ) {
		mLived = lived;
	}
}
