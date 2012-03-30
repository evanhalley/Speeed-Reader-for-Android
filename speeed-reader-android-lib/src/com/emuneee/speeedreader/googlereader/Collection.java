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

import java.util.Map;

/**
 * A collection is a logical assortment of articles. The collectino could
 * represent a collection of starred articles, or the articles belonging to the
 * 'Android' label on a users Google Reader account
 * 
 * @author Evan
 * 
 */
public class Collection {
	public enum Type {
		State, 
		Label,
		Subscription
	}

	private String mId;
	private String mTitle;
	private int mUnreadCount;
	private Type mType;
	private String mContinuation;
	private long mUpdated;
	private Map<String, Article> mArticles;

	/**
	 * @return the articles
	 */
	public Map<String, Article> getArticles() {
		return mArticles;
	}

	/**
	 * @return the continuation
	 */
	public String getContinuation() {
		return mContinuation;
	}

	/**
	 * @param continuation the continuation to set
	 */
	public void setContinuation(String continuation) {
		mContinuation = continuation;
	}

	/**
	 * @return the updated
	 */
	public long getUpdated() {
		return mUpdated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(long updated) {
		mUpdated = updated;
	}

	/**
	 * @param articles the articles to set
	 */
	public void setArticles(Map<String, Article> articles) {
		mArticles = articles;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return mId;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		mId = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		mTitle = title;
	}

	/**
	 * @return the unreadCount
	 */
	public int getUnreadCount() {
		return mUnreadCount;
	}

	/**
	 * @param unreadCount
	 *            the unreadCount to set
	 */
	public void setUnreadCount(int unreadCount) {
		mUnreadCount = unreadCount;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return mType;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		mType = type;
	}
}
