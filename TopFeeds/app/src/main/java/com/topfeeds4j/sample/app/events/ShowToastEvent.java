package com.topfeeds4j.sample.app.events;

/**
 * Show toast information.
 *
 * @author Xinyue Zhao
 */
public final class ShowToastEvent {
	public enum Type {WARNING, INFO, ERROR, PROGRESS}

	;

	private Type mType;

	private String mText;

	public ShowToastEvent( Type type, String text ) {
		mType = type;
		mText = text;
	}

	public Type getType() {
		return mType;
	}

	public String getText() {
		return mText;
	}
}
