package com.topfeeds4j.sample.app.adapters;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.events.OpenLinkEvent;
import com.topfeeds4j.sample.app.events.ShareEvent;
import com.topfeeds4j.sample.utils.Prefs;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;


/**
 * The adapter for news-list.
 *
 * @author Xinyue Zhao
 */
public final class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_news_list;

	/**
	 * A menu on each line of list.
	 */
	private static final int MENU_LIST_ITEM = R.menu.menu_item;


	/**
	 * Data-source.
	 */
	private List<NewsEntry> mData;

	/**
	 * Constructor of {@link NewsListAdapter}.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public NewsListAdapter(List<NewsEntry> data) {
		setData(data);
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData(List<NewsEntry> data) {
		mData = data;
	}

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<NewsEntry> getData() {
		return mData;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		View convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT, parent, false);
		NewsListAdapter.ViewHolder viewHolder = new NewsListAdapter.ViewHolder(convertView);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final NewsEntry entry = mData.get(position);
		holder.mTitleTv.setText(entry.getTitle());
		if (TextUtils.isEmpty(entry.getDesc())) {
			holder.mDescTv.setVisibility(View.GONE);
		} else {
			holder.mDescTv.setVisibility(View.VISIBLE);
			holder.mDescTv.setText(entry.getDesc());
		}
		holder.mPubDateTv.setText(entry.getPubDate());

		MenuItem menuItem = holder.mToolbar.getMenu().findItem(R.id.action_share_item);
		menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Api.getTinyUrl(entry.getUrlMobile(), new Callback<Response>() {
					@Override
					public void success(Response response, retrofit.client.Response response2) {
						String subject = App.Instance.getString(R.string.lbl_share_item_title);
						String text = App.Instance.getString(R.string.lbl_share_item_content, entry.getTitle(),
								response.getResult(), subject, Prefs.getInstance().getAppTinyuUrl());
						EventBus.getDefault().post(new ShareEvent(subject, text));
					}

					@Override
					public void failure(RetrofitError error) {
						String subject = App.Instance.getString(R.string.lbl_share_item_title);
						String text = App.Instance.getString(R.string.lbl_share_item_content, entry.getTitle(),
								entry.getUrlMobile(), subject, Prefs.getInstance().getAppTinyuUrl());
						EventBus.getDefault().post(new ShareEvent(subject, text));
					}
				});
				return true;
			}
		});
		holder.itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new OpenLinkEvent(entry.getUrlMobile()));
			}
		});

	}

	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	/**
	 * ViewHolder for the list.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView mTitleTv;
		private TextView mDescTv;
		private TextView mPubDateTv;
		private Toolbar mToolbar;

		ViewHolder(View convertView) {
			super(convertView);
			mTitleTv = (TextView) convertView.findViewById(R.id.title_tv);
			mDescTv = (TextView) convertView.findViewById(R.id.desc_tv);
			mPubDateTv = (TextView) convertView.findViewById(R.id.pub_date_tv);
			mToolbar = (Toolbar) convertView.findViewById(R.id.toolbar);
			mToolbar.inflateMenu(MENU_LIST_ITEM);
		}
	}
}
