package com.topfeeds4j.sample.utils;

import android.content.Context;
import android.support.v4.util.LongSparseArray;

import com.chopping.application.BasicPrefs;

/**
 * Store app and device information.
 *
 * @author Chris.Xinyue Zhao
 */
public final class Prefs extends BasicPrefs {
	/**
	 * Storage. Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 * {@code true} if EULA has been shown and agreed.
	 */
	private static final String KEY_EULA_SHOWN = "key_eula_shown";
	/**
	 * Storage for the tinyurl of the application's store-location.
	 */
	private static final String KEY_APP_TINYURL = "key_app_tinyurl";
	private static final String KEY_SHOWN_DETAILS_ADS_TIMES = "ads";
	private static final String KEY_SHOWN_DETAILS_TIMES = "key.details.shown.times";
	private static final String KEY_DEVICE_IDENT = "key.device.ident";

	private static final String KEY_VIEW_MODE = "key.view.mode";
	private static final String KEY_BLOGGER_ID_LIST = "blogger_ids";
	private static final String KEY_BLOGGER_NAME_LIST = "blogger_names";

	public static final int VIEW_MODE_MULTI = 0;
	public static  final int VIEW_MODE_SINGLE = 1;
	/**
	 * The Instance.
	 */
	private static Prefs sInstance;



	private Prefs() {
		super(null);
	}

	/**
	 * Created a DeviceData storage.
	 *
	 * @param context
	 * 		A context object.
	 */
	private Prefs(Context context) {
		super(context);
	}

	/**
	 * Singleton method.
	 *
	 * @param context
	 * 		A context object.
	 *
	 * @return single instance of DeviceData
	 */
	public static Prefs createInstance(Context context) {
		if (sInstance == null) {
			synchronized (Prefs.class) {
				if (sInstance == null) {
					sInstance = new Prefs(context);
				}
			}
		}
		return sInstance;
	}

	/**
	 * Singleton getInstance().
	 *
	 * @return The instance of Prefs.
	 */
	public static Prefs getInstance() {
		return sInstance;
	}


	/**
	 * Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @return {@code true} if EULA has been shown and agreed.
	 */
	public boolean isEULAOnceConfirmed() {
		return getBoolean(KEY_EULA_SHOWN, false);
	}

	/**
	 * Set whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @param isConfirmed
	 * 		{@code true} if EULA has been shown and agreed.
	 */
	public void setEULAOnceConfirmed(boolean isConfirmed) {
		setBoolean(KEY_EULA_SHOWN, isConfirmed);
	}

	/**
	 * @return Storage for the tinyurl of the application's store-location.
	 */
	public String getAppTinyuUrl() {
		return getString(KEY_APP_TINYURL, null);
	}

	/**
	 * Set the storage for the tinyurl of the application's store-location.
	 *
	 * @param url
	 * 		the application's store-location.
	 */
	public void setAppTinyUrl(String url) {
		setString(KEY_APP_TINYURL, url);
	}

	public int getShownDetailsAdsTimes() {
		return getInt(KEY_SHOWN_DETAILS_ADS_TIMES, 5);
	}

	public void setShownDetailsTimes(int times) {
		setInt(KEY_SHOWN_DETAILS_TIMES, times);
	}

	public int getShownDetailsTimes() {
		return getInt(KEY_SHOWN_DETAILS_TIMES, 1);
	}

	/**
	 * @return Cache size for response.
	 */
	public int getCacheSize() {
		return getInt("cache_size", 1024);
	}

	/**
	 * @return Location of API.
	 */
	public String getTopFeeds4JHost() {
		return getString("topfeeds4j_host", "http://top-feeds2-91415.appspot.com/");
	}

	/**
	 * Set device identifier for remote storage.
	 *
	 * @param ident
	 * 		An identifier.
	 */
	public void setDeviceIdent(String ident) {
		setString(KEY_DEVICE_IDENT, ident);
	}

	/**
	 * @return The device identifier for remote storage.
	 */
	public String getDeviceIdent() {
		return getString(KEY_DEVICE_IDENT, null);
	}

	public int getViewMode() {
		return getInt(KEY_VIEW_MODE, VIEW_MODE_SINGLE);
	}

	public void setViewMode(int mode) {
		  setInt(KEY_VIEW_MODE, mode);
	}

	/**
	 * Get all blogger meta information.
	 * @return A list of [blogger-id : blogger-name].
	 */
	public LongSparseArray<String> getBloggersMeta () {
		LongSparseArray<String> lst = new LongSparseArray<>();
		String ids = getString(KEY_BLOGGER_ID_LIST, null);
		String names = getString(KEY_BLOGGER_NAME_LIST, null);
		String[] idList = ids.split(",");
		String[] nameList = names.split(",");
		int i = 0;
		for(String id : idList) {
			lst.put(Long.valueOf(id), nameList[i++]);
		}
		return lst;
	}

	/**
	 * Get id-list of blogger.
	 */
	public long[] getBloggerIds() {
		String ids = getString(KEY_BLOGGER_ID_LIST, null);
		String[] idList = ids.split(",");
		long[] ret = new long[idList.length];
		int i = 0;
		for(String id : idList) {
			ret[i++] = Long.valueOf(id);
		}
		return ret;
	}

	/**
	 * Get name-list of blogger.
	 */
	public String[] getBloggerNames() {
		String names = getString(KEY_BLOGGER_NAME_LIST, null);
		String[] nameList = names.split(",");
		String[] ret = new String[nameList.length];
		int i = 0;
		for(String name : nameList) {
			ret[i++] = name;
		}
		return ret;
	}
}
