package com.topfeeds4j.sample.app.events;


public final class FABEvent {
	private boolean mHide;

	public FABEvent( boolean hide ) {
		mHide = hide;
	}


	public boolean isHide() {
		return mHide;
	}
}
