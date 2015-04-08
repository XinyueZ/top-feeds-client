package com.topfeeds4j;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.topfeeds4j.ds.NewsEntries;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Api to call <a href="https://github.com/XinyueZ/top-feeds">Top Feeds</a>
 *
 * @author Xinyue Zhao
 */
public final class Api {
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
		File cacheDir = new File(cxt != null ? cxt.getCacheDir().getAbsolutePath() : System.getProperty(
				"java.io.tmpdir"), UUID.randomUUID().toString());
		try {
			sCache = new com.squareup.okhttp.Cache(cacheDir, sCacheSize);
			okHttpClient.setCache(sCache);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	private interface S {
		@GET("/topfeeds")
		void getNewsEntries(@Query("type") int type, @Query("page") int page, Callback<NewsEntries> callback);

		@GET("/topfeeds")
		NewsEntries getNewsEntries(@Query("type") int type, @Query("page") int page);
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
		if (sClient == null) {//Create http-client when needs.
			initClient();
		}
		if (sHost == null) {//Default when needs.
			sHost = "http://top-feeds-90308.appspot.com/";
		}
		Log.i(TAG, String.format("Host:%s, Cache:%d", sHost, sCacheSize));
		if (sCache != null) {
			Log.i(TAG, String.format("RequestCount:%d", sCache.getRequestCount()));
			Log.i(TAG, String.format("NetworkCount:%d", sCache.getNetworkCount()));
			Log.i(TAG, String.format("HitCount:%d", sCache.getHitCount()));
		}
		if (s == null) {
			RestAdapter adapter = new RestAdapter.Builder().setClient(sClient).setEndpoint(sHost).build();
			s = adapter.create(S.class);
		}
		s.getNewsEntries(type, page, callback);
	}
}
