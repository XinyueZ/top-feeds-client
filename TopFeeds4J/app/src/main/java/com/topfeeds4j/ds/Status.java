package com.topfeeds4j.ds;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Status implements Serializable {
	@SerializedName("status")
	private int mStatus;


	public int getStatus() {
		return mStatus;
	}
}
