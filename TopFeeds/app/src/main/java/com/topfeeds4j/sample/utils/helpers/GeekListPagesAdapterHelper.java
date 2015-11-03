package com.topfeeds4j.sample.utils.helpers;


public final class GeekListPagesAdapterHelper extends AbstractLinkedPagesAdapterHelper {
	/**
	 * First page for Geeker-news.
	 */
	private String mFrom = DEFAULT_FROM;

	private static final String DEFAULT_FROM = "0";
	/**
	 * Previous page's start point.
	 */
	private String mPrevious = DEFAULT_FROM;

	@Override
	public void resetPointer() {
		mFrom = DEFAULT_FROM;
		mPrevious = DEFAULT_FROM;
	}

	public String getFrom() {
		return mFrom;
	}

	public void setFrom(String from) {
		mFrom = from;
	}

	public String getPrevious() {
		return mPrevious;
	}

	public void setPrevious(String previous) {
		mPrevious = previous;
	}

	private static GeekListPagesAdapterHelper AdapterHelper = new GeekListPagesAdapterHelper();

	public static GeekListPagesAdapterHelper getInstance() {
		return AdapterHelper;
	}

	protected GeekListPagesAdapterHelper() {
	}
}
