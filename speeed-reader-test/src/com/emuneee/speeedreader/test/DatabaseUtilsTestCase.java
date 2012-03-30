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

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;

import com.emuneee.speeedreader.AuthUtils;
import com.emuneee.speeedreader.SQLiteHelper;
import com.emuneee.speeedreader.activity.LoginActivity;
import com.emuneee.speeedreader.googlereader.Article;
import com.emuneee.speeedreader.googlereader.ArticleUtils;
import com.emuneee.speeedreader.googlereader.Collection;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

/**
 * @author Evan
 *
 */
public class DatabaseUtilsTestCase extends
		ActivityInstrumentationTestCase2<LoginActivity> {
	private final String TAG = getClass().getSimpleName();
	private Activity mActivity;
	private String mToken;
	
	public DatabaseUtilsTestCase() {
		super(LoginActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        mToken = AuthUtils.getAuthToken(mActivity, "emuneee@gmail.com", "reader");
    }
    
    public void testDatabaseIsCreated() {
    	SQLiteDatabase database;
    	SQLiteHelper helper;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	database = helper.getReadableDatabase();
    	assertNotNull(database);
    	assertEquals(true, database.isOpen());
    	database.close();
    }
    
    public void testInsertArticles() {
    	Collection readingList;
    	SQLiteHelper helper;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
	    	readingList = ArticleUtils.getCollectionByState(mToken, 25, "reading-list");
	    	assertEquals(true, helper.insertArticles(readingList.getArticles())); 
    	} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
    
    public void testGetArticlesByState() {
    	Map<String, Article> articles;
    	SQLiteHelper helper;
    	Collection readingList;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
    		readingList = ArticleUtils.getCollectionByState(mToken, 25, "reading-list");
    		helper.insertArticles(readingList.getArticles());
	    	articles = helper.getArticlesByCategory("state/com.google/reading-list", 5);
	    	assertNotNull(articles);
	    	assertEquals(true, articles.size() > 0);
		} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
    
    public void testGetStarredArticles() {
    	Map<String, Article> articles;
    	SQLiteHelper helper;
    	Collection readingList;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
    		readingList = ArticleUtils.getCollectionByState(mToken, 25, "starred");
    		helper.insertArticles(readingList.getArticles());
	    	articles = helper.getStarredArticles(5);
	    	assertNotNull(articles);
	    	assertEquals(true, articles.size() > 0);
		} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
    
    public void testInsertCollection() {
    	SQLiteHelper helper;
    	Collection readingList;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
    		readingList = ArticleUtils.getCollectionByState(mToken, 1, "starred");
    		assertEquals(true, helper.insertCollection(readingList));

		} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
    
    public void testUpdateCollection() {
    	SQLiteHelper helper;
    	Collection readingList;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
    		readingList = ArticleUtils.getCollectionByState(mToken, 1, "starred");
    		assertEquals(true, helper.insertCollection(readingList));
    		readingList.setContinuation("123456789");
    		assertEquals(true, helper.updateCollection(readingList));
		} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
    
    public void testDeleteCollection() {
    	SQLiteHelper helper;
    	Collection readingList;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
    		readingList = ArticleUtils.getCollectionByState(mToken, 1, "starred");
    		assertEquals(true, helper.insertCollection(readingList));
    		assertEquals(true, helper.deleteCollection(readingList.getId()));
    		assertEquals(true, ArticleUtils.getCollectionByState(mToken, 1, "starred") == null);
		} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
    
    public void testUpdateArticle() {
    	Map<String, Article> articles;
    	SQLiteHelper helper;
    	Collection readingList;
    	Article article;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
    		readingList = ArticleUtils.getCollectionByState(mToken, 1, "starred");
    		assertEquals(true, helper.insertArticles(readingList.getArticles()));
	    	articles = helper.getStarredArticles(1);
	    	article = articles.values().iterator().next();
	    	article.setTitle("THIS IS A TEST");
	    	assertEquals(true, helper.updateArticle(article));
	    	articles = helper.getStarredArticles(1);
	    	article = articles.values().iterator().next();
	    	assertEquals(true, article.getTitle().contentEquals("THIS IS A TEST"));
		} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
    
    public void testUpdateArticles() {
    	Map<String, Article> articles;
    	SQLiteHelper helper;
    	Collection readingList;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
    		readingList = ArticleUtils.getCollectionByState(mToken, 5, "starred");
    		assertEquals(true, helper.insertArticles(readingList.getArticles()));
	    	articles = helper.getStarredArticles(5);
	    	for(Article article : articles.values()) {
	    		article.setLikes(99);
	    	}
	    	assertEquals(true, helper.updateArticles(articles));
	    	articles = helper.getStarredArticles(5);
	    	for(Article article : articles.values()) {
	    		assertEquals(true, article.getLikes() == 99);
	    	}
		} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
    
    public void testDeleteArticle() {
    	final Map<String, Article> articles;
    	SQLiteHelper helper;
    	Collection readingList;
    	
    	helper = SQLiteHelper.getSQLiteHelper(mActivity);
    	helper.clearTables();
    	try {
    		readingList = ArticleUtils.getCollectionByState(mToken, 1, "starred");
    		assertEquals(true, helper.insertArticles(readingList.getArticles()));
	    	articles = helper.getStarredArticles(1);
	    	assertEquals(true, helper.deleteArticles(new ArrayList<String>() {{ 
	    		add(articles.values().iterator().next().getId()); }}));
	    	assertEquals(0, helper.getStarredArticles(1).size());
		} catch (JSONException e) {
			
		} finally {
			helper.clearTables();
			helper.close();
		}
    }
   
}
