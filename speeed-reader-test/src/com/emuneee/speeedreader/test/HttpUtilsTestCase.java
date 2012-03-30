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
import java.util.HashMap;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.emuneee.speeedreader.AuthUtils;
import com.emuneee.speeedreader.HttpUtils;
import com.emuneee.speeedreader.activity.LoginActivity;

/**
 * @author Evan
 *
 */
public class HttpUtilsTestCase extends
		ActivityInstrumentationTestCase2<LoginActivity>{
	private final String TAG = getClass().getSimpleName();
	private final String ACCOUNT_NAME = "emuneee@gmail.com";  
	private Activity mActivity;
	private String mToken;
	
	public HttpUtilsTestCase() {
		super("com.emuneee.speeedreader", LoginActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	mActivity = this.getActivity(); 
    	mToken = AuthUtils.getAuthToken(mActivity, ACCOUNT_NAME, "reader");
    }
    
    /**
     * tests processing http request
     */
    public void testProcessHttpRequest() {
    	HttpURLConnection urlConnection;
    	String response;
    	
    	try {
			urlConnection = HttpUtils.getUrlConnection(
					"http://www.google.com/reader/api/0/stream/contents/user/-/state/com.google/reading-list", mToken);
			assertNotNull(urlConnection);
			response = HttpUtils.processHttpRequest(urlConnection);
			Log.v(TAG, response);
			assertNotNull(response);
			assertEquals(true, response.length() > 0);
		} catch (IOException e) {
			Log.w(TAG, e.getMessage());
		}
    }
    
    /**
     * test execute http request
     */
    public void testExecuteHttpRequest() {
    	String response;
    	
    	try {
			response = HttpUtils.executeHttpRequest(
					"http://www.google.com/reader/api/0/stream/contents/user/-/state/com.google/reading-list", mToken);
			Log.v(TAG, response);
			assertNotNull(response);
			assertEquals(true, response.length() > 0);
		} catch (IOException e) {
			Log.w(TAG, e.getMessage());
		}
    }
    
    /**
     * test we can add query strings to a url
     */
    public void testAddQueryStrings() {
    	String url;
    	final HashMap<String, String> queryStrings;
    	
    	queryStrings = new HashMap<String, String>(2);
    	queryStrings.put("Hello", "World");
    	queryStrings.put("Foo", "Bar");
    	url =  HttpUtils.addQueryStrings("http://google.com", queryStrings);
    	Log.v(TAG, url);
    	assertEquals("http://google.com?Foo=Bar&Hello=World", url);
    	
    	url =  HttpUtils.addQueryStrings("http://google.com", null);
    	assertEquals("http://google.com", url);
    }
}
