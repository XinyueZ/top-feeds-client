/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG, Never BUG.
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱。

package com.topfeeds4j.sample.app;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.support.multidex.MultiDexApplication;

import com.chopping.net.TaskHelper;
import com.chopping.utils.RestUtils;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.firebase.client.Firebase;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.utils.DeviceUniqueUtil;
import com.topfeeds4j.sample.utils.Prefs;

import io.fabric.sdk.android.Fabric;


public final class App extends MultiDexApplication {
	/**
	 * Application's instance.
	 */
	public static App Instance;

	{
		Instance = this;
	}

	/**
	 * The list of all saved favorite news.
	 */
	private List<NewsEntry> mBookmarkList;

	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with( this, new Crashlytics() );
		TaskHelper.init( getApplicationContext() );
		Prefs prefs = Prefs.createInstance( this );
		try {
			DeviceUniqueUtil.getDeviceIdent( this );
		} catch( NoSuchAlgorithmException e ) {
			//Ignore.
		}
		Stetho.initialize( Stetho.newInitializerBuilder( this ).enableDumpapp( Stetho.defaultDumperPluginsProvider( this ) )
								   .enableWebKitInspector( Stetho.defaultInspectorModulesProvider( this ) ).build() );



		Firebase.setAndroidContext( App.Instance );
		String[] fireInfo = RestUtils.initRest(
				this,
				true
		);
		prefs.setFirebaseUrl( fireInfo[ 0 ] );
		prefs.setFirebaseAuth( fireInfo[ 1 ] );
		prefs.setFirebaseStandardLastLimit( Integer.valueOf( fireInfo[ 2 ] ) );
	}


	public boolean isBookmarked( NewsEntry item ) {
		if( mBookmarkList == null ) {
			return false;
		}

		if( item == null ) {
			return false;
		}
		for( NewsEntry t : mBookmarkList ) {
			if( t.equals( item ) ) {
				return true;
			}
		}
		return false;
	}


	public void addBookmark( NewsEntry item ) {
		if(mBookmarkList == null) {
			mBookmarkList = new ArrayList<>(  );
		}
		mBookmarkList.add( 0, item );
	}


	public void removeBookmark( NewsEntry item ) {
		if( mBookmarkList != null ) {
			for( NewsEntry fi : mBookmarkList ) {
				if( fi.equals( item ) ) {
					mBookmarkList.remove( fi );
					return;
				}
			}
		}
	}
	public List<NewsEntry> getBookmarkList() {
		return mBookmarkList;
	}
	public void setBookmarkList( List<NewsEntry> list ) {
		mBookmarkList = list;
	}
}
