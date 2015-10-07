package com.topfeeds4j.sample.app.events;

public final class OpenLinkEvent {
	private String mUrl;
	private String mTitle;


	public OpenLinkEvent(String url, String title) {
		mUrl = url;
		mTitle = title;
	}

	public String getUrl() {
		return mUrl;
	}


	public String getTitle() {
		return mTitle;
	}
}
