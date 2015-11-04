package com.topfeeds4j.sample.utils.helpers;


import android.support.v4.util.LongSparseArray;

import com.topfeeds4j.sample.utils.Prefs;

public final class BloggerHelperFactory {
	private static BloggerHelperFactory sInstance = null;
	private LongSparseArray<BloggerPagesAdapterHelper> mHelpers;

	public static void createInstance() {
		if (sInstance == null) {
			sInstance = new BloggerHelperFactory();
		}
	}

	public static BloggerHelperFactory getInstance() {
		return sInstance;
	}


	public BloggerHelperFactory() {
		mHelpers = new LongSparseArray<>();
		long[] ids = Prefs.getInstance().getBloggerIds();
		//create adapter-helpers
		for (long id : ids) {
			mHelpers.put(id, new BloggerPagesAdapterHelper());
		}
	}

	public BloggerPagesAdapterHelper getHelper(long id) {
		return mHelpers.get(id);
	}
}
