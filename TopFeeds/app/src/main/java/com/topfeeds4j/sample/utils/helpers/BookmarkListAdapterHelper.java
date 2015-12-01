package com.topfeeds4j.sample.utils.helpers;


public final class BookmarkListAdapterHelper extends AbstractAdapterHelper {
	private static BookmarkListAdapterHelper ourInstance = new BookmarkListAdapterHelper();

	public static BookmarkListAdapterHelper getInstance() {
		return ourInstance;
	}

	private BookmarkListAdapterHelper() {
	}
}
