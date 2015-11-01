package com.topfeeds4j.sample.utils;


public final class OscListAdapterHelper extends AbstractAdapterHelper {
	/**
	 * The page of tweets to load.
	 */
	private int mPage = DEFAULT_PAGE;

	private static final int DEFAULT_PAGE = 0;

	@Override
	public void resetPointer() {
		mPage = DEFAULT_PAGE;
	}

	public int getPage() {
		return mPage;
	}

	public void setPage(int page) {
		mPage = page;
	}

	private static OscListAdapterHelper AdapterHelper = new OscListAdapterHelper();

	public static OscListAdapterHelper getInstance() {
		return AdapterHelper;
	}

	protected OscListAdapterHelper() {
	}
}
