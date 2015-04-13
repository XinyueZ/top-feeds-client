package com.topfeeds4j.sample.app.events;

import com.topfeeds4j.ds.NewsEntry;

/**
 * Share message with Facebook or Tweet.
 *
 * @author Xinyue Zhap
 */
public final class ShareEntryEvent {
	/**
	 * Message to share.
	 */
	private NewsEntry mEntry;

	/**
	 * Facebook or Tweet.
	 */
	public enum Type {
		Facebook, Tweet
	}

	/**
	 * Facebook or Tweet.
	 */
	private Type mType;

	/**
	 * Constructor of {@link ShareEntryEvent}
	 *
	 * @param entry
	 * 		Message to share.
	 * @param type
	 * 		Facebook or Tweet.
	 */
	public ShareEntryEvent(NewsEntry entry, Type type) {
		mEntry = entry;
		mType = type;
	}

	/**
	 * @return Facebook or Tweet.
	 */
	public Type getType() {
		return mType;
	}

	/**
	 * @return Facebook or Tweet.
	 */
	public NewsEntry getEntry() {
		return mEntry;
	}
}
