package com.topfeeds4j.sample.app.events;

/**
 * Refresh list  event.
 *
 * @author Xinyue Zhao
 */
public final class RefreshListEvent {
	private int mPosition;


	public RefreshListEvent( int position ) {
		mPosition = position;
	}


	public int getPosition() {
		return mPosition;
	}
}
