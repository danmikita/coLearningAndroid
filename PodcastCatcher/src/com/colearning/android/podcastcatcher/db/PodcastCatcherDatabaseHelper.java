package com.colearning.android.podcastcatcher.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.colearning.android.podcastcatcher.model.Subscription;
import com.colearning.android.podcastcatcher.model.SubscriptionItem;

public class PodcastCatcherDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "podcastCatcher.sqlite";
	private static final int VERSION = 1;

	public static final String TABLE_SUBSCRIPTION = "subscription";
	public static final String COLUMN_SUBSCRIPTION_ID = "_id";
	public static final String COLUMN_SUBSCRIPTION_FEED_URL = "feed_url";
	public static final String COLUMN_SUBSCRIPTION_TITLE = "title";
	public static final String COLUMN_SUBSCRIPTION_SUBTITLE = "subtitle";
	public static final String COLUMN_SUBSCRIPTION_AUTHOR = "author";
	public static final String COLUMN_SUBSCRIPTION_SUMMARY = "summary";
	public static final String COLUMN_SUBSCRIPTION_CATEGORY = "category";
	public static final String COLUMN_SUBSCRIPTION_IMAGE_URL = "image_url";
	public static final String COLUMN_SUBSCRIPTION_LAST_SYNC_TIME = "last_sync_time";

	public static final String TABLE_SUBSCRIPTION_ITEM = "subscription_item";
	public static final String COLUMN_SUBSCRIPTION_ITEM_ID = "_id";
	public static final String COLUMN_SUBSCRIPTION_ITEM_SUBSCRIPTION_ID = "subscription_id";
	public static final String COLUMN_SUBSCRIPTION_ITEM_TITLE = "title";
	public static final String COLUMN_SUBSCRIPTION_ITEM_GUID_ID = "guid_id";
	public static final String COLUMN_SUBSCRIPTION_ITEM_LINK_URL = "link_url";
	public static final String COLUMN_SUBSCRIPTION_ITEM_THUMBNAIL_URL = "thumbnail_url";
	public static final String COLUMN_SUBSCRIPTION_ITEM_ITEM_DESC = "item_desc";
	public static final String COLUMN_SUBSCRIPTION_ITEM_MEDIA_URL = "media_url";
	public static final String COLUMN_SUBSCRIPTION_ITEM_FILE_LOCATION = "file_location";

	public PodcastCatcherDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//@formatter:off
		db.execSQL(
				"create table subscription (" +
				"_id integer primary key autoincrement, " +
				"feed_url varchar(200), " +
				"title varchar(200), " +
				"subtitle varchar(200), " +
				"author varchar(200), " +
				"summary varchar(200), " +
				"category varchar(200), " +
				"image_url varchar(200), " +
				"last_sync_time integer" +
				")"
		);
		//@formatter:on

		//@formatter:off
		db.execSQL(
				"create table subscription_item (" +
				"_id integer primary key autoincrement, " +
				"subscription_id integer, " +
				"title varchar(200)," +
				"guid_id varchar(200), " +
				"link_url varchar(200), " +
				"thumbnail_url varchar(200), " +
				"item_desc varchar(400), " +
				"media_url varchar(200), " +
				"file_location varchar(200))"
		);
		//@formatter:on
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public long insertSubscription(Subscription subscription) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_SUBSCRIPTION_AUTHOR, subscription.getAuthor());
		contentValues.put(COLUMN_SUBSCRIPTION_CATEGORY, subscription.getCategory());
		contentValues.put(COLUMN_SUBSCRIPTION_FEED_URL, subscription.getFeedUrl());
		contentValues.put(COLUMN_SUBSCRIPTION_IMAGE_URL, subscription.getImageUrl());
		contentValues.put(COLUMN_SUBSCRIPTION_SUBTITLE, subscription.getSubTitle());
		contentValues.put(COLUMN_SUBSCRIPTION_SUMMARY, subscription.getSummary());
		contentValues.put(COLUMN_SUBSCRIPTION_TITLE, subscription.getTitle());
		Long lastSyncTime = (subscription.getLastSyncDate() == null) ? null : subscription.getLastSyncDate().getTime();
		contentValues.put(COLUMN_SUBSCRIPTION_LAST_SYNC_TIME, lastSyncTime);
		return getWritableDatabase().insert(TABLE_SUBSCRIPTION, null, contentValues);
	}

	public void insertSubscriptionItems(long subscriptionId, List<SubscriptionItem> subscriptionItems) {
		for (SubscriptionItem currSubscriptionItem : subscriptionItems) {
			insertSubscriptionItem(subscriptionId, currSubscriptionItem);
		}
	}

	public void insertSubscriptionItem(long subscriptionId, SubscriptionItem currSubscriptionItem) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_SUBSCRIPTION_ITEM_FILE_LOCATION, currSubscriptionItem.getFileLocation());
		contentValues.put(COLUMN_SUBSCRIPTION_ITEM_GUID_ID, currSubscriptionItem.getGuidId());
		contentValues.put(COLUMN_SUBSCRIPTION_ITEM_ITEM_DESC, currSubscriptionItem.getItemDesc());
		contentValues.put(COLUMN_SUBSCRIPTION_ITEM_LINK_URL, currSubscriptionItem.getLinkUrl());
		contentValues.put(COLUMN_SUBSCRIPTION_ITEM_MEDIA_URL, currSubscriptionItem.getMediaUrl());
		contentValues.put(COLUMN_SUBSCRIPTION_ITEM_SUBSCRIPTION_ID, subscriptionId);
		contentValues.put(COLUMN_SUBSCRIPTION_ITEM_THUMBNAIL_URL, currSubscriptionItem.getThumbnailUrl());
		contentValues.put(COLUMN_SUBSCRIPTION_ITEM_TITLE, currSubscriptionItem.getTitle());

		getWritableDatabase().insert(TABLE_SUBSCRIPTION_ITEM, null, contentValues);
	}

	public void deleteAll() {
		getWritableDatabase().delete(TABLE_SUBSCRIPTION, null, null);
		getWritableDatabase().delete(TABLE_SUBSCRIPTION_ITEM, null, null);
	}

	public void insertTestSubscriptions() {
		long subscriptionId = insertSubscription(createSubscription(0));
		insertSubscriptionItem(subscriptionId, toSubscriptionItem(0));
		insertSubscriptionItem(subscriptionId, toSubscriptionItem(1));
		insertSubscriptionItem(subscriptionId, toSubscriptionItem(2));
	}

	private SubscriptionItem toSubscriptionItem(int i) {
		SubscriptionItem item = new SubscriptionItem();
		item.setFileLocation(null);
		item.setGuidId(null);
		item.setItemDesc("Item Description " + i);
		item.setLinkUrl("someUrl " + i);
		item.setMediaUrl("someMediaUrl " + i);
		item.setThumbnailUrl("someUrl" + i);
		item.setTitle("Very Awesome..." + i);

		return item;
	}

	private Subscription createSubscription(int i) {
		Subscription subscription = new Subscription();
		subscription.setTitle("Title #" + i);
		subscription.setSubTitle("A subtitle " + i);
		subscription.setAuthor("Carlus Henry");
		subscription.setCategory("Tech");
		subscription.setSummary("This is just something silly");
		subscription.setFeedUrl("http://feeds.feedburner.com/javaposse");
		return subscription;
	}

	public SubscriptionCursor querySubscription() {
		Cursor cursor = getReadableDatabase().query(TABLE_SUBSCRIPTION, null, null, null, null, null, null);
		return new SubscriptionCursor(cursor);
	}

	public SubscriptionCursor findSubscriptionById(long id) {
		//@formatter:off
		Cursor cursor = getReadableDatabase().query(
				TABLE_SUBSCRIPTION, 
				null, 
				COLUMN_SUBSCRIPTION_ID + " = ?", 
				new String[] { String.valueOf(id) }, 
				null, 
				null, 
				null, 
				"1");
		//@formatter:on
		return new SubscriptionCursor(cursor);
	}

	public SubscriptionItemCursor findSubscriptionItemsBySubscriptionId(long subscriptionId) {
		//@formatter:off
		Cursor cursor = getReadableDatabase().query(
				TABLE_SUBSCRIPTION_ITEM, 
				null, 
				COLUMN_SUBSCRIPTION_ITEM_SUBSCRIPTION_ID + " = ?", 
				new String[] { String.valueOf(subscriptionId) }, 
				null, 
				null, 
				null, 
				null);
		//@formatter:on
		return new SubscriptionItemCursor(cursor);
	}

	public static class SubscriptionCursor extends CursorWrapper {
		public SubscriptionCursor(Cursor cursor) {
			super(cursor);
		}

		public Subscription getSubscription() {

			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}

			Subscription subscription = new Subscription();
			long subscriptioinId = getLong(getColumnIndex(COLUMN_SUBSCRIPTION_ID));
			String author = getString(getColumnIndex(COLUMN_SUBSCRIPTION_AUTHOR));
			String category = getString(getColumnIndex(COLUMN_SUBSCRIPTION_CATEGORY));
			String feedUrl = getString(getColumnIndex(COLUMN_SUBSCRIPTION_FEED_URL));
			String imageUrl = getString(getColumnIndex(COLUMN_SUBSCRIPTION_IMAGE_URL));
			String subTitle = getString(getColumnIndex(COLUMN_SUBSCRIPTION_SUBTITLE));
			String summary = getString(getColumnIndex(COLUMN_SUBSCRIPTION_SUMMARY));
			String title = getString(getColumnIndex(COLUMN_SUBSCRIPTION_TITLE));

			subscription.setId(subscriptioinId);
			subscription.setAuthor(author);
			subscription.setCategory(category);
			subscription.setFeedUrl(feedUrl);
			subscription.setImageUrl(imageUrl);
			subscription.setSubTitle(subTitle);
			subscription.setSummary(summary);
			subscription.setTitle(title);

			return subscription;

		}
	}

	public static class SubscriptionItemCursor extends CursorWrapper {
		public SubscriptionItemCursor(Cursor cursor) {
			super(cursor);
		}

		public SubscriptionItem getSubscriptionItem() {

			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}

			SubscriptionItem si = new SubscriptionItem();
			// public static final String COLUMN_SUBSCRIPTION_ITEM_ID = "_id";
			// public static final String
			// COLUMN_SUBSCRIPTION_ITEM_SUBSCRIPTION_ID = "subscription_id";
			// public static final String COLUMN_SUBSCRIPTION_ITEM_TITLE =
			// "title";
			// public static final String COLUMN_SUBSCRIPTION_ITEM_GUID_ID =
			// "guid_id";
			// public static final String COLUMN_SUBSCRIPTION_ITEM_LINK_URL =
			// "link_url";
			// public static final String COLUMN_SUBSCRIPTION_ITEM_THUMBNAIL_URL
			// = "thumbnail_url";
			// public static final String COLUMN_SUBSCRIPTION_ITEM_ITEM_DESC =
			// "item_desc";
			// public static final String COLUMN_SUBSCRIPTION_ITEM_MEDIA_URL =
			// "media_url";
			// public static final String COLUMN_SUBSCRIPTION_ITEM_FILE_LOCATION
			// = "file_location";

			long id = getLong(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_ID));
			String fileLocation = getString(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_FILE_LOCATION));
			String guidId = getString(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_GUID_ID));
			long subscriptionId = getLong(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_SUBSCRIPTION_ID));
			String itemDesc = getString(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_ITEM_DESC));
			String linkUrl = getString(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_LINK_URL));
			String mediaUrl = getString(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_MEDIA_URL));
			String thumbnailUrl = getString(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_THUMBNAIL_URL));
			String title = getString(getColumnIndex(COLUMN_SUBSCRIPTION_ITEM_TITLE));

			si.setId(id);
			si.setFileLocation(fileLocation);
			si.setGuidId(guidId);
			si.setItemDesc(itemDesc);
			si.setLinkUrl(linkUrl);
			si.setMediaUrl(mediaUrl);
			si.setSubscriptionId(subscriptionId);
			si.setThumbnailUrl(thumbnailUrl);
			si.setTitle(title);

			return si;

		}
	}
}
