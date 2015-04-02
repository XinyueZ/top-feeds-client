package com.topfeeds4j.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.topfeeds4j.Api;
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.adapters.NewsListAdapter;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * System base {@link android.support.v4.app.Fragment}
 */
public abstract class TopFeedsFragment extends Fragment implements Callback<NewsEntries> {

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

	private LinearLayoutManager mLayoutManager;
	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean mInProgress;

	/**
	 * Handler for {@link}.
	 *
	 * @param e
	 * 		Event {@link}.
	 */
	public void onEvent(Object e) {

	}


	@Override
	public void onResume() {

		EventBus.getDefault().registerSticky(this);
		super.onResume();
	}

	@Override
	public void onPause() {

		EventBus.getDefault().unregister(this);
		super.onPause();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRv = (RecyclerView) view.findViewById(R.id.news_list_rv);
		mRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new NewsListAdapter(null));

		mNotLoadV = view.findViewById(R.id.not_loaded_pb);
		mErrorV = view.findViewById(R.id.error_iv);

		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2,
				R.color.color_pocket_3, R.color.color_pocket_4);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				pull2Load();
			}
		});

		getNewsList();
	}


	/**
	 * Get host type ident, {@code 1} is CSDN, otherwise is oschina.net
	 */
	protected abstract int getNewsHostType();

	/**
	 * @return A list of {@link NewsEntry}s.
	 */
	protected void getNewsList() {
		if(!isInProgress()) {
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
			mAdp.setData(newsEntries.getNewsEntries());
			mAdp.notifyDataSetChanged();
		} else {
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
}