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
package com.emuneee.speeedreader.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.emuneee.speeedreader.AuthUtils;
import com.emuneee.speeedreader.HttpUtils;
import com.emuneee.speeedreader.activity.LoginActivity;
import com.emuneee.speeedreader.googlereader.Article;
import com.emuneee.speeedreader.googlereader.ArticleUtils;
import com.emuneee.speeedreader.googlereader.Collection;
import com.emuneee.speeedreader.googlereader.CollectionList;

/**
 * @author Evan
 *
 */
public class ArticleUtilsTestCase extends
		ActivityInstrumentationTestCase2<LoginActivity>{
	private final String TAG = getClass().getSimpleName();
	private final String ACCOUNT_NAME = "emuneee@gmail.com";  
	private Activity mActivity;
	private String mToken;
	
	public ArticleUtilsTestCase() {
		super(LoginActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	mActivity = this.getActivity(); 
    	mToken = AuthUtils.getAuthToken(mActivity, ACCOUNT_NAME, "reader");
    }
    
    /**
     * Test the ability to get a reading list from Google Reader
     */
    public void testParseArticles() {
    	HttpURLConnection urlConnection;
    	String response;
    	Collection readingList;
    	int n = 50;
    	
    	try {
			urlConnection = HttpUtils.getUrlConnection(
					"http://www.google.com/reader/api/0/stream/contents/user/-/state/com.google/reading-list?n=" + n, mToken);
			assertNotNull(urlConnection);
			response = HttpUtils.processHttpRequest(urlConnection);
			Log.v(TAG, response);
			assertNotNull(response);
			assertEquals(true, response.length() > 0);
			readingList = ArticleUtils.parseArticles(response);
			assertNotNull(readingList);
			assertNotNull(readingList.getArticles());
			assertEquals(n, readingList.getArticles().size());
		} catch (IOException e) {
			Log.w(TAG, e.getMessage());
			assertTrue(false);
		} catch (JSONException e) {
			Log.w(TAG, e.getMessage());
			assertTrue(false);
		}
    }
    
    /**
     * test we are able to get the reading list from google reader
     */
    public void testGetReadingList() {
    	Collection readingList;
    	int n = 50;
    	
    	try {
			readingList = ArticleUtils.getCollectionByState(mToken, n, "reading-list");
			assertNotNull(readingList);
			assertNotNull(readingList.getArticles());
			assertEquals(n, readingList.getArticles().size());
			readingList = ArticleUtils.getCollectionByState(mToken, n, "reading-list", 
					readingList.getContinuation());
			assertEquals(true, checkArticlesForCategory(readingList, "reading-list"));
			assertNotNull(readingList);
			assertNotNull(readingList.getArticles());
			assertEquals(n, readingList.getArticles().size());
		} catch (JSONException e) {
			Log.w(TAG, e.getMessage());
			assertTrue(false);
		}
    }
    
    /**
     * test we are able to get the reading list from google reader
     */
    public void testGetArticleListForLabel() {
    	Collection readingList;
    	int n = 50;
    	
    	try {
			readingList = ArticleUtils.getCollectionByLabel(mToken, n, "News");
			assertNotNull(readingList);
			assertNotNull(readingList.getArticles());
			assertEquals(n, readingList.getArticles().size());
			assertEquals(true, checkArticlesForCategory(readingList, "News"));
			readingList = ArticleUtils.getCollectionByLabel(mToken, n, "News", 
					readingList.getContinuation());
			assertNotNull(readingList);
			assertNotNull(readingList.getArticles());
			assertEquals(n, readingList.getArticles().size());
		} catch (JSONException e) {
			Log.w(TAG, e.getMessage());
			assertTrue(false);
		}
    }
    
    public void testGetUnreadCount() {
    	CollectionList collectionList;
    	
    	try {
			collectionList = ArticleUtils.getUnreadCount(mToken);
			Log.v(TAG, collectionList.getCollectionListSize() + "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private boolean checkArticlesForCategory(Collection articleList, String cat) {
    	Map<String, Article> articles;
    	ArrayList<String> categories;
    	
    	articles = articleList.getArticles();
    	for(Article article : articles.values()) {
    		boolean doesMatch = false;
    		categories = article.getCategories();
    		for(String category : categories) {
    			if(category.contains(cat)) {
    				doesMatch = true;
    			}
    		}
    		if(!doesMatch) {
    			return false;
    		}
    	}
    	return true;
    }
}
