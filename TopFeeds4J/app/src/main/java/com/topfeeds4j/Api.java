package com.topfeeds4j;

import com.topfeeds4j.ds.NewsEntries;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Query;

/**
 * Api to call <a href="https://github.com/XinyueZ/top-feeds">Top Feeds</a>
 *
 * @author Xinyue Zhao
 */
public final class Api {
	public static final void getNewsEntries(@Query("type") int type, @Query("page") int page,
			Callback<NewsEntries> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://top-feeds-90308.appspot.com/")
				.build();
		ApiService apiService = restAdapter.create(ApiService.class);
		apiService.getNewsEntries(type, page, callback);
	}
}
