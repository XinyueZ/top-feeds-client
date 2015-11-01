package com.topfeeds4j.sample.utils;


public  final class CSDNListAdapterHelper extends AbstractAdapterHelper {
	private static CSDNListAdapterHelper ourInstance = new CSDNListAdapterHelper();

	public static CSDNListAdapterHelper getInstance() {
		return ourInstance;
	}

	private CSDNListAdapterHelper() {
	}
}
