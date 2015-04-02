package com.topfeeds4j.ds;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class NewsEntries implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("site")
	private String mSite;
	@SerializedName("site_mobile")
	private String mSiteMobile;
	@SerializedName("result")
	private List<NewsEntry> mNewsEntries;


	public int getStatus() {
		return mStatus;
	}

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
