package com.topfeeds4j.sample.utils;


import java.util.Calendar;

public final class AndroiderListAdapterHelper extends AbstractAdapterHelper {
	/**
	 * First page for Geeker-news.
	 */
	private String mFrom = DEFAULT_FROM;

	private static final String DEFAULT_FROM = "";
	/**
	 * Previous page's start point.
	 */
	private String mPrevious = DEFAULT_FROM;

	private String mFirstPage;

	@Override
	public void resetPointer() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		String defaultDateTime = String.format("%d:%d", year, month);
		mFrom =defaultDateTime;
		mPrevious =defaultDateTime;
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

	private static AndroiderListAdapterHelper AdapterHelper = new AndroiderListAdapterHelper();

	public static AndroiderListAdapterHelper getInstance() {
		return AdapterHelper;
	}

	protected AndroiderListAdapterHelper() {
		resetPointer();
		mFirstPage = mFrom;
	}

	public String getFirstPage() {
		return mFirstPage;
	}
}
