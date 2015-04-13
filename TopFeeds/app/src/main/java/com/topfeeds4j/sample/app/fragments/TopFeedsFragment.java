package com.topfeeds4j.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.chopping.utils.Utils;
import com.topfeeds4j.Api;
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.adapters.NewsListAdapter;
import com.topfeeds4j.sample.app.events.RefreshListEvent;
import com.topfeeds4j.sample.utils.Prefs;

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
	 * Adapter for {@link #mRv} to show all comments.
	 */
	private NewsListAdapter mAdp;
	/**
	 * List container for showing all comments.
	 */
	private RecyclerView mRv;
	/**
	 * Pull to load.
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;
	//Indicator for not load and error.
	private View mNotLoadV;
	private View mErrorV;
	private View mEmptyV;

	private LinearLayoutManager mLayoutManager;
	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean mInProgress;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link RefreshListEvent}.
	 *
	 * @param e
	 * 		Event {@link RefreshListEvent}.
	 */
	public void onEvent(RefreshListEvent e) {
		if (getAdapter() != null) {
			getAdapter().notifyDataSetChanged();
		}
	}
	//------------------------------------------------

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_to_top:
			if (mAdp != null && mAdp.getItemCount() > 0) {
				mLayoutManager.scrollToPositionWithOffset(0, 0);
				Utils.showShortToast(App.Instance, R.string.action_to_top);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);
		mRv = (RecyclerView) view.findViewById(R.id.news_list_rv);
		mRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new NewsListAdapter(null));

		mNotLoadV = view.findViewById(R.id.not_loaded_pb);
		mErrorV = view.findViewById(R.id.error_iv);
		mEmptyV = view.findViewById(R.id.empty_iv);

		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2,
				R.color.color_pocket_3, R.color.color_pocket_4);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				pull2Load();
			}
		});

	}


	/**
	 * Get host type ident, {@code 1} is CSDN, otherwise is oschina.net
	 */
	protected abstract int getNewsHostType();

	/**
	 * @return A list of {@link NewsEntry}s.
	 */
	protected void getNewsList() {
		if (!isInProgress()) {
			setInProgress(true);
			Api.getNewsEntries(getNewsHostType(), 0, this);
		}
	}

	/**
	 * Doing pull-to-load.
	 */
	protected void pull2Load() {
		getNewsList();
	}

	@Override
	public void success(NewsEntries newsEntries, Response response) {
		onFinishLoading();
		if (newsEntries.getStatus() == 200) {
			if (newsEntries.getNewsEntries() != null && newsEntries.getNewsEntries().size() > 0) {
				mAdp.setData(newsEntries.getNewsEntries());
				mAdp.notifyDataSetChanged();
			} else {
				if (mAdp.getData() == null || mAdp.getData().size() == 0) {
					mEmptyV.setVisibility(View.VISIBLE);
				}
			}
		} else {
			if (mAdp != null && mAdp.getData() != null && mAdp.getData().size() > 0) {
				return;
			}
			mErrorV.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public void failure(RetrofitError error) {
		onFinishLoading();
		mErrorV.setVisibility(View.VISIBLE);
	}

	/**
	 * Views changing after finishing loading feeds.
	 */
	protected void onFinishLoading() {
		setInProgress(false);
		mNotLoadV.setVisibility(View.GONE);
		mErrorV.setVisibility(View.GONE);
		mEmptyV.setVisibility(View.GONE);
		mSwipeRefreshLayout.setRefreshing(false);
	}

	protected NewsListAdapter getAdapter() {
		return mAdp;
	}

	protected LinearLayoutManager getLayoutManager() {
		return mLayoutManager;
	}

	protected RecyclerView getRecyclerView() {
		return mRv;
	}

	protected void setInProgress(boolean inProgress) {
		mInProgress = inProgress;
	}

	protected boolean isInProgress() {
		return mInProgress;
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	protected View getEmptyView() {
		return mEmptyV;
	}
}
