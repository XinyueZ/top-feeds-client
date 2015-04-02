package com.topfeeds4j.sample.app.adapters;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.events.OpenLinkEvent;

import de.greenrobot.event.EventBus;


/**
 * The adapter for news-list.
 *
 * @author Xinyue Zhao
 */
public final class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
	/**
	 * Data-source.
	 */
	private List<NewsEntry> mData;
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_news_list;


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
	public void onBindViewHolder(ViewHolder holder, int position) {
		final NewsEntry entry = mData.get(position);
		holder.mTitleTv.setText(entry.getTitle());
		if (TextUtils.isEmpty(entry.getDesc())) {
			holder.mDescTv.setVisibility(View.GONE);
		} else {
			holder.mDescTv.setVisibility(View.VISIBLE);
			holder.mDescTv.setText(entry.getDesc());
		}
		holder.mPubDateTv.setText(entry.getPubDate());
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

		ViewHolder(View convertView) {
			super(convertView);
			mTitleTv = (TextView) convertView.findViewById(R.id.title_tv);
			mDescTv = (TextView) convertView.findViewById(R.id.desc_tv);
			mPubDateTv = (TextView) convertView.findViewById(R.id.pub_date_tv);
		}
	}
}
