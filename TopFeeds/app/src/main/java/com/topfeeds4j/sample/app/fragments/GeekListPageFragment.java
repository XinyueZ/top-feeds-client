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
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.events.LoadMoreEvent;
import com.topfeeds4j.sample.app.events.LoadedBookmarkEvent;
import com.topfeeds4j.sample.app.events.ShowProgressIndicatorEvent;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * The list of news.
 *
 * @author Xinyue Zhao
 */
public final class GeekListPageFragment extends TopFeedsFragment {

	/**
	 * First page for Geeker-news.
	 */
	private String mFrom = DEFAULT_FROM;

	private static final String DEFAULT_FROM = "";
	/**
	 * Previous page's start point.
	 */
	private String mPrevious = DEFAULT_FROM;

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
	public void onEvent(LoadedBookmarkEvent e) {
		getNewsList();
	}



	//------------------------------------------------
	/**
	 * Initialize an {@link  GeekListPageFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link GeekListPageFragment}.
	 */
	public static GeekListPageFragment newInstance(Context context) {
		return (GeekListPageFragment) Fragment.instantiate(context, GeekListPageFragment.class.getName());
	}

	/**
	 * Get host type ident, {@code 1} is CSDN, {@code 2} is techug.com, {@code 3} is Geeker-news, otherwise is oschina.net
	 */
	protected int getNewsHostType() {
		return 3;
	}

	@Override
	protected void pull2Load() {
		resetPointers();
		super.pull2Load();
	}

	private void resetPointers() {
		mFrom = DEFAULT_FROM;
		mPrevious = DEFAULT_FROM;
	}

	@Override
	public void success(NewsEntries newsEntries, Response response) {
		super.success(newsEntries, response);
		if (newsEntries.getStatus() == 200) { //Feeds with validated content, otherwise the status is 300 or other else.
			mPrevious = mFrom;
			mFrom = newsEntries.getFrom();
		}
	}


	/**
	 * Load more and more news
	 */
	private void getMoreNews() {
		if (!isInProgress()) {
			EventBus.getDefault().post(new ShowProgressIndicatorEvent(true));
			setInProgress(true);
			Api.getNewsEntries(getNewsHostType(), mFrom, new Callback<NewsEntries>() {
				@Override
				public void success(NewsEntries newsEntries, Response response) {
					onFinishLoading();
					if (newsEntries.getStatus() == 200) {
						getAdapter().getData().addAll(newsEntries.getNewsEntries());
						getAdapter().notifyDataSetChanged();
						mPrevious = mFrom;
						mFrom = newsEntries.getFrom();
					} else {
						mFrom = mPrevious;
					}
				}

				@Override
				public void failure(RetrofitError error) {
					onFinishLoading();
					mFrom = mPrevious;

					new DialogFragment() {
						@Override
						public Dialog onCreateDialog(Bundle savedInstanceState) {
							// Use the Builder class for convenient dialog construction
							AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
							builder.setMessage(R.string.lbl_retry).setNegativeButton(R.string.lbl_no, null)
									.setPositiveButton(R.string.lbl_yes, new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int id) {
													getMoreNews();
												}
											});
							return builder.create();
						}
					}.show(getChildFragmentManager(), null);
				}
			});
		}
	}

	@Override
	protected void onFinishLoading() {
		super.onFinishLoading();
		mLoading = true;
		EventBus.getDefault().post(new ShowProgressIndicatorEvent(false));
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

				mVisibleItemCount = getLayoutManager().getChildCount();
				mTotalItemCount = getLayoutManager().getItemCount();
				mPastVisibleItems = getLayoutManager().findFirstVisibleItemPosition();

				if (mLoading) {
					if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
						mLoading = false;
						EventBus.getDefault().post(new LoadMoreEvent());
						//showLoadingIndicator();
						getMoreNews();
					}
				}
			}
		});
	}
}
