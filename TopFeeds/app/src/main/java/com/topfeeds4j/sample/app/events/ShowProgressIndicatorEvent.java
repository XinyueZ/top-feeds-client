package com.topfeeds4j.sample.app.events;


public final class ShowProgressIndicatorEvent {
	private boolean mShow;

	public ShowProgressIndicatorEvent(boolean show) {
		mShow = show;
	}

	public boolean isShow() {
		return mShow;
	}
}
