package com.topfeeds4j.sample.utils;

import android.content.Context;

import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;
import static android.text.format.DateUtils.formatDateTime;

/**
 * Utils method for date-time operations.
 *
 * @author Xinyue Zhao
 */
public final class DateTimeUtils {

	/**
	 * Convert a timestamps to a readable date in string.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param timestamps
	 * 		A long value for a timestamps.
	 *
	 * @return A date string format.
	 */
	public static String convertTimestamps2DateString(Context cxt, long timestamps) {
		return formatDateTime(cxt, timestamps, FORMAT_SHOW_YEAR | FORMAT_SHOW_DATE | FORMAT_ABBREV_MONTH);
	}
}
