package com.topfeeds4j.ds;


import java.io.Serializable;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public final class NewsEntry implements Serializable {
	@SerializedName("title")
	private String mTitle;
	@SerializedName("desc")
	private String mDesc;
	@SerializedName("url")
	private String mUrl;
	@SerializedName("url_mobile")
	private String mUrlMobile;
	@SerializedName("pubDate")
	private long mPubDate;


	public String getTitle() {
		return mTitle;
	}

	public String getDesc() {
		return mDesc;
	}

	public String getUrl() {
		return mUrl;
	}

	public String getUrlMobile() {
		return mUrlMobile;
	}

	public long getPubDate() {
		return mPubDate;
	}

	@Override
	public boolean equals(Object o) {
		NewsEntry other = (NewsEntry) o;
		return TextUtils.equals(mTitle, other.mTitle) && TextUtils.equals(mDesc, other.mDesc) && TextUtils.equals(mUrl,
				other.mUrl) &&
				TextUtils.equals(mUrlMobile, other.mUrlMobile) && mPubDate == other.mPubDate;
	}
}
