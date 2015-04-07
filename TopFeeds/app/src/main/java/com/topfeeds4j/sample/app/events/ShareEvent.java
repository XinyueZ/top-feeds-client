package com.topfeeds4j.sample.app.events;


public final class ShareEvent {
	private String mSubject;
	private String mContent;

	public ShareEvent(String subject, String content) {
		mSubject = subject;
		mContent = content;
	}

	public String getSubject() {
		return mSubject;
	}

	public String getContent() {
		return mContent;
	}
}
