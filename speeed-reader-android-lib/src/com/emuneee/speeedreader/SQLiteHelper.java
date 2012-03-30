/*
 * Copyright 2012 emuneee apps
 * http://emuneee.com/apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.emuneee.speeedreader;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.emuneee.speeedreader.googlereader.Article;
import com.emuneee.speeedreader.googlereader.Collection;
import com.emuneee.speeedreader.googlereader.Collection.Type;
import com.emuneee.speeedreader.googlereader.CollectionList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Handles database transactions
 * @author Evan
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	private final static String TAG = "DatabaseUtils";
	private final static String DATABASE_NAME = "article.db";
	private final static int DATABASE_VERSION = 1;
	private static SQLiteHelper ref;
	
	private final static String TABLE_ARTICLES = "articles";
	private final static String COL_ID = "id";
	private final static String COL_TITLE = "title";
	private final static String COL_PUBLISHED_TIMESTAMP = "publishedTimestamp";
	private final static String COL_ARTICLE_LINK = "articleLink";
	private final static String COL_CONTENT = "content";
	private final static String COL_AUTHOR = "author";
	private final static String COL_LIKES = "likes";
	private final static String COL_SITE_URL = "siteUrl";
	private final static String COL_SITE_TITLE = "siteTitle";
	private final static String COL_CATEGORIES = "categories";
	private final static String COL_IS_READ = "isRead";
	private final static String COL_IS_STARRED = "isStarred";
	
	private final static String COL_COLLECTION_TYPE = "collectionType";
	private final static String COL_UNREAD_COUNT = "unreadCount";
	private final static String COL_CONTINUATION = "continuation";
	private final static String COL_UPDATED = "updated";
	private final static String TABLE_COLLECTIONS = "collections";
	
	private final static String CREATE_COLLECTIONS_TABLE =  "CREATE TABLE " +
			TABLE_COLLECTIONS + "( " + 
			COL_ID + " TEXT PRIMARY KEY, " + 
			COL_TITLE + " TEXT NOT NULL, " + 
			COL_UNREAD_COUNT + " INTEGER NOT NULL, " +
			COL_UPDATED + " INTEGER NOT NULL, " + 
			COL_CONTINUATION + " STRING NOT NULL, " + 
			COL_COLLECTION_TYPE + " INTEGER NOT NULL)";
	
	private final static String CREATE_ARTICLES_TABLE = "CREATE TABLE " +
			TABLE_ARTICLES + "( " + 
			COL_ID + " TEXT PRIMARY KEY, " + 
			COL_TITLE + " TEXT NOT NULL, " + 
			COL_PUBLISHED_TIMESTAMP + " INTEGER NOT NULL, " + 
			COL_ARTICLE_LINK + " TEXT NOT NULL, " + 
			COL_CONTENT + " TEXT NOT NULL, " + 
			COL_AUTHOR + " TEXT NOT NULL, " + 
			COL_LIKES + " INTEGER NOT NULL, " + 
			COL_SITE_URL + " TEXT NOT NULL, " +
			COL_CATEGORIES + " TEXT NOT NULL, " +
			COL_IS_STARRED + " INTEGER NOT NULL, " +
			COL_IS_READ + " INTEGER NOT NULL, " +
			COL_SITE_TITLE + " TEXT NOT NULL)";
	
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * Returns instance of the singleton SQLiteHelper.  If it doesn't exist,
	 * it will be instantiated
	 * @param context
	 * @return
	 */
	public static SQLiteHelper getSQLiteHelper(Context context) {
		if(ref == null) {
			ref = new SQLiteHelper(context);
		}
		return ref;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.beginTransaction();
		try {
			database.execSQL(CREATE_ARTICLES_TABLE);
			database.execSQL(CREATE_COLLECTIONS_TABLE);
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.w(TAG, e.getMessage());
		} finally {
			database.endTransaction();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	/**
	 * Clears the articles and collections tables
	 */
	public void clearTables() {
		final SQLiteDatabase database;
		
		database = ref.getWritableDatabase();
		database.beginTransaction();
		try {
			database.delete(TABLE_ARTICLES, null, null);
			database.delete(TABLE_COLLECTIONS, null, null);
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.w(TAG, e.getMessage());
		} finally {
			database.endTransaction();
		}
	}
	
	/**
	 * Deletes the articles with the IDs specified from the database
	 * @param ids
	 * @return
	 */
	public boolean deleteArticles(List<String> ids) {
		final SQLiteDatabase database;
		StringBuilder where;
		int record;
		boolean result = true;
		
		database = ref.getWritableDatabase();
		database.beginTransaction();
		try {
			for(String id : ids) {
				where = new StringBuilder(COL_ID).append( " = ");
				where.append("'").append(id).append("'");
				record = database.delete(TABLE_ARTICLES, where.toString(), null);
				if(result != false && record == -1) result = false;
			}
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			result = false;
			Log.w(TAG, e.getMessage());
		} finally {
			database.endTransaction();
		}
		return result;
	}
	
	/**
	 * Deletes the collection with the specified ID from the database
	 * @param collectionId
	 * @return
	 */
	public boolean deleteCollection(String collectionId) {
		final SQLiteDatabase database;
		final StringBuilder where;
		boolean result;
		
		where = new StringBuilder(COL_ID).append( " = ");
		where.append("'").append(collectionId).append("'");
		database = ref.getWritableDatabase();
		database.beginTransaction();
		try {
			result = database.delete(TABLE_COLLECTIONS, where.toString(), null) > 0;
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			result = false;
			Log.w(TAG, e.getMessage());
		} finally {
			database.endTransaction();
		}
		return result;
	}
	
	/**
	 * Returns a list of starred articles
	 * @param limit max number of articles to return
	 * @return list of starred articles
	 */
	public Map<String, Article> getStarredArticles(int limit) {
		final SQLiteDatabase database;
		final StringBuilder where;
		final StringBuilder orderBy;
		final Cursor cursor;
		Map<String, Article> articles = null;
		
		orderBy = new StringBuilder().append(COL_PUBLISHED_TIMESTAMP);
		orderBy.append(" DESC");
		where = new StringBuilder().append(COL_IS_STARRED);
		where.append(" = ").append(1);
		database = ref.getReadableDatabase();
		database.beginTransaction();
		try {
			cursor = database.query(TABLE_ARTICLES, null, 
					where.toString(), null, null, null, orderBy.toString(), String.valueOf(limit));
			articles = cursorToArticles(cursor);
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.w(TAG, e.getMessage());
		} finally {
			database.endTransaction();
		}
		return articles;
	}
	
	/**
	 * Returns a list of articles by category
	 * @param category category specified
	 * @param limit max number of articles to return
	 * @return list of articles by category
	 */
	public Map<String, Article> getArticlesByCategory(String category, int limit) {
		final SQLiteDatabase database;
		final StringBuilder where;
		final StringBuilder orderBy;
		final Cursor cursor;
		Map<String, Article> articles = null;
		
		orderBy = new StringBuilder().append(COL_PUBLISHED_TIMESTAMP);
		orderBy.append(" DESC");
		where = new StringBuilder().append(COL_CATEGORIES);
		where.append(" LIKE '%").append(category).append("%'");
		database = ref.getReadableDatabase();
		database.beginTransaction();
		try {
			cursor = database.query(TABLE_ARTICLES, null, 
					where.toString(), null, null, null, orderBy.toString(), String.valueOf(limit));
			articles = cursorToArticles(cursor);
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.w(TAG, e.getMessage());
		} finally {
			database.endTransaction();
		}
		return articles;
	}
	
	/**
	 * Returns the collection with the specified ID
	 * @param collectionId id of the collection desired
	 * @return collection with the specified ID
	 */
	public Collection getCollectionById(String collectionId) {
		final SQLiteDatabase database;
		final StringBuilder where;
		final Cursor cursor;
		Collection collection = null;
		
		where = new StringBuilder().append(COL_ID);
		where.append(" = '").append(collectionId).append("'");
		database = ref.getReadableDatabase();
		database.beginTransaction();
		try {
			cursor = database.query(TABLE_ARTICLES, null, 
					where.toString(), null, null, null, null, "1");
			collection = cursorToCollection(cursor);
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.w(TAG, e.getMessage());
		} finally {
			database.endTransaction();
		}
		return collection;
	}
	
	/**
	 * Returns all the collections from the database
	 * @return all the collections from the database
	 */
	public CollectionList getAllCollections() {
		final SQLiteDatabase database;
		final Cursor cursor;
		CollectionList collectionList = null;
		
		database = ref.getReadableDatabase();
		database.beginTransaction();
		try {
			cursor = database.query(TABLE_ARTICLES, null, 
					null, null, null, null, null, null);
			collectionList = cursorToCollectionList(cursor);
			database.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.w(TAG, e.getMessage());
		} finally {
			database.endTransaction();
		}
		return collectionList;
	}
	
	/**
	 * Updates a collection in the database
	 * @param collection
	 * @return
	 */
	public boolean updateCollection(Collection collection) {
		final SQLiteDatabase database;
		final ContentValues contentValues;
		final StringBuilder whereClause;
		boolean result;
		int rows;
		
		whereClause = new StringBuilder(COL_ID);
		whereClause.append("=").append("'").append(collection.getId()).append("'");
		contentValues = new ContentValues();
		database = ref.getWritableDatabase();
		database.beginTransaction();
		try {
			contentValues.put(COL_ID, collection.getId());
			contentValues.put(COL_TITLE, collection.getTitle());
			contentValues.put(COL_UNREAD_COUNT, collection.getUnreadCount());
			contentValues.put(COL_COLLECTION_TYPE, collection.getType().ordinal());
			contentValues.put(COL_CONTINUATION, collection.getContinuation());
			contentValues.put(COL_UPDATED, collection.getUpdated());
			rows = database.update(TABLE_COLLECTIONS, contentValues, whereClause.toString(), null);
			result = rows > 0;
			database.setTransactionSuccessful();
		}catch (SQLException e) {
			Log.w(TAG, e.getMessage());
			result = false;
		} finally {
			database.endTransaction();
		}
		
		return result;
	}
	
	/**
	 * Update a list of articles in the database
	 * @param article
	 * @return
	 */
	public boolean updateArticles(Map<String, Article> articles) {
		final SQLiteDatabase database;
		final ContentValues contentValues;
		StringBuilder whereClause;
		boolean result = true;
		int rows;
		
		contentValues = new ContentValues();
		database = ref.getWritableDatabase();
		database.beginTransaction();
		for(Article article : articles.values()) {
			try {
				whereClause = new StringBuilder(COL_ID);
				whereClause.append("=").append("'").append(article.getId()).append("'");
				contentValues.put(COL_ID, article.getId());
				contentValues.put(COL_CATEGORIES, article.getCategoriesAsString());
				contentValues.put(COL_PUBLISHED_TIMESTAMP, article.getPublishedTimeStamp());
				contentValues.put(COL_ARTICLE_LINK, article.getArticleLink());
				contentValues.put(COL_CONTENT, article.getContent());
				contentValues.put(COL_AUTHOR, article.getAuthor());
				contentValues.put(COL_LIKES, article.getLikes());
				contentValues.put(COL_SITE_URL, article.getSiteUrl());
				contentValues.put(COL_SITE_TITLE, article.getSiteTitle());
				contentValues.put(COL_TITLE, article.getTitle());
				contentValues.put(COL_IS_READ, article.isIsRead());
				contentValues.put(COL_IS_STARRED, article.isIsStarred());
				rows = database.update(TABLE_ARTICLES, contentValues, whereClause.toString(), null);
				if(rows == -1) {
					result = false;
				}
				database.setTransactionSuccessful();
			}catch (SQLException e) {
				Log.w(TAG, e.getMessage());
				result = false;
			} finally {
				database.endTransaction();
			}
		}
		return result;
	}
	
	/**
	 * Updates an article in the database
	 * @param article
	 * @return
	 */
	public boolean updateArticle(Article article) {
		final SQLiteDatabase database;
		final ContentValues contentValues;
		final StringBuilder whereClause;
		boolean result;
		int rows;
		
		whereClause = new StringBuilder(COL_ID);
		whereClause.append("=").append("'").append(article.getId()).append("'");
		contentValues = new ContentValues();
		database = ref.getWritableDatabase();
		database.beginTransaction();
		try {
			contentValues.put(COL_ID, article.getId());
			contentValues.put(COL_CATEGORIES, article.getCategoriesAsString());
			contentValues.put(COL_PUBLISHED_TIMESTAMP, article.getPublishedTimeStamp());
			contentValues.put(COL_ARTICLE_LINK, article.getArticleLink());
			contentValues.put(COL_CONTENT, article.getContent());
			contentValues.put(COL_AUTHOR, article.getAuthor());
			contentValues.put(COL_LIKES, article.getLikes());
			contentValues.put(COL_SITE_URL, article.getSiteUrl());
			contentValues.put(COL_SITE_TITLE, article.getSiteTitle());
			contentValues.put(COL_TITLE, article.getTitle());
			contentValues.put(COL_IS_READ, article.isIsRead());
			contentValues.put(COL_IS_STARRED, article.isIsStarred());
			rows = database.update(TABLE_ARTICLES, contentValues, whereClause.toString(), null);
			result = rows > 0;
			database.setTransactionSuccessful();
		}catch (SQLException e) {
			Log.w(TAG, e.getMessage());
			result = false;
		} finally {
			database.endTransaction();
		}
		
		return result;
	}
	
	public boolean insertCollectionList(CollectionList collectionList) {
		final SQLiteDatabase database;
		final ContentValues contentValues;
		boolean result = true;
		long row;
		
		contentValues = new ContentValues();
		database = ref.getWritableDatabase();
		database.beginTransaction();
		try {
			for(Collection collection : collectionList.getCollectionList().values()) {
				contentValues.put(COL_ID, collection.getId());
				contentValues.put(COL_TITLE, collection.getTitle());
				contentValues.put(COL_UNREAD_COUNT, collection.getUnreadCount());
				contentValues.put(COL_COLLECTION_TYPE, collection.getType().ordinal());
				contentValues.put(COL_CONTINUATION, collection.getContinuation());
				contentValues.put(COL_UPDATED, collection.getUpdated());
				row = database.insert(TABLE_COLLECTIONS, null, contentValues);
				insertArticles(collection.getArticles());
				if(row < 0) {
					result = false;
				}
			}
			database.setTransactionSuccessful();
		}catch (SQLException e) {
			Log.w(TAG, e.getMessage());
			result = false;
		} finally {
			database.endTransaction();
		}

		
		return result;
	}
	
	/**
	 * Inserts a collection and its articles into the database
	 * @param collection
	 * @return
	 */
	public boolean insertCollection(Collection collection) {
		final SQLiteDatabase database;
		final ContentValues contentValues;
		boolean result;
		
		contentValues = new ContentValues();
		database = ref.getWritableDatabase();
		database.beginTransaction();
		try {
			contentValues.put(COL_ID, collection.getId());
			contentValues.put(COL_TITLE, collection.getTitle());
			contentValues.put(COL_UNREAD_COUNT, collection.getUnreadCount());
			contentValues.put(COL_COLLECTION_TYPE, collection.getType().ordinal());
			contentValues.put(COL_CONTINUATION, collection.getContinuation());
			contentValues.put(COL_UPDATED, collection.getUpdated());
			database.insert(TABLE_COLLECTIONS, null, contentValues);
			result = true;
			database.setTransactionSuccessful();
		}catch (SQLException e) {
			Log.w(TAG, e.getMessage());
			result = false;
		} finally {
			database.endTransaction();
		}
		result = insertArticles(collection.getArticles());
		
		return result;
	}
	
	/**
	 * Inserts article list into the database
	 * @param articleList articles to insert
	 * @return result of the insert operation
	 */
	public boolean insertArticles(Map<String, Article> articles) {
		final SQLiteDatabase database;
		ContentValues contentValues;
		boolean result = true;
		long record;
		
		database = ref.getWritableDatabase();
		database.beginTransaction();
		try {
			for(Article article : articles.values()) {
				contentValues = new ContentValues();
				contentValues.put(COL_ID, article.getId());
				contentValues.put(COL_CATEGORIES, article.getCategoriesAsString());
				contentValues.put(COL_PUBLISHED_TIMESTAMP, article.getPublishedTimeStamp());
				contentValues.put(COL_ARTICLE_LINK, article.getArticleLink());
				contentValues.put(COL_CONTENT, article.getContent());
				contentValues.put(COL_AUTHOR, article.getAuthor());
				contentValues.put(COL_LIKES, article.getLikes());
				contentValues.put(COL_SITE_URL, article.getSiteUrl());
				contentValues.put(COL_SITE_TITLE, article.getSiteTitle());
				contentValues.put(COL_TITLE, article.getTitle());
				contentValues.put(COL_IS_READ, article.isIsRead());
				contentValues.put(COL_IS_STARRED, article.isIsStarred());
				record = database.insert(TABLE_ARTICLES, null, contentValues);
				if(result != false && record == -1) result = false;
			}
			database.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.w(TAG, e.getMessage());
			result = false;
		} finally {
			database.endTransaction();
		}
		return result;
	}
	
	/**
	 * Parses out article objects from the Cursor
	 * @param cursor
	 * @return
	 */
	public static Map<String, Article> cursorToArticles(Cursor cursor) {
		final Map<String, Article> articles;
		Article article;
		
		articles = new Hashtable<String, Article>();
		while(cursor.moveToNext()) {
			article = new Article();
			article.setId(cursor.getString(cursor.getColumnIndex(COL_ID)));
			article.setCategoriesFromString(cursor.getString(cursor.getColumnIndex(COL_CATEGORIES)));
			article.setPublishedTimeStamp(cursor.getLong(cursor.getColumnIndex(COL_PUBLISHED_TIMESTAMP)));
			article.setArticleLink(cursor.getString(cursor.getColumnIndex(COL_ARTICLE_LINK)));
			article.setContent(cursor.getString(cursor.getColumnIndex(COL_CONTENT)));
			article.setAuthor(cursor.getString(cursor.getColumnIndex(COL_AUTHOR)));
			article.setLikes(cursor.getInt(cursor.getColumnIndex(COL_LIKES)));
			article.setSiteUrl(cursor.getString(cursor.getColumnIndex(COL_SITE_URL)));
			article.setSiteTitle(cursor.getString(cursor.getColumnIndex(COL_SITE_TITLE)));
			article.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
			article.setIsRead(cursor.getInt(cursor.getColumnIndex(COL_IS_READ)) == 1);
			article.setIsStarred(cursor.getInt(cursor.getColumnIndex(COL_IS_STARRED)) == 1);
			articles.put(article.getId(), article);
		}
		cursor.close();
		return articles;
	}
	
	/**
	 * Parses a collection list from the cursor
	 * @param cursor
	 * @return
	 */
	public static CollectionList cursorToCollectionList(Cursor cursor) {
		final CollectionList collectionList;
		Collection collection;
		
		collectionList = new CollectionList();
		while(cursor.moveToFirst()) {
			collection = new Collection();
			collection.setId(cursor.getString(cursor.getColumnIndex(COL_ID)));
			collection.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
			collection.setUnreadCount(cursor.getInt(cursor.getColumnIndex(COL_UNREAD_COUNT)));
			collection.setType(Type.values()[cursor.getInt(cursor.getColumnIndex(COL_COLLECTION_TYPE))]);
			collection.setContinuation(cursor.getString(cursor.getColumnIndex(COL_CONTINUATION)));
			collection.setUpdated(cursor.getLong(cursor.getColumnIndex(COL_UPDATED)));
			collectionList.addCollection(collection.getId(), collection);		
		}
		
		return collectionList;
	}
	
	/**
	 * Parses a collection from the Cursor
	 * @param cursor
	 * @return
	 */
	public static Collection cursorToCollection(Cursor cursor) {
		Collection collection = null;
		
		while(cursor.moveToFirst()) {
			collection = new Collection();
			collection.setId(cursor.getString(cursor.getColumnIndex(COL_ID)));
			collection.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
			collection.setUnreadCount(cursor.getInt(cursor.getColumnIndex(COL_UNREAD_COUNT)));
			collection.setType(Type.values()[cursor.getInt(cursor.getColumnIndex(COL_COLLECTION_TYPE))]);
			collection.setContinuation(cursor.getString(cursor.getColumnIndex(COL_CONTINUATION)));
			collection.setUpdated(cursor.getLong(cursor.getColumnIndex(COL_UPDATED)));
		}
		return collection;
	}
}
