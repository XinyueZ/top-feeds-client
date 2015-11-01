package com.topfeeds4j.sample.utils;

import com.topfeeds4j.sample.app.adapters.NewsListAdapter;

public abstract class AbstractAdapterHelper {
	private NewsListAdapter mNewsListAdapter = new NewsListAdapter(null);

	public NewsListAdapter getNewsListAdapter() {
		return mNewsListAdapter;
	}

	public void resetPointer() {

	}
}
