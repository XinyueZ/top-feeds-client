package com.topfeeds4j.sample.app.events;

import com.topfeeds4j.ds.NewsEntry;

public final class OpenLinkEvent {
	private String mUrl;
	private String mTitle;
	private NewsEntry mNewsEntry;

	public OpenLinkEvent(String url, String title, NewsEntry entry) {
		mUrl = url;
		mTitle = title;
		mNewsEntry = entry;
	}

	public String getUrl() {
		return mUrl;
	}


	public String getTitle() {
		return mTitle;
	}

	public NewsEntry getNewsEntry() {
		return mNewsEntry;
	}
}
