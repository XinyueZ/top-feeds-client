package com.topfeeds4j.sample.utils;

import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.topfeeds4j.Api;
import com.topfeeds4j.Api.TopFeeds;
import com.topfeeds4j.ds.NewsEntries;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.fragments.BookmarkListPageFragment;
import com.topfeeds4j.sample.app.fragments.CsdnNewsListPageFragment;
import com.topfeeds4j.sample.app.fragments.GeekListPageFragment;
import com.topfeeds4j.sample.app.fragments.OscNewsListPageFragment;
import com.topfeeds4j.sample.app.fragments.TechugNewsListPageFragment;

import retrofit2.Call;
import retrofit2.Callback;


public final class Utils {
	public static void facebookShare( Context cxt, NewsEntry msg ) {
		final WebDialog fbDlg;
		Bundle          postParams = new Bundle();
		String          desc       = !TextUtils.isEmpty( msg.getDesc() ) ? msg.getDesc() : null;
		if( desc == null ) {
			fbDlg = new WebDialog.FeedDialogBuilder( cxt, cxt.getString( R.string.applicationId ), postParams ).setName( msg.getTitle() ).setLink(
					msg.getUrlMobile() ).build();
		} else {
			fbDlg = new WebDialog.FeedDialogBuilder( cxt, cxt.getString( R.string.applicationId ), postParams ).setName( msg.getTitle() )
					.setDescription( desc ).setLink( msg.getUrlMobile() ).build();
		}
		fbDlg.setOnCompleteListener( new OnCompleteListener() {
			@Override
			public void onComplete( Bundle bundle, FacebookException e ) {
				fbDlg.dismiss();
			}
		} );
		fbDlg.show();
	}

	@Nullable
	public static Fragment getFragment( Context cxt, int position ) {
		switch( position ) {
			case 4:
				return BookmarkListPageFragment.newInstance( cxt );
			case 3:
				return OscNewsListPageFragment.newInstance( cxt );
			case 2:
				return CsdnNewsListPageFragment.newInstance( cxt );
			case 1:
				return TechugNewsListPageFragment.newInstance( cxt );
			case 0:
				return GeekListPageFragment.newInstance( cxt );
		}
		return null;
	}


	public static void loadBookmarkList( Callback<NewsEntries> callback ) {
		String ident = null;
		try {
			ident = DeviceUniqueUtil.getDeviceIdent( App.Instance );
			Call<NewsEntries> bookmarkListCall = Api.Retrofit.create( TopFeeds.class )
														 .getBookmarkList( ident );
			bookmarkListCall.enqueue( callback );
		} catch( NoSuchAlgorithmException e ) {
			e.printStackTrace();
		}
	}
}
