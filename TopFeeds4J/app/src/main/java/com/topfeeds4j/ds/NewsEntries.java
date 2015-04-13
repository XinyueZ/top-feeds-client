package com.topfeeds4j.ds;


import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class NewsEntries extends Status {
	@SerializedName("site")
	private String mSite;
	@SerializedName("site_mobile")
	private String mSiteMobile;
	@SerializedName("result")
	private List<NewsEntry> mNewsEntries;


	public String getSite() {
		return mSite;
	}

	public String getSiteMobile() {
		return mSiteMobile;
	}

	public List<NewsEntry> getNewsEntries() {
		return mNewsEntries;
	}
}
