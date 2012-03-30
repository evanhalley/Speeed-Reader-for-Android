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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.util.Log;

/**
 * @author Evan
 *
 */
public class HttpUtils {
	public final static String TAG = "HttpUtils";
	public static final String GDATA_VERSION = "3.0";
    private static final String GDATA_VERSION_HEADER = "GData-Version";
    private static final String GOOGLE_LOGIN_AUTH_HEADER = "GoogleLogin auth=";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static int TIMEOUT = 30000;
    private static int BUFFER_SIZE = 4096;
    
    /**
     * Returns a URL connection object tailor made for the Google APIs
     * @param urlString URL to connect to
     * @param authToken authorization token
     * @return 
     * @throws IOException
     */
	public static HttpURLConnection getUrlConnection(String urlString, String authToken) 
			throws IOException {
		Log.v(TAG, "New URL connection: " + urlString);
		final URL url;
		final HttpURLConnection urlConnection;

		url = new URL(urlString);
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty(GDATA_VERSION_HEADER, GDATA_VERSION);
		urlConnection.setRequestProperty(AUTHORIZATION_HEADER, 
				GOOGLE_LOGIN_AUTH_HEADER + authToken);;
		urlConnection.setConnectTimeout(TIMEOUT);
		urlConnection.setReadTimeout(TIMEOUT);
		return urlConnection;
	}
	
	/**
	 * Processes an HTTP request. Expects string data as the result
	 * @param request
	 * @return
	 */
	public static String processHttpRequest(HttpURLConnection urlConnection) throws IOException {
		final StringBuilder retVal;
		BufferedReader bReader = null;
		InputStreamReader iReader = null;
		String line;
		
		retVal = new StringBuilder();
		try {
			bReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()), 
					BUFFER_SIZE);
			while((line = bReader.readLine()) != null) {
				retVal.append(line);
			}
			Log.v(TAG, "Content Size: " + retVal.length());
			Log.v(TAG, "Response Code: " + urlConnection.getResponseCode());
		} catch(IOException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} finally {
			if(iReader != null) iReader.close();
			if(bReader != null) bReader.close();
			urlConnection.disconnect();
		}

		return retVal.toString();
	}
	
	/**
	 * Executes an HTTP request
	 * @param url URL to send the HTTP request
	 * @param token Google Reader API token
	 * @return String data as a result of the http request
	 * @throws IOException
	 */
	public static String executeHttpRequest(String url, String token) throws IOException {
		final StringBuilder retVal;
		HttpURLConnection urlConnection = null;
		BufferedReader bReader = null;
		InputStreamReader iReader = null;
		String line;
		
		retVal = new StringBuilder();
		try {
			urlConnection = HttpUtils.getUrlConnection(url, token);
			bReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()), 
					BUFFER_SIZE);
			while((line = bReader.readLine()) != null) {
				retVal.append(line);
			}
			Log.v(TAG, "Content Size: " + retVal.length());
			Log.v(TAG, "Response Code: " + urlConnection.getResponseCode());
		} catch(IOException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} finally {
			if(iReader != null) iReader.close();
			if(bReader != null) bReader.close();
			if(urlConnection != null) urlConnection.disconnect();
		}

		return retVal.toString();
	}
	
	/**
	 * Adds query strings to a URL
	 * @param url URL to add query strings to
	 * @param queryStrings query strings to add to the url
	 * @return URL with the query strings added
	 */
	public static String addQueryStrings(String url, Map<String, String> queryStrings) {
		final StringBuilder urlString;
		int index;
		
		urlString = new StringBuilder(url);
		index = 0;
		if(queryStrings == null || queryStrings.size() == 0) {
			return url;
		}
		urlString.append('?');
		for(String key : queryStrings.keySet()) {
			urlString.append(key).append('=');
			urlString.append(queryStrings.get(key));
			if(index + 1 < queryStrings.size()) {
				urlString.append('&');
			}
			index++;
		}
		
		return urlString.toString();
	}

}
