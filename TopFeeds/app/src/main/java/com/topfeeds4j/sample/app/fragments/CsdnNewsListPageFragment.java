package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
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

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class CsdnNewsListPageFragment extends Fragment implements Callback<NewsEntries> {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_news_list;
	/**
	 * Page data.
	 */
	private static final String EXTRAS_NEWS_LIST = CsdnNewsListPageFragment.class.getName() + ".EXTRAS.newsList";
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


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRv = (RecyclerView) view.findViewById(R.id.news_list_rv);
		mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
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
				getNewsList();
			}
		});

		getNewsList();
	}


	/**
	 * @return A list of {@link NewsEntry}s
	 */
	private void getNewsList() {
		Api.getNewsEntries(1, 0, this);
	}

	@Override
	public void success(NewsEntries newsEntries, Response response) {
		mAdp.setData(newsEntries.getNewsEntries());
		mAdp.notifyDataSetChanged();
		finishLoading();
	}


	@Override
	public void failure(RetrofitError error) {
		finishLoading();
		mErrorV.setVisibility(View.VISIBLE);
	}

	private void finishLoading() {
		mNotLoadV.setVisibility(View.GONE);
		mErrorV.setVisibility(View.GONE);
		mSwipeRefreshLayout.setRefreshing(false);
	}
}
