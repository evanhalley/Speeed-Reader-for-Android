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
package com.emuneee.speeedreader.googlereader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emuneee.speeedreader.HttpUtils;
import com.emuneee.speeedreader.googlereader.Collection.Type;

import android.util.Log;

/**
 * Handles interaction with Google Reader web service.
 * @author Evan
 *
 */
public class ArticleUtils {
	private final static String TAG = "ArticleUtils";
	
	private final static String URI_UNREAD_COUNT = 
			"http://www.google.com/reader/api/0/unread-count?output=json";
	private final static String URI_SUBSCRIPTION_LIST = 
			"http://www.google.com/reader/api/0/subscription/list?output=json";
	private final static String URI_API_STREAM = 
			"http://www.google.com/reader/api/0/stream/contents/user/-";
	private final static String URI_STATE_LIST = 
			URI_API_STREAM + "/state/com.google/";
	private final static String URI_LABEL_LIST = 
			URI_API_STREAM + "/label/";
	
	private final static String PARAM_COUNT = "n";
	private final static String PARAM_CONTINUATION = "c";
	
	private final static String JSON_USER = "user";
	private final static String JSON_FEED = "feed";
	private final static String JSON_COUNT = "count";
	private final static String JSON_CONTINUATION = "continuation";
	private final static String JSON_ID = "id";
	private final static String JSON_TITLE = "title";
	private final static String JSON_UPDATED = "updated";
	private final static String JSON_ITEMS = "items";
	private final static String JSON_CATEGORIES = "categories";
	private final static String JSON_PUBLISHED = "published";
	private final static String JSON_ALTERNATE = "alternate";
	private final static String JSON_HREF = "href";
	private final static String JSON_CONTENT = "content";
	private final static String JSON_SUMMARY = "summary";
	private final static String JSON_AUTHOR = "author";
	private final static String JSON_ORIGIN = "origin";
	private final static String JSON_HTML_URL = "htmlUrl";
	private final static String JSON_UNREAD_COUNTS = "unreadcounts";
	private final static String JSON_SUBSCRIPTIONS = "subscriptions";
	
	private final static String READ = "state/com.google/read";
	private final static String STARRED = "state/com.google/starred";
	
	public final static String STATE_READING_LIST = "reading-list";
	public final static String STATE_STARRED = "starred";
	
	/**
	 * Returns a list of collections containing the unread counts
	 * @param token Google Reader API token
	 * @return list of collections containing the unread counts
	 * @throws JSONException
	 * @throws IOException
	 */
	public static CollectionList getUnreadCount(String token) 
			throws JSONException, IOException {
		final CollectionList collectionList;
		JSONObject jsonObject;
		JSONArray jsonArray;
		Collection collection;
		JSONObject jsonCollection;
		int length;
		String id;
		
		collectionList = new CollectionList();
		/*
		 *  Make a call and get the unread count json from Google Reader.  We 
		 *  do this to build an initial list of collections
		 */
		jsonObject = new JSONObject(
				HttpUtils.executeHttpRequest(URI_UNREAD_COUNT, token));
		jsonArray = jsonObject.optJSONArray(JSON_UNREAD_COUNTS);
		if(jsonArray != null) {
			length = jsonArray.length();
			for(int i = 0; i < length; i++) {
				jsonCollection = jsonArray.getJSONObject(i);
				collection = new Collection();
				collection.setId(jsonCollection.optString(JSON_ID));
				collection.setUnreadCount(jsonCollection.optInt(JSON_COUNT));
				if(collection.getId().startsWith(JSON_FEED)) {
					collection.setType(Type.Subscription);
				} else if (collection.getId().startsWith(JSON_USER)) {
					collection.setTitle(
							parseCollectionTitle(collection.getId()));
					if(collection.getId().contains(URI_STATE_LIST)) {
						collection.setType(Type.State);
					} else {
						collection.setType(Type.Label);
					}
				} 
				collectionList.addCollection(collection.getId(), collection);
			}
		}
		
		/* 
		 * Make a call and get the subscription list json from Google Reader. We loop through
		 * each subscription and populate the correct title for Subscription types
		 */
		jsonObject = new JSONObject(
				HttpUtils.executeHttpRequest(URI_SUBSCRIPTION_LIST, token));
		jsonArray = jsonObject.optJSONArray(JSON_SUBSCRIPTIONS);
		if(jsonArray != null) {
			length = jsonArray.length();
			for(int i = 0; i < length; i++) {
				jsonCollection = jsonArray.getJSONObject(i);
				id = jsonCollection.optString(JSON_ID);
				collectionList.getCollection(id).setTitle(
						jsonCollection.optString(JSON_TITLE));
			}
		}
		
		return collectionList;
	}
	
	/**
	 * Returns the requested number of articles for a particular label
	 * @param token Google Reader API token
	 * @param numberOfArticles number of articles to return
	 * @param label label to retrieve the articles from
	 * @return article list containing the articles requested
	 * @throws JSONException
	 */
	public static Collection getCollectionByState(String token, int numberOfArticles, String state,
			String continuation) throws JSONException {
		final HashMap<String, String> params;
		String url;
		Collection collection = null;
		
		params = new HashMap<String, String>(2);
		params.put(PARAM_COUNT, String.valueOf(numberOfArticles));
		if(continuation != null) params.put(PARAM_CONTINUATION, continuation);
		url = URI_STATE_LIST + state;
		url = HttpUtils.addQueryStrings(url, params);
		try {
			collection = parseArticles(
					HttpUtils.executeHttpRequest(url, token));
			collection.setType(Type.State);
		} catch(IOException e) {
			Log.w(TAG, e.getMessage());
		}
		
		return collection;
	}
	
