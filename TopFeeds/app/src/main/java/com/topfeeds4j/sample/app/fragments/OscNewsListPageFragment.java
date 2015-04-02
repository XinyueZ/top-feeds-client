package com.topfeeds4j.sample.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.adapters.NewsListAdapter;

/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class OscNewsListPageFragment extends Fragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_news_list;
	/**
	 * Page data.
	 */
	private static final String EXTRAS_NEWS_LIST = OscNewsListPageFragment.class.getName() + ".EXTRAS.newsList";
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
	 * Initialize an {@link  OscNewsListPageFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link OscNewsListPageFragment}.
	 */
	public static OscNewsListPageFragment newInstance(Context context   ) {
		return (OscNewsListPageFragment) Fragment.instantiate(context, OscNewsListPageFragment.class.getName());
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
	}
}
