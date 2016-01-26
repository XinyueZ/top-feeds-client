package com.topfeeds4j;

import com.topfeeds4j.ds.EntryMeta;
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.ds.Status;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Api to call <a href="https://github.com/XinyueZ/top-feeds">Top Feeds</a>
 *
 * @author Xinyue Zhao
 */
public final class Api {
	private static final String TAG = Api.class.getSimpleName();
	public static Retrofit Retrofit;

	/**
	 * To initialize API.
	 *
	 * @param host
	 * 		The host of API.
	 */
	public static void initialize( String host ) {
		Retrofit = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create() )
										 .baseUrl( host )
										 .build();
	}


	public interface TopFeeds {
		@GET("/topfeeds")
		Call<NewsEntries> getNewsEntries( @Query("type") int type, @Query("page") int page );

		@GET("/topfeeds")
		Call<NewsEntries> getNewsEntries( @Query("type") int type, @Query("page") String page );

		@POST("/topfeeds")
		Call<NewsEntries> getNewsEntries( @Query("type") int type, @Query("page") String page, @Body EntryMeta meta );


		@GET("/bookmarkList")
		Call<NewsEntries> getBookmarkList( @Query("ident") String ident );

		@POST("/bookmark")
		Call<Status> bookmark( @Body NewsEntry newsEntry, @Query("ident") String ident );

		@POST("/removeBookmark")
		Call<Status> removeBookmark( @Body NewsEntry newsEntry, @Query("ident") String ident );
	}
}