	/**
	 * Returns the requested number of articles for a particular label
	 * @param token Google Reader API token
	 * @param numberOfArticles number of articles to return
	 * @param label label to retrieve the articles from
	 * @return article list containing the articles requested
	 * @throws JSONException
	 */
	public static Collection getCollectionByState(String token, int numberOfArticles, String state) 
			throws JSONException {
		return getCollectionByState(token, numberOfArticles, state, null); 
	}
	
	/**
	 * Returns the requested number of articles for a particular label
	 * @param token Google Reader API token
	 * @param numberOfArticles number of articles to return
	 * @param label label to retrieve the articles from
	 * @return article list containing the articles requested
	 * @throws JSONException
	 */
	public static Collection getCollectionByLabel(String token, int numberOfArticles, String label,
			String continuation) throws JSONException {
		final HashMap<String, String> params;
		String url;
		Collection collection = null;
		
		params = new HashMap<String, String>(2);
		params.put(PARAM_COUNT, String.valueOf(numberOfArticles));
		if(continuation != null) params.put(PARAM_CONTINUATION, continuation);
		url = URI_LABEL_LIST + label;
		url = HttpUtils.addQueryStrings(url, params);
		try {
			collection = parseArticles(
					HttpUtils.executeHttpRequest(url, token));
			collection.setType(Type.Label);
		} catch(IOException e) {
			Log.w(TAG, e.getMessage());
		}
		
		return collection;
	}
	
	/**
	 * Returns the requested number of articles for a particular label
	 * @param token Google Reader API token
	 * @param numberOfArticles number of articles to return
	 * @param label label to retrieve the articles from
	 * @return article list containing the articles requested
	 * @throws JSONException
	 */
	public static Collection getCollectionByLabel(String token, int numberOfArticles, String label) 
			throws JSONException {
		return getCollectionByLabel(token, numberOfArticles, label, null);
	}
	
	/**
	 * Parses Google Reader reading-list JSON and returns a ReadingList object
	 * @param articleJson reading-list JSON to parse
	 * @return ReadingList object
	 * @throws JSONException
	 */
	public static Collection parseArticles(String articleJson) 
			throws JSONException {
		final Map<String, Article> articles;
		final JSONObject jsonReadingList;
		final JSONArray jsonItems;
		final Collection readingList;
		int length;
		Article article;
		JSONObject jsonItem;
		JSONObject temp;
		
		readingList = new Collection();
		jsonReadingList = new JSONObject(articleJson);
		// lets get the meta data for the reading list
		readingList.setContinuation(jsonReadingList.optString(JSON_CONTINUATION));
		readingList.setTitle(jsonReadingList.optString(JSON_TITLE));
		readingList.setUpdated(jsonReadingList.optLong(JSON_UPDATED));
		readingList.setId(jsonReadingList.optString(JSON_ID));
		/*
		 *  iterate through each item in the reading and parse out the
		 */
		jsonItems = jsonReadingList.getJSONArray(JSON_ITEMS);
		length = jsonItems.length();
		articles = new Hashtable<String, Article>(length);
		for(int i = 0; i < length; i++) {
			jsonItem = jsonItems.getJSONObject(i);
			article = new Article();
			temp = jsonItem.optJSONObject(JSON_ORIGIN);
			article.setId(jsonItem.optString(JSON_ID));
			article.setAuthor(jsonItem.optString(JSON_AUTHOR));
			article.setPublishedTimeStamp(jsonItem.optLong(JSON_PUBLISHED));
			article.setTitle(jsonItem.optString(JSON_TITLE));
			//article.setCategories(getCategories(jsonItem.getJSONArray(JSON_CATEGORIES)));
			configureCategories(article, jsonItem.getJSONArray(JSON_CATEGORIES));
			article.setArticleLink(jsonItem.optJSONArray(
					JSON_ALTERNATE).getJSONObject(0).getString(JSON_HREF));
			article.setContent(jsonItem.optJSONObject(
					JSON_SUMMARY).getString(JSON_CONTENT));
			article.setSiteUrl(temp.getString(JSON_HTML_URL));
			article.setSiteTitle(temp.getString(JSON_TITLE));
			articles.put(article.getId(), article);
		}
		readingList.setArticles(articles);
		return readingList;
	}
	
	public static void configureCategories(Article article, JSONArray jsonArray) 
			throws JSONException {
		int length;
		String category;
		ArrayList<String> categories;
		
		categories = new ArrayList<String>();
		length = jsonArray.length();
		for(int i = 0; i < length; i++) {
			category = jsonArray.getString(i);
			if(category.contains(READ) && 
					!category.contains(STATE_READING_LIST)) article.setIsRead(true);
			else if(category.contains(STARRED)) article.setIsStarred(true);
			else categories.add(category);
		}
		article.setCategories(categories);
	}
	
	/**
	 * Parses through the categories JSON array 
	 * @param jsonCategories JSON containing the article categories
	 * @return ArrayList of categories
	 * @throws JSONException
	 */
	public static ArrayList<String> getCategories(JSONArray jsonCategories) 
			throws JSONException {
		final int length;
		final ArrayList<String> categories;
		
		length = jsonCategories.length();
		categories = new ArrayList<String>(length);
		for(int i = 0; i < jsonCategories.length(); i++) {
			categories.add(jsonCategories.getString(i));
		}
		
		return categories;
	}
	
	public static String parseCollectionTitle(String collectionId) {
		final String tempArr[];
		
		tempArr = collectionId.split("/");
		return tempArr[tempArr.length - 1];
	}

}
