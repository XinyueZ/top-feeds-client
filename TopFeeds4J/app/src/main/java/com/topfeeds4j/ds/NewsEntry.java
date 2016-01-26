package com.topfeeds4j.ds;


import java.io.Serializable;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public final class NewsEntry implements Serializable {
	@SerializedName("title")
	@JsonProperty("title")
	private String mTitle;
	@SerializedName("desc")
	@JsonProperty("desc")
	private String mDesc;
	@SerializedName("url")
	@JsonProperty("url")
	private String mUrl;
	@SerializedName("url_mobile")
	@JsonProperty("url_mobile")
	private String mUrlMobile;
	@SerializedName("pubDate")
	@JsonProperty("pubDate")
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

	@JsonProperty("url_mobile")
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
				TextUtils.equals(mUrlMobile, other.mUrlMobile)  ;
	}
}
