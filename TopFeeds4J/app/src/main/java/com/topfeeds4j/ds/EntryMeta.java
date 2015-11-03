package com.topfeeds4j.ds;


import com.google.gson.annotations.SerializedName;

public final class EntryMeta {
	@SerializedName("id")
	private String mId;


	public EntryMeta(String id) {
		mId = id;
	}


	public String getId() {
		return mId;
	}
}
