package com.topfeeds4j;


import com.topfeeds4j.ds.NewsEntries;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ApiService {
	@GET("/topfeeds")
	void getNewsEntries(@Query("type") int type, @Query("page") int page, Callback<NewsEntries> callback);

	@GET("/topfeeds")
	NewsEntries getNewsEntries(@Query("type") int type, @Query("page") int page);
}
