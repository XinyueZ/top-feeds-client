package com.topfeeds4j.sample.utils.helpers;


public abstract class AbstractLinkedPagesAdapterHelper extends AbstractAdapterHelper {
	private String mFrom = DEFAULT_FROM;
	private static final String DEFAULT_FROM = "0";
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

}
