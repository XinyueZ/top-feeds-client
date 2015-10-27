package com.topfeeds4j.sample.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;


public final class Utils {
	public  static void facebookShare(Context cxt, NewsEntry msg) {
		final WebDialog fbDlg;
		Bundle postParams = new Bundle();
		String desc = !TextUtils.isEmpty(msg.getDesc()) ? msg.getDesc() : null;
		if (desc == null) {
			  fbDlg = new WebDialog.FeedDialogBuilder(cxt, cxt.getString(R.string.applicationId), postParams).setName(
					msg.getTitle()).setLink(msg.getUrlMobile()).build();
		} else {
			  fbDlg = new WebDialog.FeedDialogBuilder(cxt, cxt.getString(R.string.applicationId), postParams).setName(
					msg.getTitle()).setDescription(desc).setLink(msg.getUrlMobile()).build();
		}
		fbDlg.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(Bundle bundle, FacebookException e) {
				fbDlg.dismiss();
			}
		});
		fbDlg.show();
	}
}
