package com.topfeeds4j.sample.app.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chopping.utils.DateTimeUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tinyurl4j.Api;
import com.tinyurl4j.Api.TinyUrl;
import com.tinyurl4j.data.Response;
import com.topfeeds4j.ds.NewsEntry;
import com.topfeeds4j.sample.R;
import com.topfeeds4j.sample.app.App;
import com.topfeeds4j.sample.app.events.OpenLinkEvent;
import com.topfeeds4j.sample.app.events.RefreshListEvent;
import com.topfeeds4j.sample.app.events.ShareEntryEvent;
import com.topfeeds4j.sample.app.events.ShareEntryEvent.Type;
import com.topfeeds4j.sample.app.events.ShareEvent;
import com.topfeeds4j.sample.app.events.ShowToastEvent;
import com.topfeeds4j.sample.utils.DynamicShareActionProvider;
import com.topfeeds4j.sample.utils.Prefs;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * The adapter for news-list.
 *
 * @author Xinyue Zhao
 */
public final class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_news_list;

	/**
	 * A menu on each line of list.
	 */
	private static final int MENU_LIST_ITEM = R.menu.menu_item;


	/**
	 * Data-source.
	 */
	private List<NewsEntry> mData;


	/**
	 * Constructor of {@link NewsListAdapter}.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public NewsListAdapter( List<NewsEntry> data ) {
		setData( data );
	}

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<NewsEntry> getData() {
		return mData;
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData( List<NewsEntry> data ) {
		mData = data;
	}

	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		View                       convertView = LayoutInflater.from( cxt )
															   .inflate(
																	   ITEM_LAYOUT,
																	   parent,
																	   false
															   );
		NewsListAdapter.ViewHolder viewHolder  = new NewsListAdapter.ViewHolder( convertView );
		return viewHolder;
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, int position ) {
		final NewsEntry entry = mData.get( position );
		holder.mTitleTv.setText( entry.getTitle() );
		boolean isToday = DateUtils.isToday( entry.getPubDate() * 1000 );
		holder.mTitleTv.setTypeface( isToday ? Typeface.DEFAULT_BOLD : Typeface.SANS_SERIF );
		if( TextUtils.isEmpty( entry.getDesc() ) ) {
			holder.mDescTv.setVisibility( View.GONE );
		} else {
			holder.mDescTv.setVisibility( View.VISIBLE );
			holder.mDescTv.setText( entry.getDesc() );
		}
		holder.mPubDateTv.setText( DateTimeUtils.timeConvert2(
				App.Instance,
				entry.getPubDate() * 1000
		) );

		MenuItem                   shareMi            = holder.mToolbar.getMenu()
																	   .findItem( R.id.action_share_item );
		DynamicShareActionProvider shareLaterProvider = (DynamicShareActionProvider) MenuItemCompat.getActionProvider( shareMi );
		shareLaterProvider.setShareDataType( "text/plain" );
		shareLaterProvider.setOnShareLaterListener( new DynamicShareActionProvider.OnShareLaterListener() {
			@Override
			public void onShareClick( final Intent shareIntent ) {
				Call<Response> tinyUrlCall = Api.Retrofit.create( TinyUrl.class )
													 .getTinyUrl( entry.getUrlMobile() );
				tinyUrlCall.enqueue( new Callback<Response>() {
					@Override
					public void onResponse( retrofit2.Response<Response> res ) {
						if(res.isSuccess()) {
							Response response  = res.body();
							String subject = App.Instance.getString( R.string.lbl_share_item_title );
							String text = App.Instance.getString( R.string.lbl_share_item_content,
																  entry.getTitle(),
																  TextUtils.isEmpty( response.getResult() ) ? entry.getUrlMobile() :
																  response.getResult(),
																  subject,
																  Prefs.getInstance()
																	   .getAppTinyuUrl()
							);
							shareIntent.putExtra(
									Intent.EXTRA_SUBJECT,
									subject
							);
							shareIntent.putExtra(
									Intent.EXTRA_TEXT,
									text
							);
							EventBus.getDefault()
									.post( new ShareEvent( shareIntent ) );
						} else {
							onFailure( null );
						}
					}

					@Override
					public void onFailure( Throwable t ) {
						String subject = App.Instance.getString( R.string.lbl_share_item_title );
						String text = App.Instance.getString( R.string.lbl_share_item_content,
															  entry.getTitle(),
															  entry.getUrlMobile(),
															  subject,
															  Prefs.getInstance()
																   .getAppTinyuUrl()
						);
						shareIntent.putExtra(
								Intent.EXTRA_SUBJECT,
								subject
						);
						shareIntent.putExtra(
								Intent.EXTRA_TEXT,
								text
						);
						EventBus.getDefault()
								.post( new ShareEvent( shareIntent ) );
					}
				} );
			}
		} );

		MenuItem openSiteMi = holder.mToolbar.getMenu()
											 .findItem( R.id.action_open_site );
		openSiteMi.setOnMenuItemClickListener( new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick( MenuItem item ) {
				EventBus.getDefault()
						.post( new OpenLinkEvent(
								entry.getUrl(),
								entry.getTitle(),
								entry
						) );
				return true;
			}
		} );
		MenuItem fbShareMi = holder.mToolbar.getMenu()
											.findItem( R.id.action_fb_share_item );
		fbShareMi.setOnMenuItemClickListener( new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick( MenuItem item ) {
				EventBus.getDefault()
						.post( new ShareEntryEvent(
								entry,
								Type.Facebook
						) );
				return true;
			}
		} );

		final MenuItem bookmarkMi      = holder.mToolbar.getMenu()
														.findItem( R.id.action_bookmark_item );
		final MenuItem notBookmarkedMi = holder.mToolbar.getMenu()
														.findItem( R.id.action_not_bookmarked_item );

		notBookmarkedMi.setOnMenuItemClickListener( new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick( final MenuItem item ) {
				bookmarkMi.setEnabled( false );
				notBookmarkedMi.setEnabled( false );
				Prefs    prefs    = Prefs.getInstance();
				App.Instance.removeBookmark( entry );
				Firebase firebase = new Firebase( prefs.getFirebaseUrl() + prefs.getDeviceIdent() );
				firebase.authWithCustomToken( prefs.getFirebaseAuth(), null );
				firebase.setValue( App.Instance.getBookmarkList() );
				firebase.addListenerForSingleValueEvent( new ValueEventListener() {
					@Override
					public void onDataChange( DataSnapshot dataSnapshot ) {
						bookmarkMi.setEnabled( true );
						notBookmarkedMi.setEnabled( true );
						EventBus.getDefault()
								.post( new RefreshListEvent( holder.getAdapterPosition() ) );
						EventBus.getDefault()
								.post( new ShowToastEvent(
										ShowToastEvent.Type.INFO,
										App.Instance.getString( R.string.msg_removed_bookmark )
								) );
					}

					@Override
					public void onCancelled( FirebaseError firebaseError ) {
						EventBus.getDefault()
								.post( new ShowToastEvent(
										ShowToastEvent.Type.ERROR,
										App.Instance.getString( R.string.msg_error )
								) );
						bookmarkMi.setEnabled( true );
						notBookmarkedMi.setEnabled( true );
					}
				} );
				return true;
			}
		} );

		bookmarkMi.setOnMenuItemClickListener( new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick( final MenuItem item ) {
				bookmarkMi.setEnabled( false );
				notBookmarkedMi.setEnabled( false );
				Prefs    prefs    = Prefs.getInstance();
				App.Instance.addBookmark( entry );
				Firebase firebase = new Firebase( prefs.getFirebaseUrl() + prefs.getDeviceIdent() );
				firebase.authWithCustomToken( prefs.getFirebaseAuth(), null );
				firebase.setValue( App.Instance.getBookmarkList() );
				firebase.addListenerForSingleValueEvent( new ValueEventListener() {
					@Override
					public void onDataChange( DataSnapshot dataSnapshot ) {
						bookmarkMi.setEnabled( true );
						notBookmarkedMi.setEnabled( true );
						EventBus.getDefault()
								.post( new RefreshListEvent( holder.getAdapterPosition() ) );
						EventBus.getDefault()
								.post( new ShowToastEvent(
										ShowToastEvent.Type.INFO,
										App.Instance.getString( R.string.msg_added_bookmark )
								) );
					}

					@Override
					public void onCancelled( FirebaseError firebaseError ) {
						EventBus.getDefault()
								.post( new ShowToastEvent(
										ShowToastEvent.Type.ERROR,
										App.Instance.getString( R.string.msg_error )
								) );
						bookmarkMi.setEnabled( true );
						notBookmarkedMi.setEnabled( true );
					}
				} );
				return true;
			}
		} );

		if( App.Instance.getBookmarkList() == null ) {
			bookmarkMi.setVisible( true );
			notBookmarkedMi.setVisible( false );
		} else {
			boolean notBookmarked = !App.Instance.isBookmarked( entry );
			bookmarkMi.setVisible( notBookmarked );
			notBookmarkedMi.setVisible( !notBookmarked );
		}

		holder.mContentV.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				EventBus.getDefault()
						.post( new OpenLinkEvent(
								entry.getUrlMobile(),
								entry.getTitle(),
								entry
						) );
			}
		} );
	}

	@Override
	public int getItemCount() {
		return mData == null ? 0 : mData.size();
	}

	/**
	 * ViewHolder for the list.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView mTitleTv;
		private TextView mDescTv;
		private TextView mPubDateTv;
		private Toolbar  mToolbar;
		private View     mContentV;

		ViewHolder( View convertView ) {
			super( convertView );
			mTitleTv = (TextView) convertView.findViewById( R.id.title_tv );
			mDescTv = (TextView) convertView.findViewById( R.id.desc_tv );
			mPubDateTv = (TextView) convertView.findViewById( R.id.pub_date_tv );
			mToolbar = (Toolbar) convertView.findViewById( R.id.toolbar );
			mContentV = convertView.findViewById( R.id.content_v );
			mToolbar.inflateMenu( MENU_LIST_ITEM );
		}
	}
}
