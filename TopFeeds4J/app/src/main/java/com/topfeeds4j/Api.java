package com.topfeeds4j;

import com.topfeeds4j.ds.NewsEntries;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Api to call <a href="https://github.com/XinyueZ/top-feeds">Top Feeds</a>
 *
 * @author Xinyue Zhao
 */
public final class Api {
	private interface S {
		@GET("/topfeeds")
		void getNewsEntries(@Query("type") int type, @Query("page") int page, Callback<NewsEntries> callback);

		@GET("/topfeeds")
		NewsEntries getNewsEntries(@Query("type") int type, @Query("page") int page);
	}

	/**
	 * Ask news from different host.
	 * @param type {@code 0}: oschina.net, {@code 1}: csdn.
	 * @param page Paging feeds, it works when {@code type = 0}: oschina.net
	 * @param callback Callback when feeds is loaded.
	 */
	public static final void getNewsEntries(@Query("type") int type, @Query("page") int page,
			Callback<NewsEntries> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://top-feeds-90308.appspot.com/").build();
		S s = restAdapter.create(S.class);
		s.getNewsEntries(type, page, callback);
	}
}
