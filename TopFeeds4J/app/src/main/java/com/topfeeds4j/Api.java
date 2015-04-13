package com.topfeeds4j;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.ds.Status;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Api to call <a href="https://github.com/XinyueZ/top-feeds">Top Feeds</a>
 *
 * @author Xinyue Zhao
 */
public final class Api {
	/**
	 * For header, cache before request will be out.
	 */
	private final static RequestInterceptor sInterceptor = new RequestInterceptor() {
		@Override
		public void intercept(RequestFacade request) {
			request.addHeader("Content-Type", "application/json");
		}
	};
	private static final String TAG = Api.class.getSimpleName();
	/**
	 * Response-cache.
	 */
	private static com.squareup.okhttp.Cache sCache;
	/**
	 * The host of API.
	 */
	private static String sHost = null;
	/**
	 * Response-cache size with default value.
	 */
	private static long sCacheSize = 1024 * 10;

	/**
	 * Http-client.
	 */
	private static OkClient sClient = null;
	/**
	 * API methods.
	 */
	private static S s;

	/**
	 * Init the http-client and cache.
	 */
	private static void initClient(Context cxt) {
		// Create an HTTP client that uses a cache on the file system. Android applications should use
		// their Context to get a cache directory.
		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.networkInterceptors().add(new StethoInterceptor());
		File cacheDir = new File(cxt != null ? cxt.getCacheDir().getAbsolutePath() : System.getProperty(
				"java.io.tmpdir"), UUID.randomUUID().toString());
		sCache = new com.squareup.okhttp.Cache(cacheDir, sCacheSize);
		okHttpClient.setCache(sCache);
		okHttpClient.setReadTimeout(3600, TimeUnit.SECONDS);
		okHttpClient.setConnectTimeout(3600, TimeUnit.SECONDS);
		sClient = new OkClient(okHttpClient);
	}

	/**
	 * Init the http-client and cache.
	 */
	private static void initClient() {
		initClient(null);
	}

	/**
	 * To initialize API.
	 *
	 * @param host
	 * 		The host of API.
	 * @param cacheSz
	 * 		Response-cache size .
	 */
	public static void initialize(Context cxt, String host, long cacheSz) {
		sHost = host;
		sCacheSize = cacheSz;
		initClient(cxt);
	}


	/**
	 * To initialize API.
	 *
	 * @param cacheSz
	 * 		Response-cache size.
	 */
	public static void initialize(Context cxt, long cacheSz) {
		sCacheSize = cacheSz;
		initClient(cxt);
	}


	/**
	 * To initialize API.
	 *
	 * @param host
	 * 		The host of API.
	 */
	public static void initialize(Context cxt, String host) {
		sHost = host;
		initClient(cxt);
	}

	static private interface S {
		@GET("/topfeeds")
		void getNewsEntries(@Query("type") int type, @Query("page") int page, Callback<NewsEntries> callback);

		@POST("/bookmarkList")
		void getBookmarkList(Callback<NewsEntries> callback);

		@POST("/bookmark")
		void bookmark( @Body NewsEntry newsEntry,  Callback<Status> callback);

		@POST("/removeBookmark")
		void removeBookmark( @Body  NewsEntry newsEntry,  Callback<Status> callback);
	}

	/**
	 * Ask news from different host.
	 *
	 * @param type
	 * 		{@code 0}: oschina.net, {@code 1}: csdn.
	 * @param page
	 * 		Paging feeds, it works when {@code type = 0}: oschina.net
	 * @param callback
	 * 		Callback when feeds is loaded.
	 */
	public static final void getNewsEntries(@Query("type") int type, @Query("page") int page,
			Callback<NewsEntries> callback) {
		assertCall();
		if (s == null) {
			RestAdapter adapter = new RestAdapter.Builder().setClient(sClient).setRequestInterceptor(
					sInterceptor).setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(sHost).build();
			s = adapter.create(S.class);
		}
		s.getNewsEntries(type, page, callback);
	}

	private static void assertCall() {
		if (sClient == null) {//Create http-client when needs.
			initClient();
		}
		if (sHost == null) {//Default when needs.
			sHost = "http://top-feeds2-91415.appspot.com/";
		}
		Log.i(TAG, String.format("Host:%s, Cache:%d", sHost, sCacheSize));
		if (sCache != null) {
			Log.i(TAG, String.format("RequestCount:%d", sCache.getRequestCount()));
			Log.i(TAG, String.format("NetworkCount:%d", sCache.getNetworkCount()));
			Log.i(TAG, String.format("HitCount:%d", sCache.getHitCount()));
		}
	}

	/**
	 * Add a news-entry to bookmark-list.
	 * @param newsEntry {@link NewsEntry}, a news to bookmark.
	 * @param callback 	Callback after being bookmarked.
	 */
	public static final void bookmark(NewsEntry newsEntry, Callback<Status> callback) {
		assertCall();
		if (s == null) {
			RestAdapter adapter = new RestAdapter.Builder().setClient(sClient).setRequestInterceptor(
					sInterceptor).setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(sHost).build();
			s = adapter.create(S.class);
		}
		s.bookmark(newsEntry, callback);
	}


	/**
	 * Remove a news-entry to bookmark-list.
	 * @param newsEntry {@link NewsEntry}, which will be removed from bookmark-list
	 * @param callback 	Callback after being removed from bookmark-list.
	 */
	public static final void removeBookmark(NewsEntry newsEntry, Callback<Status> callback) {
		assertCall();
		if (s == null) {
			RestAdapter adapter = new RestAdapter.Builder().setClient(sClient).setRequestInterceptor(
					sInterceptor).setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(sHost).build();
			s = adapter.create(S.class);
		}
		s.removeBookmark(newsEntry, callback);
	}

	/**
	 * Get list of bookmarked {@link NewsEntry}.
	 * @param callback 	Callback contains list of bookmarked {@link NewsEntry}.
	 */
	public static final void getBookmarkList( Callback<NewsEntries> callback) {
		assertCall();
		if (s == null) {
			RestAdapter adapter = new RestAdapter.Builder().setClient(sClient).setRequestInterceptor(
					sInterceptor).setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(sHost).build();
			s = adapter.create(S.class);
		}
		s.getBookmarkList(callback);
	}
}
